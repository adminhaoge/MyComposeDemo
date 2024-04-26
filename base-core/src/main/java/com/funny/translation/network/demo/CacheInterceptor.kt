package com.funny.translation.network.demo

import com.funny.translation.network.OkHttpUtils
import okhttp3.Response

class CacheInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        println("CacheInterceptor = start")

//        val response = chain.proceed(chain.request())

//        val response = OkHttpUtils.getResponse(chain.request().url.toString(), null, null, null)

        println("CacheInterceptor = end")

        return null
    }
}