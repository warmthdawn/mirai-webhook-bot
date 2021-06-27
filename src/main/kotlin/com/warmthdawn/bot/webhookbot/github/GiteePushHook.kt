package com.warmthdawn.bot.webhookbot.github

import com.warmthdawn.bot.webhookbot.core.IWebHookProcessor
import com.warmthdawn.bot.webhookbot.util.*
import io.ktor.request.*
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

/**
 *
 * @author WarmthDawn
 * @since 2021-06-28
 */
object GiteePushHook : IWebHookProcessor {
    override fun parse(payloadString: String): String {
        val payload = Json
            .parseToJsonElement(payloadString)
            .node

        val pusher = payload["sender"]["name"].content
        val repository = payload["repository"]["full_name"].content
        val ref = payload["ref"].content

        val headCommit = parseCommit(payload["head_commit"])
        val commits = payload["commits"].map {
            parseCommit(it)
        }
        if (ref.startsWith("refs/tags/")) {
            val tag = ref.substring("refs/tags/".length)
            return buildTagMessage(pusher, repository, tag, headCommit)
        }
        var branch = ":$ref"
        if (ref.startsWith("refs/heads/")) {
            branch = ref.substring("refs/heads/".length)
        }

        return buildCommitMessage(pusher, repository, branch, commits, headCommit)
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
        val result = URLEncoder.encode(Base64.getEncoder().encodeToString(signData), StandardCharsets.UTF_8)
        return result == signature
    }

}