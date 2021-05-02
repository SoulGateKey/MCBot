package com.github.hank9999.mcbot.database

import com.github.hank9999.mcbot.types.GroupPermission
import com.github.hank9999.mcbot.types.UserPermission
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class DBCreate {
    companion object {
        fun creatTokenTable(token: String): Boolean {
            var success = false
            transaction(DataBase.db) {
                val count = Tables.Token.select { Tables.Token.token eq token }.count()
                if (count == 0.toLong()) {
                    Tables.Token.insert {
                        it[Tables.Token.token] = token
                    }
                    success = true
                }
            }
            return success
        }

        fun createGroupPermission(data: GroupPermission): Boolean {
            var success = false
            transaction(DataBase.db) {
                val count = Tables.GroupPermission.select {
                    Tables.GroupPermission.name eq data.name and
                            (Tables.GroupPermission.guild eq data.guild) and
                            (Tables.GroupPermission.permission eq data.permission)
                }.count()
                if (count == 0.toLong()) {
                    Tables.GroupPermission.insert {
                        it[name] = data.name
                        it[guild] = data.guild
                        it[permission] = data.permission
                        it[bool] = data.bool
                    }
                    success = true
                }
            }
            return success
        }

        fun createUserPermission(data: UserPermission): Boolean {
            var success = false
            transaction(DataBase.db) {
                val count = Tables.UserPermission.select {
                    Tables.UserPermission.user eq data.user and
                            (Tables.UserPermission.guild eq data.guild) and
                            (Tables.UserPermission.permission eq data.permission)
                }.count()
                if (count == 0.toLong()) {
                    Tables.UserPermission.insert {
                        it[user] = data.user
                        it[guild] = data.guild
                        it[permission] = data.permission
                        it[bool] = data.bool
                    }
                    success = true
                }
            }
            return success
        }

        fun createFilter(guild: String, filter: String): Boolean {
            var success = false
            transaction(DataBase.db) {
                val count = Tables.Filter.select {
                    Tables.Filter.guild eq guild and
                            (Tables.Filter.filter eq filter)
                }.count()
                if (count == 0.toLong()) {
                    Tables.Filter.insert {
                        it[Tables.Filter.guild] = guild
                        it[Tables.Filter.filter] = filter
                    }
                    success = true
                }
            }
            return success
        }
    }
}