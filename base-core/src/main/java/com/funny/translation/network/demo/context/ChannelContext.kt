package com.funny.translation.network.demo.context

import android.net.wifi.p2p.WifiP2pManager.ChannelListener
import com.funny.translation.network.demo.context.order.EventUniqueIds
import com.funny.translation.network.demo.context.order.Order

import java.io.Closeable


interface ChannelContext : Closeable {
    val order: Order?
    val progressInterval: Long
    val progressTimeInterval: Long
    val checkDiskSpaceTimeInterval: Long
    val eventIdGenerator: EventUniqueIds?
}