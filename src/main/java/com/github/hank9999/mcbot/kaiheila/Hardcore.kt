package com.github.hank9999.mcbot.kaiheila

sealed class Hardcore {
    sealed class Api {
        sealed class Websocket {
            companion object {
                const val gatewayUrl: String = "https://www.kaiheila.cn/api/v3/gateway/index?compress=0"
            }
        }
    }
}