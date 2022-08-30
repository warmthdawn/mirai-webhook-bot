package com.warmthdawn.bot.webhookbot.github

import com.warmthdawn.bot.webhookbot.util.JsonNode
import com.warmthdawn.bot.webhookbot.util.content
import com.warmthdawn.bot.webhookbot.util.long

data class GithubCommon(
    val sender: GithubUser,
    val action: String,
    val repository: GithubRepository?,
)

data class GithubUser(
    val id: Long,
    val login: String,
    val name: String,
)

data class GithubRepository(
    val id: Long,
    val name: String,
    val full_name: String,
)



fun parseCommon(json: JsonNode): GithubCommon {
    return GithubCommon(
        sender = parseUser(json["sender"]),
        action = json["action"].content,
        repository = if (json.contains("repository")) {
            GithubRepository(
                id = json["repository"]["id"].long,
                name = json["repository"]["name"].content,
                full_name = json["repository"]["full_name"].content
            )
        } else {
            null
        }
    )
}

fun parseUser(json: JsonNode): GithubUser {
    return GithubUser(
        id = json["id"].long,
        login = json["login"].content,
        name = json["name"].content,
    )
}

