package com.warmthdawn.bot.webhookbot.util

/**
 *
 * @author WarmthDawn
 * @since 2021-06-28
 */

fun buildTagMessage(
    pusher: String,
    repository: String,
    tag: String,
    headCommit: String = "",
): String {
    return buildString {
        append("|📌|")
        append(pusher).append("在").append(repository)
        append("创建了一个新的标签: ").append(tag)
        if (headCommit.isNotEmpty()) {
            appendLine("当前最新commit: ")
            appendLine(headCommit)
        }
    }
}


fun buildCommitMessage(
    pusher: String = "",
    repository: String,
    branch: String,
    commits: List<String> = emptyList(),
    headCommit: String = "",
): String {
    if (commits.isNotEmpty()) {
        return buildString {
            append("|🔨|").append(repository).append("/").appendLine(branch)
            append("有")
            append(commits.size)
            append("条新的commit(s)")
            appendLine()
            commits.forEach {
                appendLine(it)
            }
        }
    }
    return buildString {
        append("|📣|")
        append(pusher).append("在").append(repository).appendLine(branch)
        appendLine("提交了一个新的push, 但是却没有包含任何一个commit")
        if (headCommit.isNotEmpty()) {
            appendLine("当前最新commit: ")
            appendLine(headCommit)
        }
    }
}