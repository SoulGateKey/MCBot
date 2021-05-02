package com.github.hank9999.mcbot.types

data class Token(
    val token: String,
    val guild: String,
    val channel: String,
    val log: String,
    val chat: String,
    val playerCommand: String,
    val login: String,
    val logout: String,
    val rconCommand: String,
    val tellraw: String
)
