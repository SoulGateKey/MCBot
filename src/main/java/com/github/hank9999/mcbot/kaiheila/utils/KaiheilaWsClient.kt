package com.github.hank9999.mcbot.kaiheila.utils

import com.github.hank9999.mcbot.kaiheila.KaiheilaWs
import com.github.hank9999.mcbot.kaiheila.events.WsEvent
import com.github.hank9999.mcbot.kaiheila.types.WsStatus
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class KaiheilaWsClient(serverUri: URI) : WebSocketClient(serverUri) {

    override fun onOpen(arg0: ServerHandshake) {
        KaiheilaWs.logger.info("开黑啦WS已连接, 等待Hello消息")
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        KaiheilaWs.logger.error("开黑啦WS连接已关闭 {} 错误码: {} 原因: {}", (if (remote) "远端" else "本地"), code, reason)
        KaiheilaWs.status = WsStatus.NotConnected
        Thread.sleep(6 * 1000)
        if (KaiheilaWs.status == WsStatus.NotConnected && !KaiheilaWs.isConnecting) {
            KaiheilaWs.connect()
        }
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
        KaiheilaWs.logger.error("开黑啦WS连接出错")
    }

    override fun onMessage(message: String) {
        WsEvent().handleEvent(message)
    }
}
