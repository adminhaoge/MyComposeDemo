package com.example.proxy.models

import androidx.compose.runtime.Stable

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
data class Conversation(
    val id: String,
    val name: String,
    val faceUrl: String,
    val unreadMessageCount: Int,
    val lastMessage: Message,
    val isPinned: Boolean,
    val type: ConversationType
) {

    val formatMsg by lazy(mode = LazyThreadSafetyMode.NONE) {
        val messageDetail = lastMessage.detail
        val prefix =
            if (type == ConversationType.Group && lastMessage !is SystemMessage && !messageDetail.isOwnMessage) {
                messageDetail.sender.showName + "："
            } else {
                ""
            }
        prefix + when (messageDetail.state) {
            MessageState.Completed -> {
                lastMessage.formatMessage
            }

            MessageState.Sending -> {
                "[发送中] " + lastMessage.formatMessage
            }

            is MessageState.SendFailed -> {
                "[发送失败] " + lastMessage.formatMessage
            }
        }
    }

}

@Stable
enum class ConversationType {
    C2C,
    Group
}