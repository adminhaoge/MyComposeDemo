package com.funny.translation.translate.ui.social.chat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import com.example.proxy.models.Chat
import com.example.proxy.models.GroupMemberProfile
import com.example.proxy.models.GroupProfile
import com.example.proxy.models.Message

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
data class ChatPageViewState(
    val chat: Chat,
    val listState: LazyListState,
    val topBarTitle: MutableState<String>,
    val messageList: MutableState<List<Message>>
)

@Stable
data class LoadMessageViewState(
    val refreshing: MutableState<Boolean>,
    val loadFinish: MutableState<Boolean>
)

@Stable
data class ChatPageAction(
    val onClickAvatar: (Message) -> Unit,
    val onClickMessage: (Message) -> Unit,
    val onLongClickMessage: (Message) -> Unit
)

@Stable
data class GroupProfilePageViewState(
    val groupProfile: MutableState<GroupProfile?>,
    val memberList: MutableState<List<GroupMemberProfile>>
)

@Stable
data class GroupProfilePageAction(
    val setAvatar: (String) -> Unit,
    val quitGroup: () -> Unit,
    val onClickMember: (GroupMemberProfile) -> Unit
)