package org.mirai.qqBotMirai

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText

object BattleStatus : AutoSavePluginData("BattleStatus") {
    var battleStatusMap: MutableMap<Long, MutableMap<String, Int>> by value()
}
object CommandBattle : CompositeCommand(PluginMain, "battle", "bat", "fight", description = "战斗相关指令") {
    @SubCommand("start", "begin", "st", "s")
    suspend fun CommandSender.start() {
        this.getGroupOrNull()?.id?.let {
            BattleStatus.battleStatusMap[it] = HashMap()
            BattleStatus.battleStatusMap[it]?.set("turn", 0)
            BattleStatus.battleStatusMap[it]?.set("round", 0)
            val initList = getInitiativeSortedList(it)
            if(initList.isEmpty()){
                this.sendMessage("未找到先攻信息")
                return
            }
            this.sendMessage("战斗开始！")
            val nextUserID = initList[BattleStatus.battleStatusMap[it]?.get("turn")!!].second.userID
            val nextUserName = initList[BattleStatus.battleStatusMap[it]?.get("turn")!!].first
            this.sendMessage(PlainText("轮到$nextUserName（") + At(nextUserID) + PlainText("）的回合"))
        }

    }

    @SubCommand("over", "o", "end")
    suspend fun CommandSender.over() {
        this.getGroupOrNull()?.id?.let {
            this.sendMessage("战斗结束！共进行了" + (BattleStatus.battleStatusMap[it]?.get("round")?.plus(1)).toString() + "轮")
            BattleStatus.battleStatusMap[it] = HashMap();
        }
    }
}

object CommandBattleTurn : SimpleCommand(PluginMain, "end", "turn", description = "回合结束") {
    @Handler
    suspend fun CommandSender.turn() {
        this.getGroupOrNull()?.id?.let {
            val battleStats = BattleStatus.battleStatusMap[it]
            val initList = getInitiativeSortedList(it)
            val currName = initList[battleStats?.get("turn")!!].first
            battleStats["turn"] = battleStats["turn"]?.plus(1) ?: -1
            if(battleStats["turn"]!! >= initList.size){
                battleStats["turn"] = 0
                battleStats["round"] = battleStats["round"]?.plus(1) ?: -1
            }
            val nextUserName = initList[battleStats["turn"]!!].first
            val nextUserID = initList[battleStats["turn"]!!].second.userID
            this.sendMessage(currName + "的回合结束")
            this.sendMessage(PlainText("轮到$nextUserName（") + At(nextUserID) + PlainText("）的回合"))
            return
        }
        this.sendMessage("未检测到战斗")
    }
}