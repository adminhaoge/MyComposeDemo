package com.example.proxy.models

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import kotlinx.android.parcel.Parcelize

sealed class Chat(open val id: String) : Parcelable{
    @Parcelize
    class PrivateChat(override val id: String): Chat(id)

    @Parcelize
    class GroupChat(override val id: String) : Chat(id = id)
}