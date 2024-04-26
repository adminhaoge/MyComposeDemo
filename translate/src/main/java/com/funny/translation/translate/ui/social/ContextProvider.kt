package com.funny.translation.translate.ui.social

import android.app.Application

object ContextProvider {

    lateinit var context: Application
        private set

    fun init(application: Application) {
        context = application
    }

}