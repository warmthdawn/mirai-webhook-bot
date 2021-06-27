package com.warmthdawn.bot.webhookbot.plugin

import com.warmthdawn.bot.webhookbot.core.IServerEventHandler
import com.warmthdawn.bot.webhookbot.core.WebhookServer
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.globalEventChannel

/**
 *
 * @author WarmthDawn
 * @since 2021-06-27
 */
object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.warmthdawn.bot.github-webhook-bot",
        version = "1.0-SNAPSHOT",
    )
), IServerEventHandler {
    lateinit var server: WebhookServer
        private set

    override fun onEnable() {
        PluginConfig.reload()
        PluginCommand.register()
        if (!::server.isInitialized || server.port != PluginConfig.port) {
            server = WebhookServer(PluginConfig.port, this.coroutineContext, this)
        }
        server.secretKey = PluginConfig.secretKey
        launch {
            globalEventChannel().subscribeOnce<BotOnlineEvent> {
                if (PluginConfig.qq == -1L) {
                    PluginConfig.qq = this.bot.id
                    logger.info("Webhook: 初始化机器人账号为 ${this.bot.id}")
                }
                if (this.bot.id == PluginConfig.qq) {
                    server.start()
                }
            }
        }


    }

    override suspend fun onHookResult(hookName: String, result: String): Boolean {
        logger.info("收到hook结果$result")
        val bot = Bot.findInstance(PluginConfig.qq) ?: return false
        PluginConfig.groups.forEach { (group, hooks) ->
            if (hookName in hooks) {
                bot.getGroup(group)?.sendMessage(result)
            }
        }
        return true
    }

    override suspend fun onServerStarted() {
        logger.info("已经在端口${server.port}开启WebHook监听服务器")
    }


    override suspend fun onServerStopped() {
        logger.info("WebHook监听服务器已关闭")
    }

    override fun onDisable() {
        PluginCommand.unregister()
        launch {
            server.stop()
        }
    }
}