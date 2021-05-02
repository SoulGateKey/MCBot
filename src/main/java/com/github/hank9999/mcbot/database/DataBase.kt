package com.github.hank9999.mcbot.database

import com.github.hank9999.mcbot.utils.Config
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DataBase {
    companion object {
        val db by lazy {
            if (Config.DataBase.MySQL.enable != true) {
                Database.connect("jdbc:h2:./database", driver = "org.h2.Driver")
            } else {
                Database.connect("jdbc:mysql://${Config.DataBase.MySQL.host}:${Config.DataBase.MySQL.port}/${Config.DataBase.MySQL.database}",
                    driver = "com.mysql.cj.jdbc.Driver",
                    user = Config.DataBase.MySQL.user ?: "", password = Config.DataBase.MySQL.password ?: "")
            }
        }
        var status = false

        fun init() {
            checkTable()
        }

        private fun checkTable() {
            transaction (db) {
                if (!SchemaUtils.checkCycle(Tables.Token)) {
                    SchemaUtils.create(Tables.Token)
                }
                if (!SchemaUtils.checkCycle(Tables.UserPermission)) {
                    SchemaUtils.create(Tables.UserPermission)
                }
                if (!SchemaUtils.checkCycle(Tables.GroupPermission)) {
                    SchemaUtils.create(Tables.GroupPermission)
                }
                if (!SchemaUtils.checkCycle(Tables.Filter)) {
                    SchemaUtils.create(Tables.Filter)
                }
                status = true
            }
        }
    }
}