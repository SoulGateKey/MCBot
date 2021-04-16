package com.github.hank9999.mcbot.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess


class Config {

    private val logger: Logger = LoggerFactory.getLogger(Config::class.java)

    object Bot {
        var client_id: String? = null
        var client_secret: String? = null
        var token: String? = null
        var cmd_prefix: List<String>? =  listOf(".", "。", "/", "#")
        var superadmins: List<Int>? = listOf()
    }
    object Ws {
        var host: String? = "localhost"
        var port: Int? = 3001
        var path: String? = "/path"
    }
    object DataBase {
        object MySQL {
            var enable: Boolean? = false
            var host: String? = "localhost"
            var port: Int? = 3306
            var user: String? = null
            var password: String? = null
            var database: String? = null
        }
    }

    fun checkExists(): Boolean {
        val file = File("config.conf")
        if (file.exists()) {
            return true
        } else {
            val inputStream: InputStream = Config::class.java.getResourceAsStream("/config.conf")!!
            try {
                Files.copy(inputStream, Paths.get("config.conf"))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                logger.error("配置文件错误: 复制配置文件时未找到程序内文件")
                exitProcess(1)
            } catch (e: IOException) {
                e.printStackTrace()
                logger.error("配置文件错误: 复制配置文件时IO错误")
                exitProcess(1)
            }
        }
        return false
    }

    fun checkConfig() {
        if (Bot.client_id == null || Bot.client_id!!.isEmpty()) {
            logger.error("配置文件错误: bot.client_id 不存在或为空")
            exitProcess(1)
        } else if (Bot.client_secret == null || Bot.client_secret!!.isEmpty()) {
            logger.error("配置文件错误: bot.client_secret 不存在或为空")
            exitProcess(1)
        } else if (Bot.token == null || Bot.token!!.isEmpty()) {
            logger.error("配置文件错误: bot.token 不存在或为空")
            exitProcess(1)
        } else if (Bot.cmd_prefix == null || Bot.cmd_prefix!!.isEmpty()) {
            logger.error("配置文件错误: bot.cmd_prefix 不存在或为空")
            exitProcess(1)
        } else if (Bot.superadmins == null) {
            logger.error("配置文件错误: bot.superadmins 不存在")
            exitProcess(1)
        } else if (Ws.host == null || Ws.host!!.isEmpty()) {
            logger.error("配置文件错误: ws.host 不存在或为空")
            exitProcess(1)
        } else if (Ws.port == null || (Ws.port!! < 0 || Ws.port!! > 65535)) {
            logger.error("配置文件错误: ws.port 不存在或不合法")
            exitProcess(1)
        } else if (Ws.path == null || Ws.path!!.isEmpty()) {
            logger.error("配置文件错误: ws.path 不存在或为空")
            exitProcess(1)
        } else if (DataBase.MySQL.enable == null) {
            logger.error("配置文件错误: database.mysql.enable 不存在或为空")
            exitProcess(1)
        }
        if (DataBase.MySQL.enable!!) {
            if (DataBase.MySQL.host == null || DataBase.MySQL.host!!.isEmpty()) {
                logger.error("配置文件错误: database.mysql.host 不存在或为空")
                exitProcess(1)
            } else if (DataBase.MySQL.port == null || (DataBase.MySQL.port!! < 0 || DataBase.MySQL.port!! > 65535)) {
                logger.error("配置文件错误: database.mysql.port 不存在或不合法")
                exitProcess(1)
            } else if (DataBase.MySQL.user == null || DataBase.MySQL.user!!.isEmpty()) {
                logger.error("配置文件错误: database.mysql.user 不存在或为空")
                exitProcess(1)
            } else if (DataBase.MySQL.password == null || DataBase.MySQL.password!!.isEmpty()) {
                logger.error("配置文件错误: database.mysql.password 不存在或为空")
                exitProcess(1)
            } else if (DataBase.MySQL.database == null || DataBase.MySQL.database!!.isEmpty()) {
                logger.error("配置文件错误: database.mysql.database 不存在或为空")
                exitProcess(1)
            }
        }
    }

    fun setValue() {
        val loader = HoconConfigurationLoader.builder()
                .path(Paths.get("config.conf"))
                .build()
        val root: CommentedConfigurationNode
        try {
            root = loader.load()
        } catch (e: IOException) {
            logger.error("加载配置文件时发生错误: " + e.message)
            if (e.cause != null) {
                e.cause!!.printStackTrace()
            }
            exitProcess(1)
        }

        Bot.client_id = root.node("bot", "client_id").string
        Bot.client_secret = root.node("bot", "client_secret").string
        Bot.token = root.node("bot", "token").string
        if (!root.node("bot", "cmd_prefix").isList) {
            logger.error("配置文件错误: bot.cmd_prefix类型错误 非List类型")
            exitProcess(1)
        }
        Bot.cmd_prefix = root.node("bot", "cmd_prefix").getList(String::class.java)
        if (!root.node("bot", "superadmins").isList) {
            logger.error("配置文件错误: bot.superadmins类型错误 非List类型")
            exitProcess(1)
        }
        Bot.superadmins = root.node("bot", "superadmins").getList(Int::class.javaObjectType)
        Ws.host = root.node("ws", "host").string
        Ws.port = root.node("ws", "port").int
        Ws.path = root.node("ws", "path").string
        DataBase.MySQL.enable = root.node("database", "mysql", "enable").boolean
        DataBase.MySQL.host = root.node("database", "mysql", "host").string
        DataBase.MySQL.port = root.node("database", "mysql", "port").int
        DataBase.MySQL.user = root.node("database", "mysql", "user").string
        DataBase.MySQL.password = root.node("database", "mysql", "password").string
        DataBase.MySQL.database = root.node("database", "mysql", "database").string
    }

}