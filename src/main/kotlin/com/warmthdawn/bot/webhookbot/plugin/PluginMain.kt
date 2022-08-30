package com.warmthdawn.bot.webhookbot.plugin

import com.warmthdawn.bot.webhookbot.core.IServerEventHandler
import com.warmthdawn.bot.webhookbot.core.WebhookServer
import com.warmthdawn.bot.webhookbot.github.GithubHook
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*

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
            val channel = globalEventChannel()
            channel.subscribeOnce<BotOnlineEvent> {
                if (PluginConfig.qq == -1L) {
                    PluginConfig.qq = this.bot.id
                    logger.info("Webhook: 初始化机器人账号为 ${this.bot.id}")
                }
                if (this.bot.id == PluginConfig.qq) {
                    server.start()
                }
            }


            val cmdReply = Regex("^(回复|reply)\\s*#(\\d+)\\s*(.+)$", RegexOption.DOT_MATCHES_ALL)
            val cmdReplyQuote = Regex("^(转发|回复|reply)\\s*#(\\d+)")
            channel.subscribeAlways<GroupMessageEvent> {
                val group = this.group.id
                val repo = PluginConfig.repos[group]


                if (repo != null) {
                    val quote = message.findIsInstance<QuoteReply>()
                    if (quote != null) {
                        val content = message.contentToString()
                        val match = cmdReplyQuote.matchEntire(content)
                        if (match != null) {
                            val (_, id) = match.destructured
                            val comment = quote.source.contentToString()

                            val fromId = quote.source.fromId
                            val senderName = this.group[fromId]?.nameCardOrNick ?: "未知用户"

                            val reply = "转发自群 ${this.group.id} 的回复: \n" +
                                    "转发者：${this.sender.nameCardOrNick}(${this.sender.id}) \n" +
                                    "回复者: ${senderName}($fromId)\n\n" +
                                    comment;

                            val success = GithubHook.replyIssue(repo, id.toInt(), reply)
                            if (success) {
                                this.group.sendMessage("成功转发回复")
                            } else {
                                this.group.sendMessage("转发回复失败")
                            }
                        }
                    } else {
                        val content = message.contentToString()
                        val match = cmdReply.matchEntire(content)
                        if (match != null) {
                            val (_, id, comment) = match.destructured
                            val reply = "来自群 ${this.group.id} 的回复: \n" +
                                    "回复者: ${this.sender.nameCardOrNick}(${this.sender.id})\n\n" +
                                    comment
                            val success = GithubHook.replyIssue(repo, id.toInt(), reply)
                            if (success) {
                                this.group.sendMessage("成功回复")
                            } else {
                                this.group.sendMessage("回复失败")
                            }
                        }
                    }
                }

            }

        }


    }

    override suspend fun onHookResult(hookName: String, result: Any): Boolean {
        logger.info("收到hook结果$result")
        val bot = Bot.findInstance(PluginConfig.qq) ?: return false
        PluginConfig.groups.forEach { (group, hooks) ->
            if (hookName in hooks) {
                bot.getGroup(group)?.sendMessage(if(result is Message) result else result.toString().toPlainText())
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