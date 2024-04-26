package com.funny.translation.network.demo

import com.funny.translation.network.demo.context.ChannelContext
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response

class RealInterceptorChain(
    private val interceptors: List<Interceptor>,
    private val index: Int,
    private val request: ChannelContext
): Interceptor.Chain {

    internal fun copy(
        index: Int = this.index,
        request: ChannelContext = this.request
    ) = RealInterceptorChain(interceptors, index, request)


    override fun request(): ChannelContext = request

    override fun proceed(request: ChannelContext): Response {
        check(index < interceptors.size)

        val next = copy(index = index + 1, request = request)
        val interceptor = interceptors[index]

        val response = interceptor.intercept(next) ?: throw NullPointerException(
            "interceptor $interceptor returned null")

        return response
    }
}