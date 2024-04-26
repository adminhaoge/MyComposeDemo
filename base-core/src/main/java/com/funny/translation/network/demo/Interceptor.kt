package com.funny.translation.network.demo

import com.funny.translation.network.demo.context.ChannelContext
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

fun interface Interceptor {
    @Throws(IOException::class)
    fun intercept(chain: Chain): Response

    companion object {
        /**
         * Constructs an interceptor for a lambda. This compact syntax is most useful for inline
         * interceptors.
         *
         * ```
         * val interceptor = Interceptor { chain: Interceptor.Chain ->
         *     chain.proceed(chain.request())
         * }
         * ```
         */
        inline operator fun invoke(crossinline block: (chain: Chain) -> Response): Interceptor =
            Interceptor { block(it) }
    }

    interface Chain {
        fun request(): ChannelContext

        @Throws(IOException::class)
        fun proceed(request: ChannelContext): Response


    }
}