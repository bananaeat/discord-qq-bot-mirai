package org.mirai.qqBotMirai

import com.bernardomg.tabletop.dice.interpreter.DiceRoller
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.info
import org.mirai.qqBotMirai.CommandRollInitiative.onCommand

/**
 * 使用 kotlin 版请把
 * `src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin`
 * 文件内容改成 `org.example.mirai.plugin.PluginMain` 也就是当前主类全类名
 *
 * 使用 kotlin 可以把 java 源集删除不会对项目有影响
 *
 * 在 `settings.gradle.kts` 里改构建的插件名称、依赖库和插件版本
 *
 * 在该示例下的 [JvmPluginDescription] 修改插件名称，id和版本，etc
 *
 * 可以使用 `src/test/kotlin/RunMirai.kt` 在 ide 里直接调试，
 * 不用复制到 mirai-console-loader 或其他启动器中调试
 */

const val defaultDice = 20

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "org.mirai.qqBotMirai",
        name = "泉津bot",
        version = "0.1.0"
    ) {
        author("bananaeat")
        info(
            """
            泉津bot是为跑团设计的插件，提供投骰，查询规则，战斗轮提醒等功能。
        """.trimIndent()
        )
        // author 和 info 可以删除.
    }
) {
    override fun onEnable() {
        UserDataList.reload()
        BattleStatus.reload()
        InitiativeList.reload()
        LogStatus.reload()
        logger.info { "泉津：插件已加载" }
        //配置文件目录 "${dataFolder.absolutePath}/"
        val eventChannel = GlobalEventChannel.parentScope(this)
        CommandManager.registerCommand(CommandSpell)
        CommandManager.registerCommand(CommandRename)
        CommandManager.registerCommand(CommandDice)
        CommandManager.registerCommand(CommandInitiative)
        CommandManager.registerCommand(CommandRollInitiative)
        CommandManager.registerCommand(CommandBattle)
        CommandManager.registerCommand(CommandBattleTurn)
        CommandManager.registerCommand(CommandLog)
        CommandManager.registerCommand(CommandConditions)
        eventChannel.subscribeAlways<GroupMessageEvent> {
            if(LogStatus.logOn[this.group.id] == true){
                LogStatus.logMessages[LogStatus.currentID[this.group.id]]
                    ?.add(Pair(getUsername(this.sender.id, this.senderName),  message.contentToString()))
            }
            val messageString = message.contentToString()
            val message = messageString.split(" ")[0]
            if(messageString.startsWith(".rd") && (message.length == 3 || messageString[3] != ' ' )) {
                var diceExpr = message
                val diceTarget = if (messageString.split(" ").size > 1) (messageString.split(" ")[1]) else ""
                if (!diceExpr[2].isDigit()) {
                    diceExpr = diceExpr.replace("d+", "d$defaultDice+", ignoreCase = true)
                        .replace("d-", "d$defaultDice-", ignoreCase = true).substring(2)
                }
                if(diceExpr.endsWith('d'))
                    diceExpr += "$defaultDice"
                val parser = DefaultDiceParser();
                val roller = DiceRoller();
                val rolls = parser.parse(diceExpr, roller);

                val username = getUsername(this.sender.id, this.senderName)
                val targetMessage = if (diceTarget.isBlank()) "魔法骰子:\n" else ("$diceTarget，魔法骰子:\n")
                if (username.isBlank()) {
                    this.group.sendMessage(targetMessage + rolls.toString() + " = " + rolls.totalRoll)
                } else {
                    this.group.sendMessage(username + "的" + targetMessage + diceExpr + " = " + rolls.toString() + " = " + rolls.totalRoll)
                }
            } else if (messageString.startsWith(".ri") && messageString[3] != ' '){
                var diceExpr = message
                val username = getUsername(this.sender.id, this.senderName)
                val diceTarget = if (messageString.split(" ").size > 1) (messageString.split(" ")[1]) else username
                if (!diceExpr[2].isDigit()) {
                    diceExpr = diceExpr.replace("i", "d$defaultDice", ignoreCase = true).substring(2)
                }
                val parser = DefaultDiceParser();
                val roller = DiceRoller();
                val rolls = parser.parse(diceExpr, roller);

                setInitiative(this.group.id, this.sender.id, rolls.totalRoll, diceTarget)
                this.group.sendMessage("$diceTarget 的先攻:\n" + rolls.toString() + " = " + rolls.totalRoll)
            }
        }
        eventChannel.subscribeAlways<FriendMessageEvent> {
            //好友信息
            sender.sendMessage("hi")
        }
        eventChannel.subscribeAlways<NewFriendRequestEvent> {
            //自动同意好友申请
            accept()
        }
        eventChannel.subscribeAlways<BotInvitedJoinGroupRequestEvent> {
            //自动同意加群申请
            accept()
        }

        myCustomPermission // 注册权限
    }

    // region console 权限系统示例
    private val myCustomPermission by lazy { // Lazy: Lazy 是必须的, console 不允许提前访问权限系统
        // 注册一条权限节点 org.example.mirai-example:my-permission
        // 并以 org.example.mirai-example:* 为父节点

        // @param: parent: 父权限
        //                 在 Console 内置权限系统中, 如果某人拥有父权限
        //                 那么意味着此人也拥有该权限 (org.example.mirai-example:my-permission)
        // @func: PermissionIdNamespace.permissionId: 根据插件 id 确定一条权限 id
        PermissionService.INSTANCE.register(permissionId("my-permission"), "一条自定义权限", parentPermission)
    }

    public fun hasCustomPermission(sender: User): Boolean {
        return when (sender) {
            is Member -> AbstractPermitteeId.ExactMember(sender.group.id, sender.id)
            else -> AbstractPermitteeId.ExactUser(sender.id)
        }.hasPermission(myCustomPermission)
    }
    // endregion
}
