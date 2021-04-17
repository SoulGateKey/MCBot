package com.github.hank9999.mcbot.types

data class HttpResponse(
    val code: Int,
    val response: String,
    val headers: Map<String, String>
)
