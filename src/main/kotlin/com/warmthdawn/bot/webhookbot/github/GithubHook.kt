package com.warmthdawn.bot.webhookbot.github

import com.warmthdawn.bot.webhookbot.core.IWebHookProcessor
import com.warmthdawn.bot.webhookbot.plugin.PluginConfig
import com.warmthdawn.bot.webhookbot.plugin.PluginMain
import com.warmthdawn.bot.webhookbot.util.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.request.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.toPlainText

/**
 *
 * @author WarmthDawn
 * @since 2021-06-27
 */
object GithubHook : IWebHookProcessor {
    override fun process(payloadString: String, request: ApplicationRequest): Message? {
        val payload = Json
            .parseToJsonElement(payloadString)
            .node

        val event = request.headers["X-GitHub-Event"]!!
        val common = parseCommon(payload)

        return when (event) {
            "push" -> parsePush(payload).generateMessage(common).toPlainText()
            "issues" -> processIssue(payload, common)?.toPlainText()
            "issue_comment" -> processIssueComment(payload, common)?.toPlainText()
            else -> null
        }
    }


    override fun validate(payload: String, secret: String, request: ApplicationRequest): Boolean {
        val signature = request.headers["X-Hub-Signature-256"]
        return "sha256=" + calcSignature(payload, secret)?.toHex() == signature
    }

}