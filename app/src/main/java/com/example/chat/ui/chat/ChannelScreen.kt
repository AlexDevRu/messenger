package com.example.chat.ui.chat

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat.R
import com.example.chat.ui.base.composables.BackHandler
import com.example.data.mappers.toDomainModel
import com.example.domain.models.ChatUser
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.state.imagepreview.ImagePreviewResultType
import io.getstream.chat.android.compose.state.messages.Thread
import io.getstream.chat.android.compose.state.messages.list.Delete
import io.getstream.chat.android.compose.state.messages.list.Reply
import io.getstream.chat.android.compose.ui.common.SimpleDialog
import io.getstream.chat.android.compose.ui.common.avatar.ChannelAvatar
import io.getstream.chat.android.compose.ui.messages.attachments.AttachmentsPicker
import io.getstream.chat.android.compose.ui.messages.composer.MessageComposer
import io.getstream.chat.android.compose.ui.messages.header.MessageListHeader
import io.getstream.chat.android.compose.ui.messages.list.MessageList
import io.getstream.chat.android.compose.ui.messages.overlay.SelectedMessageOverlay
import io.getstream.chat.android.compose.ui.messages.overlay.defaultMessageOptions
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.AttachmentsPickerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.offline.ChatDomain
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChannelScreen(
    cid: String,
    onBackPressed: () -> Unit,
    onChannelAvatarClick: (user: ChatUser) -> Unit,
    viewModel: ChatVM = getViewModel(),
) {
    var granted by remember {
        mutableStateOf(false)
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if(it.values.all { it }) {
            granted = true
        } else {
            onBackPressed()
        }
    }

    LaunchedEffect(key1 = Unit) {
        permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    Log.e("asd", "currentUser ${ChatClient.instance().getCurrentUser()}")

    if(granted) {
        val factory = MessagesViewModelFactory(
            LocalContext.current,
            cid,
            ChatClient.instance(),
            ChatDomain.instance()
        )

        val listViewModel = viewModel<MessageListViewModel>(factory = factory)

        val composerViewModel = viewModel<MessageComposerViewModel>(factory = factory)

        val attachmentsPickerViewModel = viewModel<AttachmentsPickerViewModel>(factory = factory)

        val user by listViewModel.user.collectAsState()

        val currentUser = ChatClient.instance().getCurrentUser()

        val onBack = {
            if(listViewModel.channel.messages.isNullOrEmpty()) {
                viewModel.deleteChannel(listViewModel.channel.cid)
            }
            onBackPressed()
        }

        BackHandler(onBack = onBack)


        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    MessageListHeader(
                        modifier = Modifier.height(56.dp),
                        channel = listViewModel.channel,
                        currentUser = currentUser,
                        onBackPressed = onBack,
                        trailingContent = {
                            ChannelAvatar(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable {
                                        val clickedUser =
                                            listViewModel.channel.members.first { it.user.id != currentUser?.id }
                                        onChannelAvatarClick(clickedUser.user.toDomainModel())
                                    },
                                channel = listViewModel.channel,
                                currentUser = currentUser,
                                contentDescription = listViewModel.channel.name,
                            )
                        }
                    )
                },
                bottomBar = {
                    MessageComposer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .align(Alignment.Center),
                        viewModel = composerViewModel,
                        onAttachmentsClick = { attachmentsPickerViewModel.changeAttachmentState(true) },
                        onCancelAction = {
                            listViewModel.dismissAllMessageActions()
                            composerViewModel.dismissMessageActions()
                        }
                    )
                }
            ) {
                MessageList(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ChatTheme.colors.appBackground)
                        .padding(it),
                    viewModel = listViewModel,
                    onThreadClick = { message ->
                        composerViewModel.setMessageMode(Thread(message))
                        listViewModel.openMessageThread(message)
                    },
                    onImagePreviewResult = { result ->
                        when (result?.resultType) {
                            ImagePreviewResultType.QUOTE -> {
                                val message = listViewModel.getMessageWithId(result.messageId)

                                if (message != null) {
                                    composerViewModel.performMessageAction(Reply(message))
                                }
                            }

                            ImagePreviewResultType.SHOW_IN_CHAT -> {
                                listViewModel.focusMessage(result.messageId)
                            }
                            null -> Unit
                        }
                    }
                )
            }

            val selectedMessage = listViewModel.currentMessagesState.selectedMessage

            if (selectedMessage != null) {
                SelectedMessageOverlay(
                    messageOptions = defaultMessageOptions(selectedMessage, user, listViewModel.isInThread),
                    message = selectedMessage,
                    onMessageAction = { action ->
                        composerViewModel.performMessageAction(action)
                        listViewModel.performMessageAction(action)
                    },
                    onDismiss = { listViewModel.removeOverlay() }
                )
            }

            AnimatedVisibility(
                visible = attachmentsPickerViewModel.isShowingAttachments,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(delayMillis = AnimationConstants.DefaultDurationMillis / 2))
            ) {
                AttachmentsPicker(
                    attachmentsPickerViewModel = attachmentsPickerViewModel,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(350.dp)
                        .animateEnterExit(
                            enter = slideInVertically(
                                initialOffsetY = { height -> height },
                                animationSpec = tween()
                            ),
                            exit = slideOutVertically(
                                targetOffsetY = { height -> height },
                                animationSpec = tween()
                            )
                        ),
                    onAttachmentsSelected = { attachments ->
                        attachmentsPickerViewModel.changeAttachmentState(false)
                        composerViewModel.addSelectedAttachments(attachments)
                    },
                    onDismiss = {
                        attachmentsPickerViewModel.changeAttachmentState(false)
                        attachmentsPickerViewModel.dismissAttachments()
                    }
                )
            }

            val deleteAction = listViewModel.messageActions.firstOrNull { it is Delete }

            if (deleteAction != null) {
                SimpleDialog(
                    modifier = Modifier.padding(16.dp),
                    title = stringResource(R.string.delete_message),
                    message = "${stringResource(R.string.delete_message)}?",
                    onPositiveAction = { listViewModel.deleteMessage(deleteAction.message) },
                    onDismiss = { listViewModel.dismissMessageAction(deleteAction) }
                )
            }
        }
    }
}
