package com.funny.translation

import android.content.Context
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.funny.translation.helper.LocaleUtils
import com.smarx.notchlib.NotchScreenManager

open class BaseActivity : AppCompatActivity() {
    private lateinit var callback: OnBackPressedCallback

    companion object {
        private const val TAG = "BaseActivity"
    }

    protected inline fun <reified T : ViewModel> viewModelsInstance(crossinline create: () -> T): Lazy<T> {
        return viewModels(factoryProducer = {
            object : ViewModelProvider.NewInstanceFactory() {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return create() as T
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 状态栏沉浸
        NotchScreenManager.getInstance().setDisplayInNotch(this)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        reportFullyDrawn()
    }

    override fun attachBaseContext(newBase: Context?) {
        val context = newBase?.let {
            LocaleUtils.getWarpedContext(it)
        }
        super.attachBaseContext(context)
    }
}