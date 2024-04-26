package com.funny.translation.network.demo.context.order


class Link @JvmOverloads constructor(
    val url: String?,
    val readType: ReaderType,
    rate: Int = Int.MAX_VALUE,
    var isUsed: Boolean = false
) :
    Cloneable {
    val rate: Int

    constructor(url: String?, type: ReaderType, used: Boolean) : this(
        url,
        type,
        Int.MAX_VALUE,
        used
    )

    init {
        this.rate = if (rate > 0) rate else Int.MAX_VALUE
    }

    /**
     * default access control
     */
    fun used() {
        isUsed = true
    }

    override fun toString(): String {
        return "Link{" +
                "mUrl='" + url + '\'' +
                ", mRate=" + rate +
                ", mReadType=" + readType +
                ", mUsed=" + isUsed +
                '}'
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val link = o as Link
        return if (url != null) url == link.url else link.url == null
    }

    override fun hashCode(): Int {
        return if (url != null) url.hashCode() else 0
    }

    public override fun clone(): Link {
        return Link(url, readType, rate)
    }

    enum class ReaderType {
        /**
         * no encrypted reader type
         */
        NORMAL,

        /**
         * no encrypted reader type that limit bandwidth
         */
        NORMAL_RATE,

        /**
         * rpk xor encrypted reader type
         */
        RPK_XOR,

        /**
         * rpk xor encrypted reader type that limit bandwidth
         */
        RPK_XOR_RATE,
        @Deprecated("")
        XOR_DEPRECATED
    }
}