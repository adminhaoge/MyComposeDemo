package com.funny.translation.translate.ui.social.friendship

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proxy.logic.FriendshipProvider
import com.example.proxy.logic.GroupProvider
import com.example.proxy.models.ActionResult
import com.example.proxy.models.Chat
import com.example.proxy.models.GroupProfile
import com.example.proxy.models.PersonProfile
import com.funny.translation.translate.ui.social.ContextProvider
import com.funny.translation.translate.ui.social.chat.ChatActivity
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FriendshipViewModel : ViewModel() {
    private val friendshipProvider = FriendshipProvider()
    private val groupProvider = GroupProvider()

    val pageViewState by mutableStateOf(
        value = FriendshipPageViewState(
            listState = mutableStateOf(
                value = LazyListState(
                    firstVisibleItemIndex = 0,
                    firstVisibleItemScrollOffset = 0
                )
            ),
            joinedGroupList = mutableStateOf(value = emptyList()),
            friendList = mutableStateOf(value = emptyList()),
            onClickGroupItem = ::onClickGroupItem,
            onClickFriendItem = ::onClickFriendItem,
            showFriendshipDialog = ::showFriendshipDialog
        )
    )

    val friendshipDialogViewState by mutableStateOf(
        FriendshipDialogViewState(
            visible = mutableStateOf(value = false),
            dismissDialog = ::dismissFriendshipDialog,
            joinGroup = ::joinGroup,
            addFriend = ::addFriend
        )
    )

    init {
        viewModelScope.launch {
            launch {
                groupProvider.joinedGroupList.collect {
                    pageViewState.joinedGroupList.value = it
                }
            }
            launch {
                friendshipProvider.friendList.collect {
                    pageViewState.friendList.value = it
                }
            }
        }
        groupProvider.refreshJoinedGroupList()
        friendshipProvider.refreshFriendList()
    }

    private fun onClickGroupItem(groupProfile: GroupProfile) {
        ChatActivity.navTo(
            context = ContextProvider.context,
            chat = Chat.GroupChat(id = groupProfile.id)
        )
    }

    private fun onClickFriendItem(personProfile: PersonProfile) {
//        FriendProfileActivity.navTo(
//            context = context,
//            friendId = personProfile.id
//        )
    }

    fun showFriendshipDialog() {
        friendshipDialogViewState.visible.value = true
    }

    private fun dismissFriendshipDialog() {
        friendshipDialogViewState.visible.value = false
    }

    private fun addFriend(userId: String) {
        viewModelScope.launch {
            val formatUserId = userId.lowercase()
            when (val result = friendshipProvider.addFriend(friendId = formatUserId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400)
                    ToastUtils.show("添加成功")
                    ChatActivity.navTo(
                        context = ContextProvider.context,
                        chat = Chat.PrivateChat(id = formatUserId)
                    )
                    dismissFriendshipDialog()
                }

                is ActionResult.Failed -> {
                    ToastUtils.show(result.reason)
                }
            }
        }
    }

    private fun joinGroup(groupId: String) {
        viewModelScope.launch {
            when (val result = groupProvider.joinGroup(groupId = groupId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 800)
                    ToastUtils.show("加入成功")
                    groupProvider.refreshJoinedGroupList()
                    dismissFriendshipDialog()
                }

                is ActionResult.Failed -> {
                    ToastUtils.show(result.reason)
                }
            }
        }
    }
}