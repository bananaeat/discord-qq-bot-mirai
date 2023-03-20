package org.mirai.qqBotMirai

import com.bernardomg.tabletop.dice.interpreter.DiceRoller
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.nameCardOrNick

@Serializable // kotlinx.serialization.Serializable
data class InitiativeEntry(var initValue: Int, var userID: Long)
object InitiativeList : AutoSavePluginData("InitiativeList") {
    var initiative: MutableMap<Long, MutableMap<String, InitiativeEntry>> by value()
}

public fun getInitiative(groupID: Long, name: String): Int{
    return InitiativeList.initiative[groupID]?.get(name)?.initValue ?: -1
}

public fun getInitiativeSortedList(groupID: Long): List<Pair<String, InitiativeEntry>>{
    val init = InitiativeList.initiative[groupID]?.toList() ?: emptyList()
    init.sortedByDescending { it.second.initValue }
    return init
}

public fun setInitiative(groupID: Long, userID: Long, value: Int, name: String){
    if(groupID !in InitiativeList.initiative){
        InitiativeList.initiative[groupID] = HashMap()
    }
    val initMap = InitiativeList.initiative[groupID]
    if (initMap != null) {
        if(name !in initMap){
            initMap[name] = InitiativeEntry(value, userID)
        } else {
            initMap[name]?.userID = userID
            initMap[name]?.initValue = value
        }
    }
}
public fun clearInitiative(groupID: Long){
    InitiativeList.initiative[groupID] = HashMap()
}
object CommandRollInitiative : SimpleCommand(PluginMain, "ri", description = "投掷先攻") {
    @Handler
    suspend fun CommandSender.onCommand(diceExpr: String, diceTarget: String? = null) {
        val parser = DefaultDiceParser();
        val roller = DiceRoller();
        val rolls = parser.parse(diceExpr, roller);
        val username = this.user?.let { getUsername(it.id, it.nameCardOrNick) } ?: "未知用户名"
        val targetMessage = if (diceTarget.isNullOrBlank()) "$username 的先攻:\n" else "$diceTarget 的先攻:\n"
        this.user?.let {
            this.getGroupOrNull()?.id?.let { it1 ->
                setInitiative(it1, this.user!!.id, rolls.totalRoll, diceTarget ?: username)
                this.sendMessage(targetMessage + rolls.toString() + " = " + rolls.totalRoll)
            }
        }
    }
}