package com.warmthdawn.bot.webhookbot.github

import com.warmthdawn.bot.webhookbot.util.JsonNode
import com.warmthdawn.bot.webhookbot.util.content
import com.warmthdawn.bot.webhookbot.core.IWebHookProcessor
import com.warmthdawn.bot.webhookbot.util.node
import com.warmthdawn.bot.webhookbot.util.buildCommitMessage
import com.warmthdawn.bot.webhookbot.util.buildTagMessage
import com.warmthdawn.bot.webhookbot.util.verifySignature
import io.ktor.request.*
import kotlinx.serialization.json.Json

/**
 *
 * @author WarmthDawn
 * @since 2021-06-27
 */
object GithubPushHook : IWebHookProcessor {
    override fun parse(payloadString: String): String {
        val payload = Json
            .parseToJsonElement(payloadString)
            .node

        val repository = payload["repository"]["full_name"].content
        val ref = payload["ref"].content
        val pusher = payload["pusher"]["name"].content

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


    override fun validate(payload: String, secret: String, request: ApplicationRequest): Boolean {
        var signature = request.headers["X-Hub-Signature-256"]
        if(signature != null && signature.startsWith("sha256=")) {
            signature = signature.substring("sha256=".length)
        }

        return verifySignature(signature, payload, secret)
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
}