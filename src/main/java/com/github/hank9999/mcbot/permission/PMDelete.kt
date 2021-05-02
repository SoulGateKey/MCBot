package com.github.hank9999.mcbot.permission

import com.github.hank9999.mcbot.database.DBDelete
import com.github.hank9999.mcbot.types.GroupPermission
import com.github.hank9999.mcbot.types.UserPermission

class PMDelete {
    companion object {
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