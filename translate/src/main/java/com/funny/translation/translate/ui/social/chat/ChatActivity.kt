package com.funny.translation.translate.ui.social.chat

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.proxy.models.Chat
import com.example.proxy.models.ImageMessage
import com.example.proxy.models.TextMessage
import com.funny.translation.BaseActivity
import com.funny.translation.theme.TransTheme
import com.funny.translation.translate.R
import com.funny.translation.translate.ui.social.friendship.FriendshipViewModel
import com.hjq.toast.ToastUtils

class ChatActivity: BaseActivity() {
    companion object {
        private const val keyChat = "keyChat"

        fun navTo(context: Context, chat: Chat) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(keyChat, chat)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    @Suppress("DEPRECATION")
    private val chat: Chat by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getParcelableExtra(keyChat)!!
    }

    private val chatViewModel by viewModelsInstance {
        ChatViewModel(chat = chat)
    }

    private val chatPageAction = ChatPageAction(
        onClickAvatar = {
            val messageSenderId = it.detail.sender.id
            if (messageSenderId.isNotBlank()) {
//                FriendProfileActivity.navTo(context = this, friendId = messageSenderId)
            }
        },
        onClickMessage = {
            when (it) {
                is ImageMessage -> {
                    val allImageUrls = chatViewModel.filterAllImageMessageUrl()
                    val initialImageUrl = it.previewImageUrl
                    if (allImageUrls.isNotEmpty() && initialImageUrl.isNotBlank()) {
                        val initialPage = allImageUrls.indexOf(element = initialImageUrl)
                            .coerceAtLeast(minimumValue = 0)

                    }
                }
                else -> {
                }
            }
        },
        onLongClickMessage = {
            when (it) {
                is TextMessage -> {
                    val msg = it.formatMessage
                    if (msg.isNotEmpty()) {
                        copyText(context = this, text = msg)
                        ToastUtils.show("已复制")
                    }
                }
                else -> {

                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TransTheme {
                ChatPage(
                    chatViewModel = chatViewModel,
                    chatPageAction = chatPageAction
                )
            }
        }
    }

    private fun copyText(context: Context, text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        if (clipboardManager != null) {
            val clipData = ClipData.newPlainText(context.getString(R.string.app_name), text)
            clipboardManager.setPrimaryClip(clipData)
        }
    }

}