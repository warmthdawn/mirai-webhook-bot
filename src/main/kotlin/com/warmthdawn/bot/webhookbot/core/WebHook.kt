package com.warmthdawn.bot.webhookbot.core

import com.warmthdawn.bot.webhookbot.gitee.GiteePushHook
import com.warmthdawn.bot.webhookbot.github.GithubHook
import com.warmthdawn.bot.webhookbot.gitlab.GitlabPushHook
import kotlinx.serialization.Serializable

/**
 *
 * @author WarmthDawn
 * @since 2021-06-27
 */
@Serializable
enum class WebHookType {
    Github {
        override val processor: IWebHookProcessor
            get() = GithubHook
    },
    Gitlab {
        override val processor: IWebHookProcessor
            get() = GitlabPushHook
    },
    Gitee {
        override val processor: IWebHookProcessor
            get() = GiteePushHook
    };


    abstract val processor: IWebHookProcessor
}


@Serializable
data class WebHook(
    val name: String,
    val type: WebHookType,
)


