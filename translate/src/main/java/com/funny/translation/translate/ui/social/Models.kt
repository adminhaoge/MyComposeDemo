package com.funny.translation.translate.ui.social

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import com.example.proxy.models.PersonProfile

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
enum class MainPageTab {
    Conversation,
    Friendship,
    Person;
}

@Stable
data class MainPageDrawerViewState(
    val drawerState: DrawerState,
    val personProfile: MutableState<PersonProfile>,
    val previewImage: (String) -> Unit,
    val switchTheme: () -> Unit,
    val updateProfile: () -> Unit,
    val logout: () -> Unit
)

@Stable
data class MainPageTopBarViewState(
    val openDrawer: suspend () -> Unit
)

@Stable
data class MainPageBottomBarViewState(
    val selectedTab: MutableState<MainPageTab>,
    val unreadMessageCount: MutableState<Long>,
    val onClickTab: (MainPageTab) -> Unit
)

@Stable
enum class AppTheme {
    Light,
    Dark,
    Gray;
}