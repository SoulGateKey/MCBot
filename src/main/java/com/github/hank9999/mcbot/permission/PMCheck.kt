package com.github.hank9999.mcbot.permission

class PMCheck {
    companion object {
        fun checkUserPermission(user: String, guild: String, permission: String): Boolean {
            return PMRead.getUserPermission(user, guild, permission) ?: false
        }

        fun checkGroupPermission(name: String, guild: String, permission: String): Boolean {
            return PMRead.getGroupPermission(name, guild, permission) ?: false
        }

        fun checkUser(user: String, guild: String): Boolean {
            return PMRead.getUserGroups(user, guild).isNotEmpty()
        }

        fun checkUserGroupPermission(user: String, guild: String, permission: String): Boolean {
            val groups = PMRead.getUserGroups(user, guild)
            if (groups.isEmpty()) {
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

        fun checkPermission(user: String, guild: String, permission: String): Boolean {
            return if (checkUserPermission(user, guild, permission)) {
                true
            } else {
                checkUserGroupPermission(user, guild, permission)
            }
        }
    }
}