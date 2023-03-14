package org.mirai.qqBotMirai

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CommandUnitTest {
    @org.junit.jupiter.api.Test
    fun commandSpellParse() {
        val spellsJSON = CommandSpell::class.java.getResource("/pack/spells.json")?.readText()
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        val spellC = json.decodeFromString<List<Spell>>(spellsJSON.orEmpty())
        print(spellC)
    }

}