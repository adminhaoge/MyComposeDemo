package com.funny.translation.network.x

import androidx.annotation.Keep
import com.funny.translation.core.R
import com.funny.translation.helper.string

@Keep
@kotlinx.serialization.Serializable
data class Result<T : Any>(val code: Int, val message: String? = null, val data: T? = null, val error_msg:String? = null) {
    val displayErrorMsg get() = error_msg ?: message ?: string(R.string.unknown_error)

    fun onSuccess(data: (T) -> Result<T>) : Result<T> {
        return this
    }


    fun onFailure(throwable: (Throwable) -> Result<Throwable>): Result<T> {
        return this
    }
}
