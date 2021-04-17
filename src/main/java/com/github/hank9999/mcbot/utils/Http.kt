package com.github.hank9999.mcbot.utils

import com.github.hank9999.mcbot.types.HttpResponse
import okhttp3.*


class Http {
    fun get(url: String, headers: Map<String, String>): HttpResponse {
        val client = OkHttpClient()
        val builder = Request.Builder().url(url)
        for (item in headers.entries) {
            builder.addHeader(item.key, item.value)
        }
        val request = builder.build()

        client.newCall(request).execute().use { response ->
            val h = mutableMapOf<String, String>()
            for ((name, value) in response.headers) {
                h[name] = value
            }

            return HttpResponse(response.code, response.body!!.string(), h)
        }
    }

//    fun getAsynchronous(url: String, headers: Map<String, String>) {
//        val client = OkHttpClient()
//        val builder = Request.Builder().url(url)
//        for (item in headers.entries) {
//            builder.addHeader(item.key, item.value)
//        }
//        val request = builder.build()
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                response.use {}
//            }
//        })
//    }
}