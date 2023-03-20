package org.mirai.qqBotMirai

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.internal.data.builtins.AutoLoginConfig.Account.ConfigurationKey.Parser.inferGroup
import net.mamoe.mirai.console.util.scopeWith
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import org.mirai.qqBotMirai.PluginMain.getResourceAsStream

@Serializable
data class SpellAction(val cost: Int, val type: String)

@Serializable
data class SpellRange(val value: String?, val units: String?)
@Serializable
data class SpellDuration(val value: String, val units: String)

@Serializable
data class Spell(val name: String,
                 val types: String,
                 val action: SpellAction,
                 val duration: SpellDuration,
                 val target: String,
                 val range: SpellRange,
                 val effect: String,
                 val save: String,
                 val classes: Map<String, Int>,
                 val domain: Map<String, Int>,
                 val subDomain: Map<String, Int>,
                 val bloodline: Map<String, Int>,
                 val elementalSchool: Map<String, Int>,
                 val focus: String,
                 val area: String,
                 val school: String,
                 val subschool: String,
                 val components: Map<String, Boolean>,
                 val materials: Map<String, String>,
                 val sr: String,
                 val shortDescription: String,
    )

private fun searchSpells(key: String, spells: List<Spell>): List<Spell> {
    val spellList = mutableListOf<Spell>()
    spells.forEach(fun(s){
        if(s.name.contains(key))
            spellList.add(s)
    })
    // return original response
    return spellList
}
object CommandSpell : SimpleCommand(PluginMain, "spell", description = "查询法术") {
    private val spellsJSON = CommandSpell::class.java.getResource("/pack/spells.json")?.readText()
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    private val spellC = json.decodeFromString<List<Spell>>(spellsJSON.orEmpty())

    @Handler
    suspend fun CommandSender.onCommand(spellName: String) {
        val spellList = searchSpells(spellName, spellC)
        val queryResult = Utils.spellSearchFormatter(spellList)
        this.sendMessage(queryResult)
    }
}