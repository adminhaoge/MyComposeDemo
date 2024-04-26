package com.example.proxy.provider

import android.app.Application
import com.example.proxy.models.ActionResult
import com.example.proxy.models.PersonProfile
import com.example.proxy.models.ServerState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface IAccountProvider {

    val personProfile: StateFlow<PersonProfile>

    val serverConnectState: SharedFlow<ServerState>

    fun init(application: Application)

    suspend fun login(userId: String): ActionResult

    suspend fun logout(): ActionResult

    suspend fun getPersonProfile(): PersonProfile?

    fun refreshPersonProfile()

    suspend fun updatePersonProfile(
        faceUrl: String,
        nickname: String,
        signature: String
    ): ActionResult

}