package com.github.hank9999.mcbot.database

import com.github.hank9999.mcbot.types.GroupPermission
import com.github.hank9999.mcbot.types.Token
import com.github.hank9999.mcbot.types.UserPermission
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class DBRead {
    companion object {
        fun readTokenTable(token: String? = null, guild: String? = null, channel: String? = null): Token? {
            var result: ResultRow? = null
            when {
                token != null && token.isNotEmpty() -> {
                    transaction (DataBase.db) {
                        Tables.Token.select { Tables.Token.token eq token }.forEach {
                            result = it
                        }
                    }
                    return result?.let { createData.createTokenByResultRow(it) }
                }

                guild != null && guild.isNotEmpty() -> {
                    transaction (DataBase.db) {
                        Tables.Token.select { Tables.Token.guild eq guild }.forEach {
                            result = it
                        }
                    }
                    return result?.let { createData.createTokenByResultRow(it) }
                }

                channel != null && channel.isNotEmpty() -> {
                    transaction (DataBase.db) {
                        Tables.Token.select { Tables.Token.channel eq channel }.forEach {
                            result = it
                        }
                    }
                    return result?.let { createData.createTokenByResultRow(it) }
                }

                else -> return null
            }
        }

        fun readGroupPermissionTable(name: String, guild: String): List<GroupPermission> {
            val list = mutableListOf<GroupPermission>()
            transaction (DataBase.db) {
                Tables.GroupPermission.select {
                    Tables.GroupPermission.name eq name and (Tables.GroupPermission.guild eq guild)
                }.forEach {
                    list.add(createData.createGroupPermissionByResultRow(it))
                }
            }
            return list
        }

        fun readUserPermissionTable(user: String, guild: String): List<UserPermission> {
            val list = mutableListOf<UserPermission>()
            transaction (DataBase.db) {
                Tables.UserPermission.select {
                    Tables.UserPermission.user eq user and (Tables.UserPermission.guild eq guild)
                }.forEach {
                    list.add(createData.createUserPermissionByResultRow(it))
                }
            }
            return list
        }

        fun readUserPermissionTable(user: String): List<UserPermission> {
            val list = mutableListOf<UserPermission>()
            transaction (DataBase.db) {
                Tables.UserPermission.select { Tables.UserPermission.user eq user }.forEach {
                    list.add(createData.createUserPermissionByResultRow(it))
                }
            }
            return list
        }

        fun getGroupPermission(name: String, guild: String, permission: String): Boolean? {
            var bool: Boolean? = null
            transaction (DataBase.db) {
                val query: Query = Tables.GroupPermission.select {
                    Tables.GroupPermission.name eq name and
                            (Tables.GroupPermission.guild eq guild) and
                            (Tables.GroupPermission.permission eq permission)
                }
                bool = if (query.empty()) {
                    null
                } else {
                    query.first()[Tables.GroupPermission.bool]
                }
            }
            return bool
        }

        fun getUserPermission(name: String, guild: String, permission: String): Boolean? {
            var bool: Boolean? = null
            transaction (DataBase.db) {
                val query: Query = Tables.UserPermission.select {
                    Tables.UserPermission.user eq name and
                            (Tables.UserPermission.guild eq guild) and
                            (Tables.UserPermission.permission eq permission)
                }
                bool = if (query.empty()) {
                    null
                } else {
                    query.first()[Tables.UserPermission.bool]
                }
            }
            return bool
        }

        fun readFilter(guild: String): List<String> {
            val list = mutableListOf<String>()
            transaction (DataBase.db) {
                Tables.Filter.select { Tables.Filter.guild eq guild }.forEach {
                    list.add(it[Tables.Filter.filter])
                }
            }
            return list
        }
    }
}