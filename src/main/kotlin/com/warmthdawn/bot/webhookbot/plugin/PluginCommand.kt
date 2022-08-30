package com.warmthdawn.bot.webhookbot.plugin

import com.warmthdawn.bot.webhookbot.core.WebHook
import com.warmthdawn.bot.webhookbot.core.WebHookType
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.contact.Group

/**
 *
 * @author WarmthDawn
 * @since 2021-06-27
 */
object PluginCommand : CompositeCommand(
    owner = PluginMain,
    primaryName = "webhook",
    description = "WebHook管理"
) {
    @SubCommand("add", "添加")
    suspend fun CommandSender.add(name: String, type: WebHookType) {
        val group = requireGroup() ?: return
        if (name in PluginConfig.hooks) {
            if (PluginConfig.hooks[name]?.type != type) {
                sendMessage("名为${name}的WebHook已经存在且类型为${PluginConfig.hooks[name]?.type}, 添加失败")
                return
            }
            PluginConfig.groups[group.id].add(name)
            sendMessage("名为${name}的WebHook已经存在，将本群加入消息通知")
            info(name)
            return
        }
        PluginConfig.hooks[name] = WebHook(name, type)
        PluginConfig.groups[group.id].add(name)
        sendMessage("成功创建名为${name}的WebHook并将本群加入消息通知")
        info(name)

    }


    @SubCommand("remove", "删除")
    suspend fun CommandSender.remove(name: String) {
        val group = requireGroup() ?: return
        PluginConfig.groups[group.id].remove(name)
        if (PluginConfig.groups[group.id].isEmpty()) {
            PluginConfig.hooks.remove(name)
            sendMessage("成功删除名为$name 的WebHook")
            return
        }
        sendMessage("将本群移出$name 的消息通知")
    }

    @SubCommand("restart", "重启")
    suspend fun CommandSender.restart() {
        PluginMain.server.restart()
        sendMessage("重启完成， 服务器在端口: ${PluginMain.server.port} 开放")
    }


    @SubCommand("info", "信息")
    suspend fun CommandSender.info(name: String = "") {
        val group = getGroupOrNull()?.id
        val available = PluginConfig.groups[group]
        val rootUrl = PluginConfig.rootUrl
        val msg = buildString {
            appendLine("当前WebHook地址为")
            PluginConfig.hooks.values.asSequence()
                .filter {
                    group == null || available?.contains(it.name) == true
                }
                .filter {
                    it.name == name || name.isEmpty()
                }
                .map {
                    "${it.name} ${it.type}: $rootUrl/webhooks/general/${it.name}"
                }
                .forEach {
                    appendLine(it)
                }
        }
        sendMessage(msg)
    }

    @SubCommand("bindRepo", "绑定仓库")
    suspend fun CommandSender.bindRepo(name: String = "") {
        val group = requireGroup()?.id ?: return
        PluginConfig.repos[group] = name
        sendMessage("成功绑定仓库$name")
    }

    private suspend fun CommandSender.requireGroup(): Group? {
        val group = getGroupOrNull()
        if (group == null) {
            sendMessage("本条消息只能在群配置.")
            return group
        }
        return group
    }
}