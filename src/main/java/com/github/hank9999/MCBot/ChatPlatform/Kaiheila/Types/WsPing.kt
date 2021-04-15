package com.github.hank9999.MCBot.ChatPlatform.Kaiheila.Types

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WsPing (
    val s: Int = 2,
    val sn: Int
)