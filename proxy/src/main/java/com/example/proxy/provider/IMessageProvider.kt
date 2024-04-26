package com.example.proxy.provider

import com.example.proxy.models.Chat
import com.example.proxy.models.LoadMessageResult
import com.example.proxy.models.Message
import kotlinx.coroutines.channels.Channel

interface IMessageProvider {

    interface MessageListener {
        fun onReceiveMessage(message: Message)
    }

    fun startReceive(chat: Chat, messageListener: MessageListener)
    fun stopReceive(messageListener: MessageListener)
    suspend fun sendText(chat: Chat, text: String): Channel<Message>
    suspend fun sendImage(chat: Chat, imagePath: String): Channel<Message>
    suspend fun getHistoryMessage(chat: Chat, lastMessage: Message?): LoadMessageResult
    fun cleanConversationUnreadMessageCount(chat: Chat)
}