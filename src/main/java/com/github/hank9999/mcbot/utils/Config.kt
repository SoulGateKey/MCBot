package com.github.hank9999.mcbot.Utils

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

    object bot {
        var client_id: String? = null
        var client_secret: String? = null
        var token: String? = null
        var cmd_prefix: List<String>? =  listOf(".", "。", "/", "#")
        var superadmins: List<Int>? = listOf()
    }
    object ws {
        var host: String? = "localhost"
        var port: Int? = 3001
        var path: String? = "/path"
    }
    object database {
        object mysql {
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
        if (bot.client_id == null || bot.client_id!!.isEmpty()) {
            logger.error("配置文件错误: bot.client_id 不存在或为空")
            exitProcess(1)
        } else if (bot.client_secret == null || bot.client_secret!!.isEmpty()) {
            logger.error("配置文件错误: bot.client_secret 不存在或为空")
            exitProcess(1)
        } else if (bot.token == null || bot.token!!.isEmpty()) {
            logger.error("配置文件错误: bot.token 不存在或为空")
            exitProcess(1)
        } else if (bot.cmd_prefix == null || bot.cmd_prefix!!.isEmpty()) {
            logger.error("配置文件错误: bot.cmd_prefix 不存在或为空")
            exitProcess(1)
        } else if (bot.superadmins == null) {
            logger.error("配置文件错误: bot.superadmins 不存在")
            exitProcess(1)
        } else if (ws.host == null || ws.host!!.isEmpty()) {
            logger.error("配置文件错误: ws.host 不存在或为空")
            exitProcess(1)
        } else if (ws.port == null || (ws.port!! < 0 || ws.port!! > 65535)) {
            logger.error("配置文件错误: ws.port 不存在或不合法")
            exitProcess(1)
        } else if (ws.path == null || ws.path!!.isEmpty()) {
            logger.error("配置文件错误: ws.path 不存在或为空")
            exitProcess(1)
        } else if (database.mysql.enable == null) {
            logger.error("配置文件错误: database.mysql.enable 不存在或为空")
            exitProcess(1)
        }
        if (database.mysql.enable!!) {
            if (database.mysql.host == null || database.mysql.host!!.isEmpty()) {
                logger.error("配置文件错误: database.mysql.host 不存在或为空")
                exitProcess(1)
            } else if (database.mysql.port == null || (database.mysql.port!! < 0 || database.mysql.port!! > 65535)) {
                logger.error("配置文件错误: database.mysql.port 不存在或不合法")
                exitProcess(1)
            } else if (database.mysql.user == null || database.mysql.user!!.isEmpty()) {
                logger.error("配置文件错误: database.mysql.user 不存在或为空")
                exitProcess(1)
            } else if (database.mysql.password == null || database.mysql.password!!.isEmpty()) {
                logger.error("配置文件错误: database.mysql.password 不存在或为空")
                exitProcess(1)
            } else if (database.mysql.database == null || database.mysql.database!!.isEmpty()) {
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

        bot.client_id = root.node("bot", "client_id").string
        bot.client_secret = root.node("bot", "client_secret").string
        bot.token = root.node("bot", "token").string
        if (!root.node("bot", "cmd_prefix").isList) {
            logger.error("配置文件错误: bot.cmd_prefix类型错误 非List类型")
            exitProcess(1)
        }
        bot.cmd_prefix = root.node("bot", "cmd_prefix").getList(String::class.java)
        if (!root.node("bot", "superadmins").isList) {
            logger.error("配置文件错误: bot.superadmins类型错误 非List类型")
            exitProcess(1)
        }
        bot.superadmins = root.node("bot", "superadmins").getList(Int::class.javaObjectType)
        ws.host = root.node("ws", "host").string
        ws.port = root.node("ws", "port").int
        ws.path = root.node("ws", "path").string
        database.mysql.enable = root.node("database", "mysql", "enable").boolean
        database.mysql.host = root.node("database", "mysql", "host").string
        database.mysql.port = root.node("database", "mysql", "port").int
        database.mysql.user = root.node("database", "mysql", "user").string
        database.mysql.password = root.node("database", "mysql", "password").string
        database.mysql.database = root.node("database", "mysql", "database").string
    }

}