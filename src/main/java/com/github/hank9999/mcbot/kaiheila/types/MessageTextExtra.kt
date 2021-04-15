package com.github.hank9999.mcbot.kaiheila.types

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageTextExtra(
    val extra: Extra
) {
    data class Extra(
        val author: Author,
        val channel_name: String,
        val guild_id: String,
        val local_id: String,
        val mention: List<String>,
        val mention_all: Boolean,
        val mention_here: Boolean
    )
}