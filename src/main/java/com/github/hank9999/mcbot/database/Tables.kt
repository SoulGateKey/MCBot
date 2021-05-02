package com.github.hank9999.mcbot.database

import org.jetbrains.exposed.dao.id.IntIdTable

class Tables {
    object Token : IntIdTable("token") {
        val token = varchar("token", 36)
        val guild = varchar("guild", 24).default("0")
        val channel = varchar("channel", 24).default("0")
        val log = varchar("log", 24).default("-1")
        val chat = varchar("chat", 24).default("-1")
        val playerCommand = varchar("playerCommand", 24).default("-1")
        val login = varchar("login", 24).default("-1")
        val logout = varchar("logout", 24).default("-1")
        val rconCommand = varchar("rconCommand", 24).default("-1")
        val tellraw = varchar("tellraw", 1024).default("[\"\",{\"text\":\"<\",\"color\":\"white\"},{\"text\":\"%playerId%\",\"color\":\"gold\"},{\"text\":\"> \",\"color\":\"white\"},{\"text\":\"%text%\",\"color\":\"white\"}]")
        // for guild and channel 0 means not set, other means id
        // -1 means not set and disabled, 0 means enabled and follow default channel, other means enabled and channel
    }

    object GroupPermission : IntIdTable("permission_group") {
        val name = varchar("name", 16)
        val guild = varchar("guild", 24)
        val permission = varchar("permission", 128)
        val bool = bool("boolean").default(true)
    }

    object UserPermission : IntIdTable("permission_user") {
        val user = varchar("user", 16)
        val guild = varchar("guild", 24)
        val permission = varchar("permission", 128)
        val bool = bool("boolean").default(true)
    }

    object Filter : IntIdTable("filter") {
        val guild = varchar("guild", 24)
        val filter = varchar("filter", 128)
    }
}