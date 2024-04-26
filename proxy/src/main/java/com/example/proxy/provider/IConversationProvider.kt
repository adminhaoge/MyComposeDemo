package com.example.proxy.provider

import com.example.proxy.models.ActionResult
import com.example.proxy.models.Chat
import com.example.proxy.models.Conversation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.SharedFlow

interface IConversationProvider {

    val conversationList: SharedFlow<ImmutableList<Conversation>>
    val totalUnreadMessageCount: SharedFlow<Long>
    fun refreshConversationList()
    fun refreshTotalUnreadMessageCount()
    fun cleanConversationUnreadMessageCount(chat: Chat)
    suspend fun pinConversation(conversation: Conversation, pin: Boolean): ActionResult
    suspend fun deleteC2CConversation(userId: String): ActionResult
    suspend fun deleteGroupConversation(groupId: String): ActionResult
}