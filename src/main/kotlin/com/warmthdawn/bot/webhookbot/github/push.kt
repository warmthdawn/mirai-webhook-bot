package com.warmthdawn.bot.webhookbot.github

import com.warmthdawn.bot.webhookbot.util.*


data class GithubCommit(
    val hash: String,
    val message: String,
    val url: String,
    val author: CommitAuthor,
    val addedNumber: Int,
    val removedNumber: Int,
    val modifiedNumber: Int,
) {
    fun toFormattedString() = "${author.username} ${hash.substring(0, 6)}: ✏$modifiedNumber ,➕$addedNumber ,🗑$removedNumber\n$message"
}


data class CommitAuthor(
    val name: String,
    val email: String,
    val username: String,
)


data class GithubPush(
    val ref: String,
    val before: String,
    val created: Boolean,
    val deleted: Boolean,
    val headCommit: GithubCommit,
    val commits: List<GithubCommit>,
) {
    fun generateMessage(common: GithubCommon): String {
        val headCommitString = EmojiUtils.processCommitMessage(headCommit.toFormattedString())
        if (ref.startsWith("refs/tags/")) {
            val tag = ref.substring("refs/tags/".length)
            return buildTagMessage(common.sender.login, common.repository!!.full_name, tag, headCommitString)
        }

        val commitsLine = commits.map {
            EmojiUtils.processCommitMessage(it.toFormattedString())
        }
        val branch = if (ref.startsWith("refs/heads/")) {
            ref.substring("refs/heads/".length)
        } else {
            ":$ref"
        }
        return buildCommitMessage(
            common.sender.login,
            common.repository!!.full_name,
            branch,
            commitsLine,
            headCommitString
        )
    }
}

fun parseCommit(json: JsonNode): GithubCommit {
    return GithubCommit(
        hash = json["id"].content,
        message = json["message"].content,
        url = json["url"].content,
        author = CommitAuthor(
            name = json["author"]["name"].content,
            email = json["author"]["email"].content,
            username = json["author"]["username"].content,
        ),
        addedNumber = json["added"].size,
        removedNumber = json["removed"].size,
        modifiedNumber = json["modified"].size
    )
}

fun parsePush(json: JsonNode): GithubPush {
    return GithubPush(
        ref = json["ref"].content,
        before = json["before"].content,
        created = json["created"].boolean,
        deleted = json["deleted"].boolean,
        headCommit = parseCommit(json["head_commit"]),
        commits = json["commits"].map { parseCommit(it) }
    )
}