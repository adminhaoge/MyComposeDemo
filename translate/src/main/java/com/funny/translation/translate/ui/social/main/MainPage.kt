package com.funny.translation.translate.ui.social.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funny.translation.translate.ui.social.MainPageBottomBar
import com.funny.translation.translate.ui.social.MainPageTab
import com.funny.translation.translate.ui.social.MainPageTopBar
import com.funny.translation.translate.ui.social.conversation.ConversationPage
import com.funny.translation.translate.ui.social.conversation.ConversationViewModel
import com.funny.translation.translate.ui.social.friendship.FriendshipDialog
import com.funny.translation.translate.ui.social.friendship.FriendshipPage
import com.funny.translation.translate.ui.social.friendship.FriendshipViewModel

@Preview
@Composable
fun MainPage() {
    val mainViewModel: MainViewModel = viewModel()
    val conversationViewModel: ConversationViewModel = viewModel()
    val friendshipViewModel: FriendshipViewModel = viewModel()
//    val personProfileViewModel by viewModels<PersonProfileViewModel>()

    ModalNavigationDrawer(
        modifier = Modifier.fillMaxSize(),
        drawerState = mainViewModel.drawerViewState.drawerState,
        drawerContent = {
        },
        content = {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets(
                    left = 0.dp,
                    top = 0.dp,
                    right = 0.dp,
                    bottom = 0.dp
                ),
                topBar = {
                    if (mainViewModel.bottomBarViewState.selectedTab.value != MainPageTab.Person) {
                        MainPageTopBar(
                            viewState = mainViewModel.topBarViewState,
                            showFriendshipDialog = {
                                friendshipViewModel.showFriendshipDialog()
                            }
                        )
                    }
                },
                bottomBar = {
                    MainPageBottomBar(viewState = mainViewModel.bottomBarViewState)
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
                ) {
                    when (mainViewModel.bottomBarViewState.selectedTab.value) {
                        MainPageTab.Conversation -> {
                            ConversationPage(pageViewState = conversationViewModel.pageViewState)
                        }

                        MainPageTab.Friendship -> {
                            FriendshipPage(pageViewState = friendshipViewModel.pageViewState)
                        }

                        MainPageTab.Person -> {
//                            PersonProfilePage(pageViewState = personProfileViewModel.pageViewState)
                        }
                    }
                }
            }
            FriendshipDialog(viewState = friendshipViewModel.friendshipDialogViewState)
        }
    )
}