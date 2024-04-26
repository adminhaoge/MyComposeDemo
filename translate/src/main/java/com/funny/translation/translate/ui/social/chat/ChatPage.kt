@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)

package com.funny.translation.translate.ui.social.chat

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proxy.models.Chat
import com.funny.translation.translate.ui.widget.EmojiTable

private val DEFAULT_KEYBOARD_HEIGHT = 305.dp

@Composable
fun ChatPage(chatViewModel: ChatViewModel, chatPageAction: ChatPageAction) {
    val chatPageViewState = chatViewModel.chatPageViewState

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatPageTopBar(
                title = chatPageViewState.topBarTitle.value,
                chat = chatPageViewState.chat
            )
        },
        bottomBar = {
            ChatPageBottomBar(chatViewModel = chatViewModel)
        }
    ) { innerPadding ->
        val refreshing by chatViewModel.loadMessageViewState.refreshing
        val loadFinish by chatViewModel.loadMessageViewState.loadFinish
        val pullRefreshState = rememberPullRefreshState(
            refreshing = refreshing,
            onRefresh = {
                chatViewModel.loadMoreMessage()
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
                .pullRefresh(
                    state = pullRefreshState,
                    enabled = !loadFinish
                )
        ) {
            MessagePanel(
                pageViewState = chatPageViewState,
                pageAction = chatPageAction
            )
            PullRefreshIndicator(
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter),
                refreshing = refreshing,
                state = pullRefreshState,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun ChatPageTopBar(title: String, chat: Chat) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        modifier = Modifier
            .shadow(elevation = 0.8.dp),
        title = {
            Text(
                modifier = Modifier,
                text = title,
                fontSize = 19.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                content = {
                    Icon(
                        modifier = Modifier
                            .size(size = 22.dp),
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null
                    )
                },
                onClick = {
                    (context as Activity).finish()
                }
            )
        },
        actions = {
            IconButton(
                content = {
                    Icon(
                        modifier = Modifier
                            .size(size = 24.dp),
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null
                    )
                },
                onClick = {
                    when (chat) {
                        is Chat.PrivateChat -> {
//                            FriendProfileActivity.navTo(context = context, friendId = chat.id)
                        }

                        is Chat.GroupChat -> {
//                            GroupProfileActivity.navTo(context = context, groupId = chat.id)
                        }
                    }
                }
            )
        }
    )
}

@Composable
fun ChatPageBottomBar(chatViewModel: ChatViewModel) {

    val textMessageInputted = chatViewModel.textMessageInputted
    val currentInputSelector = chatViewModel.currentInputSelector

    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    val focusRequester = remember {
        FocusRequester()
    }
    val focusManager = LocalFocusManager.current

    BackHandler(enabled = currentInputSelector != InputSelector.NONE) {
        focusRequester.requestFocus()
    }


//TODO 选择好的开源框架
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = MatisseContract()
//    ) { result ->
//        if (!result.isNullOrEmpty()) {
//            chatViewModel.sendImageMessage(imageUri = result[0].uri)
//        }
//    }
//
//    val takePictureLauncher = rememberLauncherForActivityResult(
//        contract = MatisseCaptureContract()
//    ) { result ->
//        if (result != null) {
//            chatViewModel.sendImageMessage(imageUri = result.uri)
//        }
//    }

    var keyboardHeightDp by remember {
        mutableStateOf(value = 0.dp)
    }

    val ime = WindowInsets.ime
    val localDensity = LocalDensity.current
    val density = localDensity.density
    LaunchedEffect(key1 = density) {
        snapshotFlow {
            ime.getBottom(density = localDensity)
        }.collect {
            val realtimeKeyboardHeightDp = (it / density).dp
            keyboardHeightDp = maxOf(
                realtimeKeyboardHeightDp, keyboardHeightDp
            )
            if (realtimeKeyboardHeightDp == keyboardHeightDp) {
                chatViewModel.onInputSelectorChanged(newSelector = InputSelector.NONE)
                softwareKeyboardController?.show()
            }
        }
    }

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.onSecondary)
            .navigationBarsPadding(),
    ) {
        BasicTextField(
            modifier = Modifier
                .focusRequester(focusRequester = focusRequester)
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 12.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(size = 10.dp)
                )
                .padding(horizontal = 8.dp, vertical = 12.dp),
            value = textMessageInputted,
            onValueChange = {
                chatViewModel.onUserInputChanged(input = it)
            },
            maxLines = 6,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(value = MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    focusRequester.requestFocus()
                    chatViewModel.sendTextMessage()
                }
            )
        )
        InputSelector(
            currentInputSelector = currentInputSelector,
            sendMessageEnabled = textMessageInputted.text.isNotEmpty(),
            onInputSelectorChange = {
                focusManager.clearFocus(force = true)
                softwareKeyboardController?.hide()
                chatViewModel.onInputSelectorChanged(newSelector = it)
            },
            onClickSend = {
                focusRequester.requestFocus()
                chatViewModel.sendTextMessage()
            }
        )
        when (currentInputSelector) {
            InputSelector.NONE -> {
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .imePadding()
                )
            }

            InputSelector.EMOJI, InputSelector.Picture -> {
                val maxHeight = if (keyboardHeightDp <= 0.dp) {
                    DEFAULT_KEYBOARD_HEIGHT
                } else {
                    keyboardHeightDp
                }
                Box(
                    modifier = Modifier
                        .heightIn(min = keyboardHeightDp, max = maxHeight)
                ) {
                    when (currentInputSelector) {
                        InputSelector.EMOJI -> {
                            EmojiTable(
                                appendEmoji = {
                                    chatViewModel.appendEmoji(emoji = it)
                                }
                            )
                        }
                        InputSelector.Picture -> {
                            Box(
                                modifier = Modifier.heightIn(
                                    min = keyboardHeightDp,
                                    max = maxHeight
                                )
                            ) {
                                ExtendTable(
                                    launchTakePicture = {
                                        chatViewModel.onInputSelectorChanged(newSelector = InputSelector.NONE)
//                                        val matisse = Matisse(
//                                            maxSelectable = 1,
//                                            mediaType = MediaType.ImageOnly,
//                                            imageEngine = MatisseImageEngine(),
//                                            captureStrategy = MediaStoreCaptureStrategy()
//                                        )
//                                        imagePickerLauncher.launch(matisse)
                                    },
                                    launchImagePicker = {
//                                        chatViewModel.onInputSelectorChanged(
//                                            newSelector = InputSelector.NONE
//                                        )
//                                        val matisseCapture = MatisseCapture(
//                                            captureStrategy = MediaStoreCaptureStrategy()
//                                        )
//                                        takePictureLauncher.launch(matisseCapture)
                                    }
                                )
                            }
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }
}