package org.mirai.qqBotMirai

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.nameCardOrNick

object CommandRename : SimpleCommand(PluginMain, "nn", description = "rename") {
    @Handler
    suspend fun CommandSender.onCommand(name: String) {
        UserDataList.users[this.user?.id]?.username = this.user?.nameCardOrNick.toString()
        UserDataList.users[this.user?.id]?.nickname = name
        this.sendMessage("已设置角色名：$name")
    }
}