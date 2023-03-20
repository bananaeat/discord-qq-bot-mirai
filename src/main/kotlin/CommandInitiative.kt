package org.mirai.qqBotMirai

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull

object CommandInitiative : CompositeCommand(PluginMain, "init", description = "先攻相关指令") {
    @SubCommand("list")
    suspend fun CommandSender.list() {
        val initList = this.getGroupOrNull()?.id?.let { getInitiativeSortedList(it) }
        if (initList != null) {
            if(initList.isEmpty()){
                this.sendMessage("未发现先攻信息。")
                return
            }
        } else {
            this.sendMessage("不在一个群中。")
        }
        var replyMessage = "先攻列表：\n"
        var index = 0
        if (initList != null) {
            for(pair in initList){
                index ++
                replyMessage += index.toString() + ". " + pair.first + " 先攻：" + pair.second.initValue + "\n"
            }
            this.sendMessage(replyMessage)
        } else {
            this.sendMessage("不在一个群中。")
        }
    }

    @SubCommand("clear", "clr", "del")
    suspend fun CommandSender.clear() {
        this.getGroupOrNull()?.id?.let { clearInitiative(it); this.sendMessage("先攻信息已经清除！") }
    }
}