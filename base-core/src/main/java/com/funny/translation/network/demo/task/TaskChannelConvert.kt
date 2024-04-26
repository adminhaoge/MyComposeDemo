package com.funny.translation.network.demo.task

import com.funny.translation.network.demo.DemoManager
import com.funny.translation.network.demo.Interceptor
import com.funny.translation.network.demo.apkchannel.ApkChannel
import com.funny.translation.network.demo.apkchannel.VerifyApkUsesSdkChannel
import com.funny.translation.network.demo.normal.DownloadNetworkChannel
import com.funny.translation.network.demo.normal.DownloadReadResponseChannel
import com.funny.translation.network.demo.normal.DownloadRetryNetworkChannel
import com.funny.translation.network.demo.normal.PrepareRecordChannel
import com.funny.translation.network.demo.normal.VerifyCrc32Channel
import com.funny.translation.network.demo.utlis.FileType

class TaskChannelConvert : TaskChannel.Factory<FileType, Interceptor.Chain>() {
    override fun convert(type: FileType): Interceptor.Chain {
        initDefaultChannel()
        return when (type) {
            FileType.APK_OR_RPK -> {
                DownloadApkChannel.INSTANCE.executeChannelConvert()
            }
            FileType.HPK -> {
                DownloadHpkChannel.INSTANCE.executeChannelConvert()
            }
            FileType.ZIP -> {
                DownloadZipChannel.INSTANCE.executeChannelConvert()
            }
            FileType.XAPK_OR_APKS -> {
                DownloadXapkChannel.INSTANCE.executeChannelConvert()
            }
            else -> {
                NormalChannel.INSTANCE.executeChannelConvert()
            }
        }
    }

    private fun initDefaultChannel() {
        DemoManager.defaultChannelInterceptors.apply {
            this + PrepareRecordChannel()
            this + DownloadNetworkChannel()
            this + DownloadReadResponseChannel()
            this + DownloadRetryNetworkChannel()
            this + VerifyCrc32Channel()
        }
    }

    internal class DownloadApkChannel : TaskChannel<Interceptor.Chain> {
        companion object {
            val INSTANCE = DownloadApkChannel()
        }

        override fun executeChannelConvert(): Interceptor.Chain {
            DemoManager.apkChannelInterceptors.apply {
                addAll(DemoManager.defaultChannelInterceptors)
                this + ApkChannel()
                this + VerifyApkUsesSdkChannel()
            }

            return DemoManager.orderStartExecute(DemoManager.apkChannelInterceptors)
        }
    }

    internal class DownloadHpkChannel : TaskChannel<Interceptor.Chain> {
        companion object {
            val INSTANCE = DownloadHpkChannel()
        }

        override fun executeChannelConvert(): Interceptor.Chain {
            TODO("Not yet implemented")
        }
    }

    internal class DownloadZipChannel : TaskChannel<Interceptor.Chain> {
        companion object {
            val INSTANCE = DownloadZipChannel()
        }

        override fun executeChannelConvert(): Interceptor.Chain {
            TODO("Not yet implemented")
        }
    }

    internal class DownloadXapkChannel : TaskChannel<Interceptor.Chain> {
        companion object {
            val INSTANCE = DownloadXapkChannel()
        }

        override fun executeChannelConvert(): Interceptor.Chain {
            TODO("Not yet implemented")
        }
    }

    internal class NormalChannel : TaskChannel<Interceptor.Chain> {
        companion object {
            val INSTANCE = NormalChannel()
        }

        override fun executeChannelConvert(): Interceptor.Chain {
            TODO("Not yet implemented")
        }
    }

}