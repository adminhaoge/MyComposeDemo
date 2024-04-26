package com.funny.translation.translate.ui.social.extend

import android.os.SystemClock

class ComposeOnClick(private val onClick: () -> Unit) : Function0<Unit> {

    companion object {

        private const val MIN_DURATION = 250L

        private var lastClickTime = 0L

    }

    override fun invoke() {
        val currentTime = SystemClock.elapsedRealtime()
        val isEnabled = currentTime - lastClickTime > MIN_DURATION
        if (isEnabled) {
            lastClickTime = currentTime
            onClick()
        }
    }

}