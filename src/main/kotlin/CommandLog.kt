package org.mirai.qqBotMirai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.message.data.FileMessage
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.mirai.qqBotMirai.CommandLog.pause
import org.mirai.qqBotMirai.CommandLog.turnOn
import java.io.File
import java.time.LocalDate
import java.util.*

object LogStatus : AutoSavePluginData("LogStatus") {
    var logOn: MutableMap<Long, Boolean> by value()
    var logMessages: MutableMap<Int, MutableList<Pair<String, String>>> by value()
    var currentID: MutableMap<Long, Int> by value()
}
object CommandLog : CompositeCommand(PluginMain, "log", description = "log记录相关指令") {

    @SubCommand("start")
    suspend fun CommandSender.turnOn() {
        val groupID = this.getGroupOrNull()?.id ?: -1
        if(LogStatus.logOn[groupID] == false){
            this.sendMessage("已经在记录中。")
            return;
        }
        LogStatus.logOn[groupID] = true
        val logID = LogStatus.logMessages.size
        this.sendMessage("开始记录。记录ID：$logID")
        LogStatus.logMessages[logID] = emptyList<Pair<String, String>>().toMutableList()
        LogStatus.currentID[groupID] = logID
    }
    @SubCommand("on")
    suspend fun CommandSender.reTurnOn(logID: Int = LogStatus.logMessages.size - 1) {
        val groupID = this.getGroupOrNull()?.id ?: -1
        if(LogStatus.logOn[groupID] != false){
            this.sendMessage("已经在记录中或找不到记录。")
            return;
        }
        LogStatus.logOn[groupID] = true
        this.sendMessage("重新开始记录")
        LogStatus.currentID[groupID] = logID
    }

    @SubCommand("off")
    suspend fun CommandSender.pause() {
        val groupID = this.getGroupOrNull()?.id ?: -1
        if(LogStatus.logOn[groupID] != true){
            this.sendMessage("没有检测到正在进行的记录。")
            return;
        }
        LogStatus.logOn[groupID] = false
        this.sendMessage("暂停记录。")
    }

    @SubCommand("end")
    suspend fun CommandSender.turnOff() {
        val groupID = this.getGroupOrNull()?.id ?: -1
        if (LogStatus.logOn[groupID] != true) {
            this.sendMessage("没有检测到正在进行的记录。")
            return;
        }
        LogStatus.logOn[groupID] = false
        this.sendMessage("辛苦了。已结束记录。")

        var logString = ""
        val logFile = File("./log.txt")
        logFile.setWritable(true)
        LogStatus.logMessages[LogStatus.currentID[groupID]]?.forEach(fun(p) {
            val senderName = p.first
            val msg = p.second
            logString = "$logString$senderName：$msg\n"
        })
        print(logString)
        logFile.writeText(logString)
        val resource = logFile.toExternalResource()
        this.getGroupOrNull()?.files?.root?.uploadNewFile("LOG_record_" + LocalDate.now() + ".txt",
            resource)
        withContext(Dispatchers.IO) {
            resource.close()
        }
    }
}