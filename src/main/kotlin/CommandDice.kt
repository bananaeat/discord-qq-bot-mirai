package org.mirai.qqBotMirai

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import com.bernardomg.tabletop.dice.interpreter.DiceRoller
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser

object CommandDice : SimpleCommand(PluginMain, "r", description = "roll a dice") {
    @Handler
    suspend fun CommandSender.onCommand(diceExpr: String) {
        val parser = DefaultDiceParser();
        val roller = DiceRoller();
        val rolls = parser.parse(diceExpr, roller);

        this.sendMessage("魔法骰子~ " + rolls.toString() + " = " + rolls.totalRoll)
    }
}