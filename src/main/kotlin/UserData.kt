package org.mirai.qqBotMirai

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

@Serializable
data class UserData(var username: String, var nickname: String)
object UserDataList : AutoSavePluginData("UserData") {
    val users: Map<Long, UserData> by value()
}