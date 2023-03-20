package org.mirai.qqBotMirai

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import com.bernardomg.tabletop.dice.interpreter.DiceRoller
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser
import net.mamoe.mirai.contact.nameCardOrNick

object CommandDice : SimpleCommand(PluginMain, "r", description = "投掷骰子") {
    @Handler
    suspend fun CommandSender.onCommand(diceExpr: String, diceTarget: String? = null) {
        val parser = DefaultDiceParser();
        val roller = DiceRoller();
        val rolls = parser.parse(diceExpr, roller);
        val username = this.user?.let { getUsername(it.id, it.nameCardOrNick) }
        val targetMessage = if (diceTarget.isNullOrBlank()) "魔法骰子:\n" else ("$diceTarget，魔法骰子:\n")
        if(username.isNullOrBlank()){
            this.sendMessage(targetMessage + rolls.toString() + " = " + rolls.totalRoll)
        } else {
            this.sendMessage(username + "的" + targetMessage + diceExpr + " = " + rolls.toString() + " = " + rolls.totalRoll)
        }

    }
}