package com.funny.translation.network.demo.context

import androidx.core.util.Preconditions
import com.funny.translation.network.demo.context.order.EventUniqueIds
import com.funny.translation.network.demo.context.order.Order


class BaseChannelContext(
    order: Order?,
    ids: EventUniqueIds?,
    readerFactory: ReaderFactory?
) : ChannelContext {
    protected val mOrder: Order?
    protected val mIds: EventUniqueIds
    protected val mReaderFactory: ReaderFactory

    @get:Synchronized
    var isCancelled: Boolean
        protected set

    init {
        mOrder = Preconditions.checkNotNull(order)
        mIds = Preconditions.checkNotNull(ids)
        mReaderFactory = Preconditions.checkNotNull(readerFactory)
        isCancelled = false
    }

    override val order: Order?
        get() = mOrder
    override val progressInterval: Long
        get() = 0
    override val progressTimeInterval: Long
        get() = DEFAULT_PROGRESS_TIME_INTERVAL.toLong()
    override val checkDiskSpaceTimeInterval: Long
        get() = DEFAULT_CHECK_DISK_SPACE_TIME_INTERVAL.toLong()

    override val eventIdGenerator: EventUniqueIds
        get() = mIds

    fun getDownloadReader(progress: Long): DownloadReader {
        return mReaderFactory.get(mOrder.current(), progress)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val context = other as BaseChannelContext
        return mOrder?.equals(context.mOrder) ?: (context.mOrder == null)
    }

    override fun hashCode(): Int {
        return mOrder?.hashCode() ?: 0
    }

    override fun close() {

    }
}

