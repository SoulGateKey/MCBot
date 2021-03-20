package com.github.hank9999.MCBot.ChatPlatform.Kaiheila

import com.github.hank9999.MCBot.Utils.Config
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.Request
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ConnectWs {
    fun getWsUrl(): String? {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
                .url(Hardcore.api.Websocket.gatewayUrl)
                .addHeader("Authorization", "Bot " + Config.bot.token)
                .build()
        var resp: String
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("Unexpected code $response")
            }
            resp = response.body!!.string()
        }

        val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
        val jsonAdapter = moshi.adapter(WebsocketGatewayResp::class.java)
        val gatewayResp: WebsocketGatewayResp = jsonAdapter.fromJson(resp) ?: return null

        if (gatewayResp.code != 0) {
            println(gatewayResp.message)
            return null
        }
        return gatewayResp.data.url
    }
}