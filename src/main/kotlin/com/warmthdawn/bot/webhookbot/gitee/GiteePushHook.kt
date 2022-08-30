package com.warmthdawn.bot.webhookbot.gitee

import com.warmthdawn.bot.webhookbot.core.IWebHookProcessor
import com.warmthdawn.bot.webhookbot.plugin.PluginMain
import com.warmthdawn.bot.webhookbot.util.*
import com.warmthdawn.bot.webhookbot.util.EmojiUtils.processCommitMessage
import io.ktor.request.*
import kotlinx.serialization.json.Json
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.toPlainText
import java.util.*

/**
 *
 * @author WarmthDawn
 * @since 2021-06-28
 */
object GiteePushHook : IWebHookProcessor {
    private val logger =  PluginMain.logger
    override fun process(payloadString: String, request: ApplicationRequest): Message {
        val payload = Json
            .parseToJsonElement(payloadString)
            .node

        val pusher = payload["sender"]["name"].content
        val repository = payload["repository"]["full_name"].content
        val ref = payload["ref"].content

        val headCommit = parseCommit(payload["head_commit"])
        val commits = payload["commits"].map {
            processCommitMessage(parseCommit(it))
        }
        if (ref.startsWith("refs/tags/")) {
            val tag = ref.substring("refs/tags/".length)
            return buildTagMessage(pusher, repository, tag, headCommit).toPlainText()
        }
        var branch = ":$ref"
        if (ref.startsWith("refs/heads/")) {
            branch = ref.substring("refs/heads/".length)
        }

        return buildCommitMessage(pusher, repository, branch, commits, headCommit).toPlainText()
    }

    private fun parseCommit(it: JsonNode): String {
        val id = it["id"].content.substring(0, 6)
        val author = it["author"]["name"].content
        val message = it["message"].content
        val added = it["added"].size
        val modified = it["modified"].size
        val removed = it["removed"].size

        return "$author $id: ✏$modified ,➕$added ,🗑$removed\n$message"
    }

    override fun validate(payload: String, secret: String, request: ApplicationRequest): Boolean {
        val signature = request.headers["X-Gitee-Token"]
        val timestamp = request.headers["X-Gitee-Timestamp"]

        if (System.currentTimeMillis() - (timestamp?.toLongOrNull() ?: 0) > 1000 * 60 * 60) {
            return false
        }

        val stringToSign = timestamp + "\n" + secret
        val signData = calcSignature(stringToSign, secret)
        val result = Base64.getEncoder().encodeToString(signData)

        return if (result == signature) {
            true
        } else {
            logger.warning("校验失败：signature=$signature, result=$result")
            false
        }
    }

}