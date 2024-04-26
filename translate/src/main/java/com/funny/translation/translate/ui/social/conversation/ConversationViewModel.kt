package com.funny.translation.translate.ui.social.conversation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proxy.logic.ConversationProvider
import com.example.proxy.models.ActionResult
import com.example.proxy.models.Chat
import com.example.proxy.models.Conversation
import com.example.proxy.models.ConversationType
import com.example.proxy.provider.IConversationProvider
import com.funny.translation.translate.ui.social.ContextProvider
import com.funny.translation.translate.ui.social.chat.ChatActivity
import kotlinx.coroutines.launch

class ConversationViewModel: ViewModel() {
    private val conversationProvider: IConversationProvider = ConversationProvider()

    val pageViewState by mutableStateOf(
        value = ConversationPageViewState(
            listState = mutableStateOf(
                value = LazyListState(
                    firstVisibleItemIndex = 0,
                    firstVisibleItemScrollOffset = 0
                )
            ),
            conversationList = mutableStateOf(value = emptyList()),
            onClickConversation = ::onClickConversation,
            deleteConversation = ::deleteConversation,
            pinConversation = ::pinConversation
        )
    )

    init {
        viewModelScope.launch {
            conversationProvider.conversationList.collect {
                pageViewState.conversationList.value = it
            }
        }
        conversationProvider.refreshConversationList()
    }

    private fun onClickConversation(conversation: Conversation) {
        when (conversation.type) {
            ConversationType.C2C -> {
                ChatActivity.navTo(
                    context = ContextProvider.context,
                    chat = Chat.PrivateChat(id = conversation.id)
                )
            }

            ConversationType.Group -> {
                ChatActivity.navTo(
                    context = ContextProvider.context,
                    chat = Chat.GroupChat(id = conversation.id)
                )
            }
        }
    }

    private fun deleteConversation(conversation: Conversation) {
        viewModelScope.launch {
            val result = when (conversation.type) {
                ConversationType.C2C -> {
                    conversationProvider.deleteC2CConversation(userId = conversation.id)
                }

                ConversationType.Group -> {
                    conversationProvider.deleteGroupConversation(groupId = conversation.id)
                }
            }
            when (result) {
                is ActionResult.Success -> {
                    conversationProvider.refreshConversationList()
                }

                is ActionResult.Failed -> {
//                    showToast(msg = result.reason)
                }
            }
        }
    }

    private fun pinConversation(conversation: Conversation, pin: Boolean) {
        viewModelScope.launch {
            conversationProvider.pinConversation(
                conversation = conversation,
                pin = pin
            )
        }
    }
}