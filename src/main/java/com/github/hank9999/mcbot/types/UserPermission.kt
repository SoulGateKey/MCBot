package com.github.hank9999.mcbot.types

data class UserPermission(
    val user: String,
    val guild: String,
    val permission: String,
    val bool: Boolean,
)