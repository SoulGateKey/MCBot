package com.github.hank9999.MCBot.ChatPlatform.Kaiheila

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WebsocketGatewayResp (
        val code: Int,
        val message: String,
        val data: Data,
) {
    data class Data (
            val url: String? = null
    )
}

@JsonClass(generateAdapter = true)
data class WsSignalling (
    val s: Int,
    val sn: Int?,
    val d: D?
) {
    data class D (
        val code: Int?,
        val err: String?,
        val session_id: String?,

    )
}

@JsonClass(generateAdapter = true)
data class WsPing (
    val s: Int = 2,
    val sn: Int,
)