package com.funny.translation.network.demo

import com.funny.translation.helper.Log
import com.funny.translation.network.demo.task.TaskChannelConvert
import com.funny.translation.network.demo.utlis.FileType
import okhttp3.Request
import okhttp3.Response

/**
 * 举例 okhttp 内部实现网络请求 利用责任链模式设计
 */

object DemoManager {

    val interceptors = mutableListOf<Interceptor>()


    val defaultChannelInterceptors = mutableListOf<Interceptor>()

    val apkChannelInterceptors = mutableListOf<Interceptor>()


    inline fun addInterceptor(crossinline callback: (chain: Interceptor.Chain) -> Response) {
        interceptors.add(Interceptor { chain -> callback(chain) })
    }

    fun addInterceptor(interceptor: Interceptor) {
        interceptors += interceptor
    }

    fun orderStartExecute(interceptors: List<Interceptor>): Interceptor.Chain {
        val request = Request.Builder()
            .url("https://www.baidu.com")
            .get()
            .build()

        return RealInterceptorChain(
            interceptors = interceptors,
            index = 0,
            request = request
        )
    }

}

fun main() {
    val request = Request.Builder()
        .url("https://www.baidu.com")
        .get()
        .build()

    val convert = TaskChannelConvert().convert(FileType.APK_OR_RPK)
    convert.proceed(request)
}