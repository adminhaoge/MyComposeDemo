package com.funny.translation.network.demo.task

interface TaskChannel<C> {

    fun executeChannelConvert() : C

    abstract class Factory<F, C> {
        open fun convert(type: F): C? {
            return null
        }
    }
}