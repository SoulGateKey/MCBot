package com.github.hank9999.mcbot.permission

import com.github.hank9999.mcbot.database.DBCreate
import com.github.hank9999.mcbot.types.GroupPermission
import com.github.hank9999.mcbot.types.UserPermission

class PMAdd {
    companion object {
        fun addGroupPermission(data: GroupPermission): Boolean {
            return DBCreate.createGroupPermission(data)
        }

        fun addUserPermission(data: UserPermission): Boolean {
            return DBCreate.createUserPermission(data)
        }
    }
}