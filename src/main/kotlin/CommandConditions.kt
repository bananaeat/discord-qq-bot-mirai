package org.mirai.qqBotMirai

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand

@Serializable
data class Condition(val id: String, val name: String, val description: String)

private fun searchConditions(key: String, conditions: List<Condition>): List<Condition> {
    val conditionList = mutableListOf<Condition>()
    conditions.forEach(fun(c){
        if(c.name.contains(key))
            conditionList.add(c)
    })
    // return original response
    return conditionList
}
object CommandConditions : SimpleCommand(PluginMain, "condition", "cond", description = "查询状态") {
    private val conditionsJSON = CommandConditions::class.java.getResource("/pack/conditions.json")?.readText()
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    private val conditionC = json.decodeFromString<List<Condition>>(conditionsJSON.orEmpty())

    @Handler
    suspend fun CommandSender.onCommand(condName: String) {
        val conditionList = searchConditions(condName, conditionC)
        val queryResult = Utils.conditionSearchFormatter(conditionList)
        this.sendMessage(queryResult)
    }
}