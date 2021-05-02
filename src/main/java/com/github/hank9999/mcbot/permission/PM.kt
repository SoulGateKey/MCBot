package com.github.hank9999.mcbot.permission

import com.github.hank9999.mcbot.database.DBCreate
import com.github.hank9999.mcbot.database.DBDelete
import com.github.hank9999.mcbot.database.DBRead
import com.github.hank9999.mcbot.types.GroupPermission
import com.github.hank9999.mcbot.types.UserPermission

class PM {
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

        fun checkUserPermission(user: String, guild: String, permission: String): Boolean {
             return getUserPermission(user, guild, permission) ?: false
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

        fun checkGroupPermission(name: String, guild: String, permission: String): Boolean {
            return getGroupPermission(name, guild, permission) ?: false
        }

        fun checkUser(user: String, guild: String): Boolean {
            return getUserGroups(user, guild).count() != 0
        }

        fun checkPermission(user: String, guild: String, permission: String): Boolean {
            if (checkUserPermission(user, guild, permission)) {
                return true
            } else {
                val groups = getUserGroups(user, guild)
                if (groups.count() == 0) {
                    return checkGroupPermission("group.default", guild, permission)
                } else {
                    groups.forEach {
                        if (checkGroupPermission(it, guild, permission)) {
                            return true
                        }
                    }
                    return false
                }
            }
        }

        fun addGroupPermission(data: GroupPermission): Boolean {
            return DBCreate.createGroupPermission(data)
        }

        fun addUserPermission(data: UserPermission): Boolean {
            return DBCreate.createUserPermission(data)
        }

        fun deleteGroupPermission(name: String, guild: String, permission: String): Boolean {
            return DBDelete.deleteGroupPermission(name, guild, permission)
        }

        fun deleteGroupPermission(data: GroupPermission): Boolean {
            return DBDelete.deleteGroupPermission(data)
        }

        fun deleteUserPermission(user: String, guild: String, permission: String): Boolean {
            return DBDelete.deleteUserPermission(user, guild, permission)
        }

        fun deleteUserPermission(data: UserPermission): Boolean {
            return DBDelete.deleteUserPermission(data)
        }

    }
}