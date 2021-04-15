package com.github.hank9999.mcbot.kaiheila.types

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
    val d: D,
) {
    data class D(
        val author_id: String,
        val channel_type: String,
        val content: String,
        val msg_id: String,
        val msg_timestamp: Long,
        val nonce: String,
        val target_id: String,
        val type: Int
    )
}