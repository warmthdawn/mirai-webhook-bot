package com.warmthdawn.bot.webhookbot.github

import com.warmthdawn.bot.webhookbot.core.IWebHookProcessor
import com.warmthdawn.bot.webhookbot.plugin.PluginMain
import com.warmthdawn.bot.webhookbot.util.*
import io.ktor.request.*
import kotlinx.serialization.json.Json

/**
 *
 * @author WarmthDawn
 * @since 2021-06-27
 */
object GitlabPushHook : IWebHookProcessor {
    private val logger =  PluginMain.logger
    override fun parse(payloadString: String): String {
        val payload = Json
            .parseToJsonElement(payloadString)
            .node

        val repository = payload["repository"]["name"].content
        val ref = payload["ref"].content
        val pusher = payload["user_name"].content

        val commits = payload["commits"].map {
            EmojiUtils.processCommitMessage(parseCommit(it))
        }

        if (ref.startsWith("refs/tags/")) {
            val tag = ref.substring("refs/tags/".length)
            return buildTagMessage(pusher, repository, tag)
        }


        var branch = ":$ref"
        if (ref.startsWith("refs/heads/")) {
            branch = ref.substring("refs/heads/".length)
        }

        return buildCommitMessage(pusher = pusher, repository = repository, branch = branch, commits = commits)
    }


    override fun validate(payload: String, secret: String, request: ApplicationRequest): Boolean {
        val signature = request.headers["X-Gitlab-Token"]
        logger.info("验证 signature=$signature")
        return signature == secret
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