package com.github.hank9999.MCBot.ChatPlatform.Kaiheila.Events

import com.github.hank9999.MCBot.ChatPlatform.Kaiheila.Utils.KaiheilaWs
import com.github.hank9999.MCBot.ChatPlatform.Kaiheila.Utils.WsTimer
import com.github.hank9999.MCBot.ChatPlatform.Kaiheila.WsSignalling
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*
import kotlin.system.exitProcess

class WsEvent {

    fun handleEvent(text: String) {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(WsSignalling::class.java)
        val wsText = jsonAdapter.fromJson(text)
        if (wsText == null) {
            KaiheilaWs.logger.error("解析开黑啦消息失败")
            return
        }

        when (wsText.s) {
            1 -> {
                if (wsText.d!!.code!! == 0) {
                    KaiheilaWs.logger.info("已收到开黑啦的Hello消息, 连接成功创立")
                    KaiheilaWs.status = 2
                    Timer().schedule(WsTimer.Ping(), Date(), 30 * 1000)
                } else {
                    KaiheilaWs.status = 0
                    KaiheilaWs.logger.error("开黑啦Hello错误")
                    KaiheilaWs.logger.error("错误信息: {}", wsText.d.err)
                    if (wsText.d.code!! == 40103) {
                        KaiheilaWs.logger.error("正在尝试重新连接")
                        KaiheilaWs().connect()
                    } else {
                        exitProcess(1)
                    }
                }
            }
            0 -> {
                if (KaiheilaWs.sn < wsText.sn!!) {
                    KaiheilaWs.sn = wsText.sn
                    KaiheilaWs.logger.info("开黑啦 >>> {}", text)
                }
            }
            3 -> KaiheilaWs.pongTime = System.currentTimeMillis()
            5 -> KaiheilaWs().connect()
            6 -> KaiheilaWs.logger.info("已收到开黑啦所有离线消息")
            else -> {
                KaiheilaWs.logger.error("暂不支持处理该消息类型 {}", text)
            }
        }
    }
}