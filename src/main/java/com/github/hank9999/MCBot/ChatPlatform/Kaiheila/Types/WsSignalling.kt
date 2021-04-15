package com.github.hank9999.MCBot.ChatPlatform.Kaiheila.Types

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WsSignalling (
    val s: Int,
    val sn: Int?,
    val d: D?
) {
    data class D (
        val code: Int?,
        val err: String?,
        val session_id: String?
    )
}