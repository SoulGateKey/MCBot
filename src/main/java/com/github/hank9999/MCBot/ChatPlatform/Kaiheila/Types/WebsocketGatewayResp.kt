package com.github.hank9999.MCBot.ChatPlatform.Kaiheila.Types

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WebsocketGatewayResp (
    val code: Int,
    val message: String,
    val data: Data
) {
    data class Data (
        val url: String?
    )
}