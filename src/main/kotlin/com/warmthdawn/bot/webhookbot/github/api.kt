package com.warmthdawn.bot.webhookbot.github

import com.warmthdawn.bot.webhookbot.plugin.GithubToken
import com.warmthdawn.bot.webhookbot.plugin.PluginConfig
import com.warmthdawn.bot.webhookbot.plugin.PluginData
import com.warmthdawn.bot.webhookbot.plugin.PluginMain
import com.warmthdawn.bot.webhookbot.util.content
import com.warmthdawn.bot.webhookbot.util.node
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import net.mamoe.mirai.console.util.retryCatching
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent


private val client = HttpClient(CIO)

enum class Status {
    SUCCESS,
    UNAUTHORIZED,
    FAILURE,
}

suspend fun replyIssue(group: Long?, repo: String, issue: Int, comment: String): Status {
    val url = "https://api.github.com/repos/$repo/issues/$issue/comments"

    try {
        val result = client.post<String> {
            url(url)
            header("Authorization", authHeader(group))
            header("Content-Type", "application/json")


            body = buildJsonObject {
                put("body", comment)
            }.toString()
        }

        PluginMain.logger.info("Reply issue result: $result")
        return Status.SUCCESS

    } catch (e: Exception) {
        if (e is ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                return Status.UNAUTHORIZED
            }
        }
        PluginMain.logger.error("Reply issue failed", e)
        return Status.FAILURE
    }


}

fun authHeader(group: Long?): String {
    val tokenObj = if (group == null) {
        PluginData.default
    } else {
        PluginData.tokens[group]
    }
    val token = tokenObj?.token
    val tokenType = tokenObj?.tokenType
    return "$tokenType $token"
}

suspend fun checkToken(group: Long?): Boolean {
    val tokenObj = if (group == null) {
        PluginData.default
    } else {
        PluginData.tokens[group]
    }
    val token = tokenObj?.token
    val tokenType = tokenObj?.tokenType

    if (token.isNullOrEmpty() || tokenType.isNullOrBlank()) {
        return false
    }
    val url = "https://api.github.com"
    return try {
        client.get<String> {
            url(url)
            header("Authorization", "$tokenType $token")
        }
        true
    } catch (e: Exception) {
        PluginMain.logger.error("Check token failed", e)
        false
    }
}


suspend fun githubOAuth(subject: Contact, group: Long?) {
    val clientId = PluginConfig.clientId

    if (clientId.isEmpty()) {
        subject.sendMessage("请先配置clientId")
        return
    }

    try {

        PluginMain.logger.info("请求github oauth")
        // 1. get device code
        val url = "https://github.com/login/device/code"

        val deviceCodeResult = retryCatching(3) {
            client.post<String> {
                url(url)
                header("Accept", "application/json")
                parameter("client_id", clientId)
                parameter("scope", "repo, user, project, gist, write:discussion")
            }
        }
        PluginMain.logger.info("device code result: $deviceCodeResult")

        val jsonDeviceCode = Json.parseToJsonElement(deviceCodeResult.getOrThrow()).node
        val deviceCode = jsonDeviceCode["device_code"].content
        val userCode = jsonDeviceCode["user_code"].content
        val verificationUrl = jsonDeviceCode["verification_uri"].content
        val interval = jsonDeviceCode["interval"].content.toInt()

        // send to users
        val message = "请在浏览器中打开以下链接，并输入$userCode：\n$verificationUrl"
        PluginMain.logger.info(message)
        subject.sendMessage(message)

        // 2. get access token

        for (i in 0 until 10) {
            delay(interval * 1200L)
            PluginMain.logger.info("第 $i 次获取access token")
            try {


                val tokenResult =
                    client.post<String> {
                        url("https://github.com/login/oauth/access_token")
                        header("Accept", "application/json")
                        parameter("client_id", clientId)
                        parameter("device_code", deviceCode)
                        parameter("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
                    }


                val jsonToken = Json.parseToJsonElement(tokenResult).node
                val accessToken = jsonToken["access_token"].content
                val tokenType = jsonToken["token_type"].content

                if (group == null) {
                    PluginData.default.token = accessToken
                    PluginData.default.tokenType = tokenType
                } else {
                    PluginData.tokens[group] = GithubToken(tokenType, accessToken)
                }

                if (checkToken(group)) {
                    PluginMain.logger.info("Get access token success: $accessToken")
                    subject.sendMessage("获取access token成功")
                    break
                } else {
                    PluginMain.logger.info("Get access token failed")
                    subject.sendMessage("获取access token失败")
                }
            } catch (e: Exception) {
                PluginMain.logger.error("获取token失败", e)
            }
        }
    } catch (e: Exception) {
        PluginMain.logger.error("Github oauth failed", e)
        subject.sendMessage("获取access token失败 ${e.message}")
    }
}