package com.warmthdawn.bot.webhookbot.plugin

import com.warmthdawn.bot.webhookbot.core.WebHook
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.PluginDataExtensions.withEmptyDefault

/**
 *
 * @author WarmthDawn
 * @since 2021-06-27
 */


object PluginConfig : AutoSavePluginConfig("webhook") {
    val port: Int by value(8022)
    val rootUrl: String by value("http://localhost:8022")
    val secretKey: String by value("")
    val tokens: MutableMap<String, String> by value()
    var qq: Long by value(-1L)
    val hooks: MutableMap<String, WebHook> by value()
    val groups by value<MutableMap<Long, MutableSet<String>>>().withEmptyDefault()
    val repos: MutableMap<Long, String> by value()
}




