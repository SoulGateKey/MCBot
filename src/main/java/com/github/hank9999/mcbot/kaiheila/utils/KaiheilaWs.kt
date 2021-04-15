package com.github.hank9999.mcbot.kaiheila.utils

import com.github.hank9999.mcbot.kaiheila.Hardcore
import com.github.hank9999.mcbot.kaiheila.libs.KaiheilaWsClient
import com.github.hank9999.mcbot.kaiheila.types.WebsocketGatewayResp
import com.github.hank9999.mcbot.utils.Config
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.Request
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

class KaiheilaWs {

    companion object {
        var wsClient: KaiheilaWsClient? = null
        var status: Int = 0 //0=>未连接 1=>已创建ws连接,等待hello消息(6s) 2=>连接成功
        var sn: Int = 0
        var pongTime: Long = 0
        var timeoutCount: Int = 0
        val logger: Logger = LoggerFactory.getLogger(KaiheilaWs::class.java)
        val recvQueue = mutableMapOf<Int, String>()
    }

    private fun getWsUrl(): String {
        logger.info("开始获取开黑啦WS连接地址")
        var errorCount = 0
        while (true) {
            run loop@{
                val client = OkHttpClient()
                val request: Request = Request.Builder()
                    .url(Hardcore.api.Websocket.gatewayUrl)
                .addHeader("Authorization", "Bot " + Config.bot.token)
                    .build()
                var resp: String
                client.newCall(request).execute().use { response ->
                    resp = if (response.body != null) response.body!!.string() else ""

                    if (!response.isSuccessful) {
                        logger.error("未知状态码 {}\n返回: {}", response, resp)
                        logger.error("获取开黑啦WS连接地址时 HTTP请求失败")
                        errorCount += 1
                        logger.error(
                            "获取开黑啦WS连接地址第{}次失败, 等待{}秒后重试",
                            errorCount,
                            (if (errorCount * 5 <= 30) errorCount * 5 else 30)
                        )
                        Thread.sleep((if (errorCount * 5 <= 30) errorCount * 5 * 1000 else 30 * 1000).toLong())
                        return@loop
                    }
                }

                val moshi = Moshi.Builder()
                    .addLast(KotlinJsonAdapterFactory())
                    .build()
                val jsonAdapter = moshi.adapter(WebsocketGatewayResp::class.java)
                val gatewayResp = jsonAdapter.fromJson(resp)

                if (gatewayResp == null) {
                    logger.error("获取开黑啦WS连接地址时 json解析失败\njson内容: {}", resp)
                    errorCount += 1
                    logger.error(
                        "获取开黑啦WS连接地址第{}次失败, 等待{}秒后重试",
                        errorCount,
                        (if (errorCount * 5 <= 30) errorCount * 5 else 30)
                    )
                    Thread.sleep((if (errorCount * 5 <= 30) errorCount * 5 * 1000 else 30 * 1000).toLong())
                    return@loop
                }

                if (gatewayResp.code != 0) {
                    logger.error(gatewayResp.message)
                    logger.error("获取开黑啦WS连接地址时 开黑啦返回错误")
                    errorCount += 1
                    logger.error(
                        "获取开黑啦WS连接地址第{}次失败, 等待{}秒后重试",
                        errorCount,
                        (if (errorCount * 5 <= 30) errorCount * 5 else 30)
                    )
                    Thread.sleep((if (errorCount * 5 <= 30) errorCount * 5 * 1000 else 30 * 1000).toLong())
                    return@loop
                }

                if (gatewayResp.data.url == null || gatewayResp.data.url.isEmpty()) {
                    logger.error("获取开黑啦WS连接地址时 无法找到url")
                    errorCount += 1
                    logger.error(
                        "获取开黑啦WS连接地址第{}次失败, 等待{}秒后重试",
                        errorCount,
                        (if (errorCount * 5 <= 30) errorCount * 5 else 30)
                    )
                    Thread.sleep((if (errorCount * 5 <= 30) errorCount * 5 * 1000 else 30 * 1000).toLong())
                    return@loop
                }
                logger.info("获取开黑啦WS连接地址成功")
                return gatewayResp.data.url
            }
        }
    }

    fun connect() {
        run loop@{
            status = 0
            sn = 0
            pongTime = 0
            timeoutCount = 0
            wsClient = KaiheilaWsClient(URI(getWsUrl()))
            wsClient!!.connect()
            status = 1
            Thread.sleep(6000)
            if (status != 2) {
                logger.error("未收到开黑啦Hello消息, 连接失败, 8秒后第1次重连")
                status = 0
                wsClient!!.close(1000)
                Thread.sleep(8000)
                wsClient!!.reconnect()
                status = 1
                Thread.sleep(6000)
                if (status != 2) {
                    logger.error("未收到开黑啦Hello消息, 连接失败, 16秒后第2次重连")
                    wsClient!!.close(1000)
                    Thread.sleep(16 * 1000)
                    status = 0
                    wsClient!!.reconnect()
                    status = 1
                    Thread.sleep(6000)
                    if (status != 2) {
                        logger.error("未收到开黑啦Hello消息, 连接失败, 2次重连失败, 正在重新开始")
                        return@loop
                    }
                }
            }
        }
    }

    fun reconnect(mode: Int) { //1->Ping重试
        status = 0
        sn = 0
        pongTime = 0
        timeoutCount = 0
        if (mode == 1) {
            logger.info("8s后第1次重连")
            wsClient!!.close(1000)
            Thread.sleep(8000)
            wsClient!!.reconnect()
            status = 1
            Thread.sleep(6000)
            if (status != 2) {
                logger.error("未收到开黑啦Hello消息, 连接失败, 16秒后第2次重连")
                status = 0
                wsClient!!.close(1000)
                Thread.sleep(16 * 1000)
                wsClient!!.reconnect()
                status = 1
                Thread.sleep(6000)
                if (status != 2) {
                    logger.error("未收到开黑啦Hello消息, 连接失败, 2次重连失败, 正在重新开始")
                    connect()
                }
            }
        }
    }

}