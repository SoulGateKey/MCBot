package com.github.hank9999.mcbot.database

import com.github.hank9999.mcbot.types.GroupPermission
import com.github.hank9999.mcbot.types.UserPermission
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class DBDelete {
    companion object {
        fun deleteTokenTable(token: String): Boolean {
            var success = false
            transaction(DataBase.db) {
                val count = Tables.Token.select { Tables.Token.token eq token }.count()
                if (count != 0.toLong()) {
                    Tables.Token.deleteWhere {
                        Tables.Token.token eq token
                    }
                    success = true
                }
            }
            return success
        }

        fun deleteGroupPermission(data: GroupPermission): Boolean {
            var success = false
            transaction(DataBase.db) {
                val count = Tables.GroupPermission.select {
                    Tables.GroupPermission.name eq data.name and
                            (Tables.GroupPermission.guild eq data.guild) and
                            (Tables.GroupPermission.permission eq data.permission)
                }.count()
                if (count != 0.toLong()) {
                    Tables.GroupPermission.deleteWhere {
                        Tables.GroupPermission.name eq data.name
                        Tables.GroupPermission.guild eq data.guild
                        Tables.GroupPermission.permission eq data.permission
                        Tables.GroupPermission.bool eq data.bool
                    }
                    success = true
                }
            }
            return success
        }

        fun deleteUserPermission(data: UserPermission): Boolean {
            var success = false
            transaction(DataBase.db) {
                val count = Tables.UserPermission.select {
                    Tables.UserPermission.user eq data.user and
                            (Tables.UserPermission.guild eq data.guild) and
                            (Tables.UserPermission.permission eq data.permission)
                }.count()
                if (count != 0.toLong()) {
                    Tables.UserPermission.deleteWhere {
                        Tables.UserPermission.user eq data.user
                        Tables.UserPermission.guild eq data.guild
                        Tables.UserPermission.permission eq data.permission
                        Tables.UserPermission.bool eq data.bool
                    }
                    success = true
                }
            }
            return success
        }

        fun deleteFilter(guild: String, filter: String): Boolean {
            var success = false
            transaction(DataBase.db) {
                val count = Tables.Filter.select {
                    Tables.Filter.guild eq guild and
                            (Tables.Filter.filter eq filter)
                }.count()
                if (count != 0.toLong()) {
                    Tables.Filter.deleteWhere {
                        Tables.Filter.guild eq guild
                        Tables.Filter.filter eq filter
                    }
                    success = true
                }
            }
            return success
        }

    }
}