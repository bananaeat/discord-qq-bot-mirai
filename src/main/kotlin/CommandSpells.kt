package org.mirai.qqBotMirai

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CompositeCommand
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.SimpleCommand
import java.io.File

@Serializable
data class SpellAction(val cost: Int, val type: String)

@Serializable
data class SpellRange(val value: String, val units: String, val maxIncrements: Int, val minValue: String, val minUnits: String)

@Serializable
data class ItemValue(@Contextual val name: String, @Contextual val value: Int)
@Serializable
data class Spell(val name: String,
                 val types: String,
                 val action: SpellAction,
                 val duration: String,
                 val target: String,
                 val range: SpellRange,
                 val effect: String,
                 val save: String,
                 val classes: List<List<ItemValue>>,
                 val domain: List<List<ItemValue>>,
                 val subDomain: List<List<ItemValue>>,
                 val bloodline: List<List<ItemValue>>,
                 val elementalSchool: List<List<ItemValue>>,
                 val focus: String,
                 val area: String,
                 val level: Int,
                 val school: String,
                 val subSchool: String,
                 val components: Map<String, ItemValue>,
                 val materials: Map<String, ItemValue>,
                 val sr: String,
                 val preparation: String,
                 val shortDescription: String,
    )
@Serializable
data class SpellCompendium(val spellList: List<Spell>)

private fun SearchSpells(key: String, spells: SpellCompendium): List<Spell> {
    val spellList = mutableListOf<Spell>()
    spells.spellList.forEach(fun(s){
        if(s.name.contains(key))
            spellList.add(s)
    })
    // return original response
    return spellList
}
object CommandSpell : SimpleCommand(PluginMain, "main") {
    private val spellsJSON = File("pack/spells.json").readText()
    private val spellC = Json.decodeFromString<SpellCompendium>(SpellCompendium.serializer(), spellsJSON)

    @Handler
    suspend fun spellSearch(context: CommandContext, spellName: String) {
        val spellList = SearchSpells(spellName, spellC)
        val queryResult = Utils.Companion.spellSearchFormatter(spellList)
        println(queryResult)
    }
}