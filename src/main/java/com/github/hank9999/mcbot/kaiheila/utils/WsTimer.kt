package com.github.hank9999.mcbot.kaiheila.utils

import com.github.hank9999.mcbot.kaiheila.types.WsPing
import com.github.hank9999.mcbot.kaiheila.KaiheilaWs
import com.github.hank9999.mcbot.kaiheila.types.WsStatus
import com.github.hank9999.mcbot.utils.Json
import java.util.*

class WsTimer {
    class Ping : TimerTask() {
        override fun run() {
            val pingJson = Json().serialize(WsPing(sn=KaiheilaWs.sn))
            if (KaiheilaWs.status == WsStatus.Connected) {
                KaiheilaWs.wsClient!!.send(pingJson)
                val sendTime = System.currentTimeMillis()
                Thread.sleep(6000)
                if (KaiheilaWs.pongTime - sendTime < 0) {
                    KaiheilaWs.timeoutCount += 1
                    if (KaiheilaWs.timeoutCount >= 2) {
                        Thread.sleep(2000)
                        KaiheilaWs.wsClient!!.send(pingJson)
                        Thread.sleep(4000)
                        KaiheilaWs.wsClient!!.send(pingJson)
                        Thread.sleep(6000)
                        if (KaiheilaWs.pongTime - sendTime < 0) {
                            KaiheilaWs.logger.error("开黑啦WS超时")
                            KaiheilaWs().reconnect(1)
                        }
                    }
                }
            }
        }
    }
}