package com.funny.translation.translate.ui.social.main

import android.content.Intent
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proxy.logic.AccountProvider
import com.example.proxy.logic.ConversationProvider
import com.example.proxy.models.ActionResult
import com.example.proxy.models.ServerState
import com.example.proxy.provider.IAccountProvider
import com.example.proxy.provider.IConversationProvider
import com.funny.translation.translate.ui.social.AppTheme
import com.funny.translation.translate.ui.social.MainPageBottomBarViewState
import com.funny.translation.translate.ui.social.MainPageDrawerViewState
import com.funny.translation.translate.ui.social.MainPageTab
import com.funny.translation.translate.ui.social.MainPageTopBarViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val conversationProvider: IConversationProvider = ConversationProvider()
    private val accountProvider: IAccountProvider = AccountProvider()

    var loadingDialogVisible by mutableStateOf(value = false)
        private set

    val topBarViewState by mutableStateOf(
        value = MainPageTopBarViewState(
            openDrawer = ::openDrawer
        )
    )

    val bottomBarViewState by mutableStateOf(
        value = MainPageBottomBarViewState(
            selectedTab = mutableStateOf(value = MainPageTab.Conversation),
            unreadMessageCount = mutableLongStateOf(value = 0),
            onClickTab = ::onClickTab
        )
    )

    val drawerViewState by mutableStateOf(
        value = MainPageDrawerViewState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            personProfile = mutableStateOf(value = accountProvider.personProfile.value),
            previewImage = ::previewImage,
            switchTheme = ::switchTheme,
            logout = ::logout,
            updateProfile = ::updateProfile
        )
    )

    private val _serverConnectState = MutableStateFlow(value = ServerState.Connected)

    val serverConnectState: SharedFlow<ServerState> = _serverConnectState

    init {
        viewModelScope.launch {
            launch {
                conversationProvider.totalUnreadMessageCount.collect {
                    bottomBarViewState.unreadMessageCount.value = it
                }
            }
            launch {
                accountProvider.personProfile.collect {
                    drawerViewState.personProfile.value = it
                }
            }
            launch {
                accountProvider.serverConnectState.collect {
                    _serverConnectState.emit(value = it)
                    if (it == ServerState.Connected) {
                        requestData()
                    }
                }
            }
            launch {
                requestData()
            }
        }
    }

    private fun requestData() {
        conversationProvider.refreshTotalUnreadMessageCount()
        accountProvider.refreshPersonProfile()
    }

    private fun onClickTab(mainPageTab: MainPageTab) {
        bottomBarViewState.selectedTab.value = mainPageTab
    }

    private fun logout() {
        viewModelScope.launch {
            loadingDialog(visible = true)
            when (val result = accountProvider.logout()) {
                is ActionResult.Success -> {
//                    AccountProvider.onUserLogout()
                }

                is ActionResult.Failed -> {
//                    showToast(msg = result.reason)
                }
            }
            loadingDialog(visible = false)
        }
    }

    private suspend fun openDrawer() {
        drawerViewState.drawerState.open()
    }

    private fun updateProfile() {
//        val intent = Intent(context, ProfileUpdateActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        context.startActivity(intent)
    }

    private fun previewImage(imageUrl: String) {
//        if (imageUrl.isNotBlank()) {
//            PreviewImageActivity.navTo(context = context, imageUri = imageUrl)
//        }
    }

    private fun switchTheme() {
//        val nextTheme = AppThemeProvider.appTheme.nextTheme()
//        AppThemeProvider.onAppThemeChanged(appTheme = nextTheme)
    }

//    private fun AppTheme.nextTheme(): AppTheme {
//        val values = AppTheme.entries
//        return values.getOrElse(ordinal + 1) {
//            values[0]
//        }
//    }

    private fun loadingDialog(visible: Boolean) {
        loadingDialogVisible = visible
    }

}