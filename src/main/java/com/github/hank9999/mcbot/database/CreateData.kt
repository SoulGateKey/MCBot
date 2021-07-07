package com.github.hank9999.mcbot.database

import com.github.hank9999.mcbot.types.GroupPermission
import com.github.hank9999.mcbot.types.Token
import com.github.hank9999.mcbot.types.UserPermission
import org.jetbrains.exposed.sql.ResultRow

class CreateData {
    companion object {
        fun createTokenByResultRow(result: ResultRow): Token {
            return Token(
                token = result[Tables.Token.token],
                guild = result[Tables.Token.guild],
                channel = result[Tables.Token.channel],
                log = result[Tables.Token.log],
                chat = result[Tables.Token.chat],
                playerCommand = result[Tables.Token.playerCommand],
                login = result[Tables.Token.login],
                logout = result[Tables.Token.logout],
                rconCommand = result[Tables.Token.rconCommand],
                tellraw = result[Tables.Token.tellraw]
            )
        }

        fun createGroupPermissionByResultRow(result: ResultRow): GroupPermission {
            return GroupPermission(
                name = result[Tables.GroupPermission.name],
                guild = result[Tables.GroupPermission.guild],
                permission = result[Tables.GroupPermission.permission],
                bool = result[Tables.GroupPermission.bool]
            )
        }

        fun createUserPermissionByResultRow(result: ResultRow): UserPermission {
            return UserPermission(
                user = result[Tables.UserPermission.user],
                guild = result[Tables.UserPermission.guild],
                permission = result[Tables.UserPermission.permission],
                bool = result[Tables.UserPermission.bool]
            )
        }
    }
}