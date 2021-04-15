package com.github.hank9999.mcbot

import com.github.hank9999.mcbot.kaiheila.utils.KaiheilaWs
import com.github.hank9999.mcbot.utils.Config
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

object MCBot {

    private val logger: Logger = LoggerFactory.getLogger(MCBot::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        if (!Config().checkExists()) {
            logger.error("未找到配置文件")
            logger.info("已生成配置文件，请配置后再启动程序")
            exitProcess(1)
        }
        Config().setValue()
        Config().checkConfig()
        KaiheilaWs().connect()
        while (true) {
            val command = readLine()
        }
    }
}