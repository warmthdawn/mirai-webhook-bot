package com.warmthdawn.bot.webhookbot.github

import com.warmthdawn.bot.webhookbot.core.IWebHookProcessor
import com.warmthdawn.bot.webhookbot.util.*
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
        val signature = request.headers["X-Hub-Signature-256"]
        return "sha256=" + calcSignature(payload, secret)?.toHex() == signature
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