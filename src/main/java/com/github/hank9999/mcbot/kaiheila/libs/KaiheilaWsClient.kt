package com.github.hank9999.mcbot.kaiheila.libs

import com.github.hank9999.mcbot.kaiheila.events.WsEvent
import com.github.hank9999.mcbot.kaiheila.utils.KaiheilaWs
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class KaiheilaWsClient(serverUri: URI) : WebSocketClient(serverUri) {

    override fun onOpen(arg0: ServerHandshake) {
        KaiheilaWs.logger.info("开黑啦WS已连接, 等待Hello消息")
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        KaiheilaWs.logger.error("开黑啦WS连接已关闭 {} 错误码: {} 原因: {}", (if (remote) "远端" else "本地"), code, reason)
        KaiheilaWs.status = 0
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
        KaiheilaWs.logger.error("开黑啦WS连接出错")
    }

    override fun onMessage(message: String) {
        WsEvent().handleEvent(message)
    }
}
