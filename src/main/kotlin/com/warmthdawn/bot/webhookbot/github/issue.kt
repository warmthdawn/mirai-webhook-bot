package com.warmthdawn.bot.webhookbot.github

import com.warmthdawn.bot.webhookbot.util.JsonNode
import com.warmthdawn.bot.webhookbot.util.content
import com.warmthdawn.bot.webhookbot.util.int
import com.warmthdawn.bot.webhookbot.util.long

data class GithubIssue(
    val id: Long,
    val url: String,
    val title: String,
    val body: String,
    val number: Int,
)

data class GithubIssueComment(
    val id: Long,
    val url: String,
    val body: String,
    val user: GithubUser,
)


fun parseIssue(json: JsonNode): GithubIssue {
    return GithubIssue(
        id = json["id"].long,
        url = json["url"].content,
        title = json["title"].content,
        body = json["body"].content,
        number = json["number"].int,
    )
}

fun parseIssueComment(json: JsonNode): GithubIssueComment {
    return GithubIssueComment(
        id = json["id"].long,
        url = json["url"].content,
        body = json["body"].content,
        user = parseUser(json["user"]),
    )
}

fun processIssue(json: JsonNode, common: GithubCommon): String? {
    val issue = parseIssue(json["issue"])

    val sender = common.sender.name
    val repositoryName = common.repository!!.full_name

    return when(common.action) {
        "opened" -> buildString {
            val body = if(issue.body.length > 40) {
                issue.body.substring(0, 20) + "..."
            } else {
                issue.body
            }

            val title = if(issue.title.length > 20) {
                issue.title.substring(0, 20) + "..."
            } else {
                issue.title
            }
            append("|❓|")
            append(sender).append("创建了一个新的issue")
            append("(")
            append(repositoryName).append("/")
            append("#").append(issue.number)
            appendLine(")")
            append("|链接|").appendLine(issue.url)
            append("|标题|").appendLine(title)
            appendLine(body)
        }
        "closed" -> buildString {

            val title = if(issue.title.length > 20) {
                issue.title.substring(0, 20) + "..."
            } else {
                issue.title
            }
            append("|❌|")
            append(sender).append("关闭了一个issue")
            append("(")
            append(repositoryName).append("/")
            append("#").append(issue.number)
            appendLine(")")
            append("|链接|").appendLine(issue.url)
            append("|标题|").appendLine(title)
            appendLine(issue.body)
        }
        "reopened" -> buildString {
            val title = if(issue.title.length > 20) {
                issue.title.substring(0, 20) + "..."
            } else {
                issue.title
            }
            append("|🔓|")
            append(sender).append("重新打开了issue")
            append("(")
            append(repositoryName).append("/")
            append("#").append(issue.number)
            appendLine(")")
            append("|链接|").appendLine(issue.url)
            append("|标题|").appendLine(title)
        }

        else -> null
    }

}

fun processIssueComment(json: JsonNode, common: GithubCommon): String? {
    val comment = parseIssueComment(json["comment"])

    val sender = common.sender.name
    val repositoryName = common.repository!!.full_name

    return when(common.action) {
        "created" -> buildString {
            val body = if(comment.body.length > 40) {
                comment.body.substring(0, 20) + "..."
            } else {
                comment.body
            }
            append("|💬|")
            append(sender).append("在issue中发表了评论")
            append("(")
            append(repositoryName).append("/")
            append("#").append(json["issue"]["number"].int)
            appendLine(")")
            append("|链接|").appendLine(comment.url)
            appendLine(body)
        }
        else -> null
    }
}