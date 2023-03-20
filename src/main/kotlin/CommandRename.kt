package org.mirai.qqBotMirai

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.nameCardOrNick
import org.mirai.qqBotMirai.CommandRename.onCommand

object UserDataList : AutoSavePluginData("UserData") {
    var users: MutableMap<Long, Pair<String, String>> by value()
}

fun getUsername(userID: Long, userName: String): String{
    if(userID !in UserDataList.users.keys){
        val userData = Pair<String, String>(
            first = userName,
            second = ""
        )
        UserDataList.users[userID] = userData
        return userName
    } else {
        val username = UserDataList.users[userID]?.first
        val nickname = UserDataList.users[userID]?.second
        return if(nickname == ""){
            username
        } else {
            nickname
        } ?: "User Not Found"
    }
}
object CommandRename : SimpleCommand(PluginMain, "nn", description = "重命名昵称") {
    @Handler
    suspend fun CommandSender.onCommand(name: String) {
        val newPair = Pair(
            first = this.user?.nameCardOrNick ?: "Name Not Found",
            second = name
        )
        UserDataList.users[this.user?.id ?: 0] = newPair
        this.sendMessage("已设置角色名：$name")
    }
}