package com.github.hank9999.MCBot.ChatPlatform.Kaiheila.Utils

import com.github.hank9999.MCBot.ChatPlatform.Kaiheila.WsPing
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

class WsTimer {
    class Ping : TimerTask() {
        override fun run() {
            val wsPing = WsPing(sn=KaiheilaWs.sn)
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val jsonAdapter = moshi.adapter(WsPing::class.java)
            val pingJson = jsonAdapter.toJson(wsPing)
            if (KaiheilaWs.status == 2) {
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