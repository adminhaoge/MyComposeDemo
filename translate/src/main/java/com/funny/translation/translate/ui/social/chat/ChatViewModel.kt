package com.funny.translation.translate.ui.social.chat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proxy.logic.FriendshipProvider
import com.example.proxy.logic.GroupProvider
import com.example.proxy.logic.MessageProvider
import com.example.proxy.models.Chat
import com.example.proxy.models.ImageMessage
import com.example.proxy.models.LoadMessageResult
import com.example.proxy.models.Message
import com.example.proxy.models.MessageState
import com.example.proxy.models.SystemMessage
import com.example.proxy.models.TextMessage
import com.example.proxy.models.TimeMessage
import com.example.proxy.provider.IFriendshipProvider
import com.example.proxy.provider.IGroupProvider
import com.example.proxy.provider.IMessageProvider
import com.funny.translation.translate.ui.social.ComposeChat
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel(private val chat: Chat): ViewModel() {
    companion object {

        private const val TEXT_MSG_MAX_LENGTH = 200

    }

    private val allMessage = mutableListOf<Message>()
    private val lastMessage: Message?
        get() {
            return allMessage.lastOrNull { it !is TimeMessage }
        }

    private val messageListener = object : IMessageProvider.MessageListener {
        override fun onReceiveMessage(message: Message) {
            attachNewMessage(newMessage = message)
            markMessageAsRead()
        }
    }

    val chatPageViewState by mutableStateOf(
        value = ChatPageViewState(
            chat = chat,
            topBarTitle = mutableStateOf(value = ""),
            listState = LazyListState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
            messageList = mutableStateOf(value = emptyList())
        )
    )

    val loadMessageViewState by mutableStateOf(
        value = LoadMessageViewState(
            refreshing = mutableStateOf(value = false),
            loadFinish = mutableStateOf(value = false)
        )
    )

    var textMessageInputted by mutableStateOf(value = TextFieldValue(text = ""))
        private set

    var currentInputSelector by mutableStateOf(value = InputSelector.NONE)
        private set

    private val messageProvider: IMessageProvider = MessageProvider()

    init {
        messageProvider.startReceive(
            chat = chat,
            messageListener = messageListener
        )
        ComposeChat.accountProvider.refreshPersonProfile()
        viewModelScope.launch {
            val name = when(chat) {
                is Chat.PrivateChat -> {
                    val friendshipProvider: IFriendshipProvider = FriendshipProvider()
                    friendshipProvider.getFriendProfile(friendId = chat.id)?.showName
                }

                is Chat.GroupChat -> {
                    val groupProvider: IGroupProvider = GroupProvider()
                    groupProvider.getGroupInfo(groupId = chat.id)?.name
                }
            }?: ""
            chatPageViewState.topBarTitle.value = name
        }
        loadMoreMessage()
    }

    override fun onCleared() {
        super.onCleared()
        messageProvider.stopReceive(messageListener = messageListener)
        markMessageAsRead()
    }

    private fun addMessageToFooter(newMessageList: List<Message>) {
        if (newMessageList.isNotEmpty()) {
            if (allMessage.isNotEmpty()) {
                if (allMessage[allMessage.size - 1].detail.timestamp - newMessageList[0].detail.timestamp > 60) {
                    allMessage.add(TimeMessage(targetMessage = allMessage[allMessage.size - 1]))
                }
            }
            var filteredMsg = 1
            for (index in newMessageList.indices) {
                val currentMsg = newMessageList[index]
                val preMsg = newMessageList.getOrNull(index + 1)
                allMessage.add(currentMsg)
                if (preMsg == null || currentMsg.detail.timestamp - preMsg.detail.timestamp > 60 || filteredMsg >= 10) {
                    allMessage.add(TimeMessage(targetMessage = currentMsg))
                    filteredMsg = 1
                } else {
                    filteredMsg++
                }
            }
            chatPageViewState.messageList.value = allMessage.toList()
        }
    }

    private fun markMessageAsRead() {
        messageProvider.cleanConversationUnreadMessageCount(chat = chat)
    }

    fun loadMoreMessage() {
        viewModelScope.launch {
            loadMessageViewState.refreshing.value = true
            val loadResult = messageProvider.getHistoryMessage(
                chat = chat,
                lastMessage = lastMessage
            )
            val loadFinish = when (loadResult) {
                is LoadMessageResult.Success -> {
                    addMessageToFooter(newMessageList = loadResult.messageList)
                    loadResult.loadFinish
                }

                is LoadMessageResult.Failed -> {
                    false
                }
            }
            loadMessageViewState.refreshing.value = false
            loadMessageViewState.loadFinish.value = loadFinish
        }
    }

    fun filterAllImageMessageUrl(): List<String> {
        return allMessage.mapNotNull {
            (it as? ImageMessage)?.previewImageUrl
        }.reversed()
    }

    fun onInputSelectorChanged(newSelector: InputSelector) {
        currentInputSelector = newSelector
    }

    fun onUserInputChanged(input: TextFieldValue) {
        val newMessage = if (textMessageInputted.text.length >= TEXT_MSG_MAX_LENGTH) {
            if (input.text.length > TEXT_MSG_MAX_LENGTH) {
                return
            }
            input
        } else {
            if (input.text.length > TEXT_MSG_MAX_LENGTH) {
                textMessageInputted.copy(text = input.text.substring(0, TEXT_MSG_MAX_LENGTH))
            } else {
                input
            }
        }
        textMessageInputted = newMessage
    }

    fun sendTextMessage() {
        viewModelScope.launch {
            val text = textMessageInputted.text
            if (text.isEmpty()) {
                return@launch
            }
            textMessageInputted = TextFieldValue(text = "")
            val messageChannel = messageProvider.sendText(chat = chat, text = text)
            handleMessageChannel(messageChannel = messageChannel)
        }
    }

    private suspend fun handleMessageChannel(messageChannel: Channel<Message>) {
        lateinit var sendingMessage: Message
        for (message in messageChannel) {
            when(val state = message.detail.state) {
                is MessageState.Sending -> {
                    sendingMessage = message
                    attachNewMessage(newMessage = message)
                }

                is MessageState.Completed -> {
                    resetMessageState(
                        msgId = sendingMessage.detail.msgId, messageState = state
                    )
                }

                is MessageState.SendFailed -> {
                    resetMessageState(
                        msgId = sendingMessage.detail.msgId, messageState = state
                    )
                    val failReason = state.reason
                    if (failReason.isNotBlank()) {
                        ToastUtils.show(failReason)
                    }
                }
            }
        }
    }

    private fun attachNewMessage(newMessage: Message) {
        val firstMessage = allMessage.getOrNull(0)
        if (firstMessage == null || newMessage.detail.timestamp - firstMessage.detail.timestamp > 60) {
            allMessage.add(0, TimeMessage(targetMessage = newMessage))
        }
        allMessage.add(0, newMessage)
        chatPageViewState.messageList.value = allMessage.toList()
        viewModelScope.launch {
            delay(timeMillis = 80)
            chatPageViewState.listState.scrollToItem(index = 0)
        }
    }

    private fun resetMessageState(msgId: String, messageState: MessageState) {
        val index = allMessage.indexOfFirst { it.detail.msgId == msgId }
        if (index >= 0) {
            val targetMessage = allMessage[index]
            val messageDetail = targetMessage.detail
            val newMessage = when (targetMessage) {
                is ImageMessage -> {
                    targetMessage.copy(messageDetail = messageDetail.copy(state = messageState))
                }

                is TextMessage -> {
                    targetMessage.copy(messageDetail = messageDetail.copy(state = messageState))
                }

                is SystemMessage, is TimeMessage -> {
                    throw IllegalArgumentException()
                }
            }
            allMessage[index] = newMessage
            chatPageViewState.messageList.value = allMessage.toList()
        }
    }

    fun appendEmoji(emoji: String) {
        if (textMessageInputted.text.length + emoji.length > TEXT_MSG_MAX_LENGTH) {
            return
        }

        val currentText = textMessageInputted.text
        val currentSelection = textMessageInputted.selection.end
        val currentSelectedText = currentText.substring(
            0, currentSelection
        )
        val messageAppend = currentSelectedText + emoji
        val selectedAppend = messageAppend.length
        textMessageInputted = TextFieldValue(
            text = messageAppend + currentText.substring(currentSelection, currentText.length),
            selection = TextRange(index = selectedAppend)
        )
    }
}