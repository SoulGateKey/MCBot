package com.github.hank9999.mcbot.kaiheila

import com.github.hank9999.mcbot.kaiheila.types.WebsocketGatewayResp
import com.github.hank9999.mcbot.kaiheila.types.WsStatus
import com.github.hank9999.mcbot.kaiheila.utils.KaiheilaWsClient
import com.github.hank9999.mcbot.utils.Config
import com.github.hank9999.mcbot.utils.Http
import com.github.hank9999.mcbot.utils.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

class KaiheilaWs {

    companion object {
        var wsClient: KaiheilaWsClient? = null
        var status = WsStatus.NotConnected
        var sn = 0
        var pongTime: Long = 0
        var timeoutCount = 0
        val logger: Logger = LoggerFactory.getLogger(KaiheilaWs::class.java)
        val recvQueue = mutableMapOf<Int, String>()

        private fun getWsAddressError(errCount: Int): Int {
            val errorCount = errCount + 1
            logger.error(
                "获取开黑啦WS连接地址第{}次失败, 等待{}秒后重试",
                errorCount,
                (if (errorCount * 5 <= 30) errorCount * 5 else 30)
            )
            Thread.sleep((if (errorCount * 5 <= 30) errorCount * 5 * 1000 else 30 * 1000).toLong())
            return errorCount
        }

        private fun getWsUrl(): String {
            logger.info("开始获取开黑啦WS连接地址")
            var errorCount = 0
            while (true) {
                val resp = Http.get(
                    Hardcore.Api.Websocket.gatewayUrl,
                    mutableMapOf<String, String>().apply { this["Authorization"] = "Bot " + Config.Bot.token }
                )

                val gatewayResp = Json.deserialize(resp.response, WebsocketGatewayResp::class)

                if (gatewayResp == null) {
                    logger.error("获取开黑啦WS连接地址时 json解析失败\njson内容: {}", resp)
                    errorCount = getWsAddressError(errorCount)
                    continue
                }

                if (gatewayResp.code != 0) {
                    logger.error(gatewayResp.message)
                    logger.error("获取开黑啦WS连接地址时 开黑啦返回错误")
                    errorCount = getWsAddressError(errorCount)
                    continue
                }

                if (gatewayResp.data.url == null || gatewayResp.data.url.isEmpty()) {
                    logger.error("获取开黑啦WS连接地址时 无法找到url")
                    errorCount = getWsAddressError(errorCount)
                    continue
                }
                logger.info("获取开黑啦WS连接地址成功")
                return gatewayResp.data.url
            }
        }

        private fun cleanCounter() {
            wsClient = null
            status = WsStatus.NotConnected
            sn = 0
            pongTime = 0
            timeoutCount = 0
            recvQueue.clear()
        }

        private fun didNotReceiveHelloMessage(count: Int) {
            val waitTime = 8 * count
            logger.error("未收到开黑啦Hello消息, 连接失败, $waitTime 秒后第 $count 次重连")
            status = WsStatus.NotConnected
            wsClient!!.close(1000)
            Thread.sleep((waitTime * 1000).toLong())
            wsClient!!.reconnect()
            status = WsStatus.WaitHelloMessage
            Thread.sleep(6000)
        }

        fun connect() {
            while (true) {
                cleanCounter()
                wsClient = KaiheilaWsClient(URI(getWsUrl()))
                wsClient!!.connect()
                status = WsStatus.WaitHelloMessage
                Thread.sleep(6000)
                for (count in 1..2) {
                    if (status != WsStatus.Connected) {
                        didNotReceiveHelloMessage(count)
                    } else {
                        break
                    }
                }
                if (status == WsStatus.Connected) {
                    break
                } else {
                    logger.error("未收到开黑啦Hello消息, 连接失败, 2次重连失败, 正在重新开始")
                    continue
                }
            }
        }
    }
}