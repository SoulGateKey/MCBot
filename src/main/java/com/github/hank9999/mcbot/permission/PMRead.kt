package com.github.hank9999.mcbot.permission

import com.github.hank9999.mcbot.database.DBRead
import com.github.hank9999.mcbot.types.GroupPermission
import com.github.hank9999.mcbot.types.UserPermission

class PMRead {
    companion object {
        fun getUserPermissions(user: String): List<UserPermission> {
            return DBRead.readUserPermissionTable(user)
        }

        fun getUserPermissions(user: String, guild: String): List<UserPermission> {
            return DBRead.readUserPermissionTable(user, guild)
        }

        fun getUserPermission(user: String, guild: String, permission: String): Boolean? {
            return DBRead.getUserPermission(user, guild, permission)
        }

        fun getUserGroups(user: String, guild: String): List<String> {
            val userPermissions = DBRead.readUserPermissionTable(user, guild)
            val list = mutableListOf<String>()
            userPermissions.groupBy { it.permission.startsWith("group.") }[true].orEmpty().forEach {
                list.add(it.permission.removePrefix("group."))
            }
            return list
        }

        fun getGroupPermission(user: String, guild: String, permission: String): Boolean? {
            return DBRead.getGroupPermission(user, guild, permission)
        }

        fun getGroupPermissions(user: String, guild: String): List<GroupPermission> {
            return DBRead.readGroupPermissionTable(user, guild)
        }
    }
}