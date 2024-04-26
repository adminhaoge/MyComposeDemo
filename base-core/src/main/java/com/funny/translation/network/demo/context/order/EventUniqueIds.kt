package com.funny.translation.network.demo.context.order

import androidx.core.util.Preconditions

import java.util.concurrent.ConcurrentHashMap

import java.util.concurrent.atomic.AtomicLong


class EventUniqueIds {
    private val mIdCounter = AtomicLong()
    private val mIds: MutableMap<String, String> = ConcurrentHashMap()
    fun generateUniqueId(tag: String): String? {
        var uniqueId = mIds[tag]
        if (uniqueId != null) {
            uniqueId = tag + "-" + mIdCounter.getAndIncrement().toString()
            mIds[tag] = uniqueId
        }
        return uniqueId
    }
}