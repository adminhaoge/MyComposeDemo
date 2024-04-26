package com.funny.translation.translate.ui.social.friendship

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import com.example.proxy.models.GroupProfile
import com.example.proxy.models.PersonProfile

@Stable
data class FriendshipPageViewState(
    val listState: MutableState<LazyListState>,
    val joinedGroupList: MutableState<List<GroupProfile>>,
    val friendList: MutableState<List<PersonProfile>>,
    val onClickGroupItem: (GroupProfile) -> Unit,
    val onClickFriendItem: (PersonProfile) -> Unit,
    val showFriendshipDialog: () -> Unit
)

@Stable
data class FriendshipDialogViewState(
    val visible: MutableState<Boolean>,
    val dismissDialog: () -> Unit,
    val joinGroup: (groupId: String) -> Unit,
    val addFriend: (userId: String) -> Unit
)