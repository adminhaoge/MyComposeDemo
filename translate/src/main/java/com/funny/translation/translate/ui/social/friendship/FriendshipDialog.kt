package com.funny.translation.translate.ui.social.friendship

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.funny.translation.translate.ui.social.widgets.CommonButton
import com.funny.translation.translate.ui.social.widgets.CommonOutlinedTextField
import com.funny.translation.translate.ui.social.widgets.ComposeBottomSheetDialog
import com.hjq.toast.ToastUtils

@Composable
fun FriendshipDialog(viewState: FriendshipDialogViewState) {
    ComposeBottomSheetDialog(
        visible = viewState.visible.value,
        onDismissRequest = viewState.dismissDialog
    ) {
        var userId by remember {
            mutableStateOf(value = "")
        }
        Column(
            modifier = Modifier
                .fillMaxHeight(fraction = 0.90f)
                .clip(shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(color = MaterialTheme.colorScheme.background)
                .padding(top = 20.dp)
        ) {
            CommonOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                value = userId,
                onValueChange = { id ->
                    val realValue = id.trimStart().trimEnd()
                    if (realValue.all { it.isLowerCase() || it.isUpperCase() }) {
                        userId = realValue
                    }
                },
                label = "输入 UserID",
                singleLine = true,
                maxLines = 1
            )
            CommonButton(text = "添加好友") {
                if (userId.isBlank()) {
                    ToastUtils.show("请输入 UserID")
                } else {
                    viewState.addFriend(userId)
                }
            }

        }
    }
}