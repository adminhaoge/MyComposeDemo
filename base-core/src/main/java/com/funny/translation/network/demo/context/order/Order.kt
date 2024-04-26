package com.funny.translation.network.demo.context.order

import android.app.Notification
import androidx.annotation.Nullable
import androidx.core.util.Preconditions
import com.funny.translation.network.demo.utlis.FileType


class Order private constructor(
    links: MutableList<Link>, dir: String?, name: String?, suffix: Suffix?, fileType: FileType?,
    notification: Notification?, crc32: Long, checkSignature: Boolean, orderType: OrderType?
) {
    /**
     * link used to connect network to download file
     */
    private val mLinks: MutableList<Link>?
    private var mIterator: Iterator<Link>
    private var mLastLink: Link?

    /**
     * 下载开始时的link（统计时使用）
     */
    private val mBeginLink: Link?

    /**
     * file directory
     */
    val dir: String?

    /**
     * file name without suffix
     */
    val name: String?

    /**
     * determine whether to check the signature for apk or hpk file.
     */
    val isCheckSignature: Boolean

    /**
     * signature of apk file after downloading.
     */
    var signature: String? = null

    /**
     * file name suffix
     * When it is [Suffix.EMPTY], don't rename file.
     */
    private val mSuffix: Suffix

    /**
     * type that determine which channel would be chosen(apk, hpk, normal).
     * When it is [FileType.EMPTY], don't rename file.
     */
    val fileType: FileType?

    /**
     * Nullable
     */
    @get:Nullable
    val notification: Notification?

    /**
     * crc32 value to verify file
     */
    val crc32: Long
    val orderType: OrderType?

    init {
        mLinks = links
        mIterator = mLinks.iterator()
        val next = next()
        mLastLink = next ?: mLastLink
        mBeginLink = mLastLink
        this.dir = dir
        this.name = name
        mSuffix = suffix ?: Suffix.EMPTY
        this.fileType = fileType
        this.notification = notification
        this.crc32 = crc32
        isCheckSignature = checkSignature
        this.orderType = orderType
    }

    val suffix: Suffix
        get() = mSuffix

    @get:Synchronized
    val links: List<Link>
        get() {
            val copy: MutableList<Link> = ArrayList()
            for (link in mLinks!!) {
                copy.add(link)
            }
            return copy
        }

    @Synchronized
    fun current(): Link? {
        return mLastLink
    }

    @Synchronized
    fun begin(): Link? {
        return mBeginLink
    }

    @Synchronized
    operator fun next(): Link? {
        var nextLink: Link? = null
        while (mIterator.hasNext()) {
            val next = mIterator.next()
            if (!next.isUsed) {
                next.used()
                nextLink = next
                mLastLink = next
                break
            }
        }
        /**
         * 初次设置，全部地址就已经是被使用，纯粹是补救方案，不建议使用
         */
        if (mLastLink == null && FP.size(mLinks) > 0) {
            mLastLink = mLinks!![0]
            Log.e(TAG, "no unused link to connect")
        }
        return nextLink
    }

    @Synchronized
    fun moveToFirst(link: Link) {
        if (mLinks!!.contains(link)) {
            mLinks.remove(link)
            mLinks.add(0, link)
            mIterator = mLinks.iterator()
            next()
        }
    }

    fun containLink(linkUrl: String?): Boolean {
        if (FP.empty(mLinks)) return false
        for (link in mLinks!!) {
            if (link.getUrl().equals(linkUrl)) return true
        }
        return false
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as Order
        if (mLinks != null && that.mLinks != null) {
            for (url in that.mLinks) {
                if (mLinks.contains(url)) {
                    return true
                }
            }
        } else if (mLinks == null && that.mLinks == null) {
            return true
        }
        return false
    }

    override fun toString(): String {
        return "Order{" +
                "mName='" + name + '\'' +
                ", mLinks=" + mLinks +
                ", mLastLink=" + mLastLink +
                ", mDir='" + dir + '\'' +
                ", mSuffix=" + mSuffix +
                ", mFileType=" + fileType +
                '}'
    }

    /**
     * any order's hashcode is the same
     *
     * @return
     */
    override fun hashCode(): Int {
        return 0
    }

    class Builder {
        private var mDir: String? = null
        private var mName: String? = null
        private var mSuffix: Suffix? = null
        private var mFileType: FileType? = null
        private var mNotification: Notification? = null
        private var mCrc32: Long = 0
        private var mCheckSignature = true
        private val mLinks: MutableList<Link> = ArrayList()
        private var mOrderType: OrderType? = null
        fun setOrderType(orderType: OrderType?): Builder {
            mOrderType = orderType
            return this
        }

        fun setDir(dir: String?): Builder {
            mDir = dir
            return this
        }

        fun setName(name: String?): Builder {
            mName = name
            return this
        }

        fun setSuffix(suffix: Suffix?): Builder {
            mSuffix = suffix
            return this
        }

        fun setFileType(fileType: FileType?): Builder {
            mFileType = fileType
            return this
        }

        fun setNotification(notification: Notification?): Builder {
            mNotification = notification
            return this
        }

        fun setCrc32(crc32: Long): Builder {
            mCrc32 = crc32
            return this
        }

        fun setCheckSignature(checkSignature: Boolean): Builder {
            mCheckSignature = checkSignature
            return this
        }

        fun addLink(url: String?, readerType: Link.ReaderType?): Builder {
            mLinks.add(Link(url, readerType))
            return this
        }

        fun addLink(url: String?, readerType: Link.ReaderType?, rate: Int): Builder {
            mLinks.add(Link(url, readerType, rate))
            return this
        }

        fun addLink(url: String?, readerType: Link.ReaderType?, rate: Int, used: Boolean): Builder {
            mLinks.add(Link(url, readerType, rate, used))
            return this
        }

        fun addLink(link: Link): Builder {
            mLinks.add(link)
            return this
        }

        fun addLink(links: List<Link>?): Builder {
            mLinks.addAll(links!!)
            return this
        }

        fun build(): Order {
            Preconditions.checkArgument(!FP.empty(mLinks))
            Preconditions.checkArgument(!FP.empty(mDir))
            Preconditions.checkArgument(!FP.empty(mName))
            Preconditions.checkNotNull<Any>(mSuffix)
            Preconditions.checkNotNull(mFileType)
            return Order(
                mLinks, mDir, mName, mSuffix, mFileType, mNotification, mCrc32,
                mCheckSignature, mOrderType
            )
        }

        companion object {
            fun newBuilder(): Builder {
                return Builder()
            }
        }
    }

    // 为了使用 EventNotifyCenter时, 区分类型
    enum class OrderType {
        UNKNOWN,

        // 未知
        GAME,

        // 游戏
        RING,

        // 铃声
        VERSION,

        // 版本升级
        THEME,

        // 主题
        VIDEO,

        // 视频
        PLUGIN,

        // 插件
        VIDEOLIB_LOADER,

        // 视频插件
        TEST
        // 测试
    }

    companion object {
        const val TAG = "Order"
    }
}