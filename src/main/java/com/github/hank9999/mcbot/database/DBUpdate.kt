package com.github.hank9999.mcbot.database

import com.github.hank9999.mcbot.types.Token
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class DBUpdate {
    fun updateTokenTable(data: Token): Boolean {
        var success = false
        transaction(DataBase.db) {
            val count = Tables.Token.select { Tables.Token.token eq data.token }.count()
            if (count != 0.toLong()) {
                Tables.Token.update ({ Tables.Token.token eq data.token }) {
                    it[token] = data.token
                    it[guild] = data.guild
                    it[channel] = data.channel
                    it[log] = data.log
                    it[chat] = data.chat
                    it[playerCommand] = data.playerCommand
                    it[login] = data.login
                    it[logout] = data.logout
                    it[rconCommand] = data.rconCommand
                    it[tellraw] = data.tellraw
                }
                success = true
            }
        }
        return success
    }
}