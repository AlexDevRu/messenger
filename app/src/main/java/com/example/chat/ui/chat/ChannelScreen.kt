package com.example.chat.ui.chat

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.state.imagepreview.ImagePreviewResultType
import io.getstream.chat.android.compose.state.messages.Thread
import io.getstream.chat.android.compose.state.messages.list.Delete
import io.getstream.chat.android.compose.state.messages.list.Reply
import io.getstream.chat.android.compose.ui.common.SimpleDialog
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

@Preview
@Composable
private fun Preview() {
    ChannelScreen(cid = "", onBackPressed = {})
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChannelScreen(
    cid: String,
    onBackPressed: () -> Unit,
    viewModel: ChatVM = getViewModel(),
) {

    val listViewModel = viewModel<MessageListViewModel>(
        factory = MessagesViewModelFactory(
            LocalContext.current,
            cid,
            ChatClient.instance(),
            ChatDomain.instance()
        )
    )

    val composerViewModel = viewModel<MessageComposerViewModel>(
        factory = MessagesViewModelFactory(
            LocalContext.current,
            cid,
            ChatClient.instance(),
            ChatDomain.instance()
        )
    )

    val attachmentsPickerViewModel = viewModel<AttachmentsPickerViewModel>(
        factory = MessagesViewModelFactory(
            LocalContext.current,
            cid,
            ChatClient.instance(),
            ChatDomain.instance()
        )
    )

    val user by listViewModel.user.collectAsState()

    val onBack = {
        if(listViewModel.channel.messages.isNullOrEmpty()) {
            viewModel.deleteChannel(listViewModel.channel.cid)
            onBackPressed()
        }
    }

    BackHandler(onBack = onBack)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                MessageListHeader(
                    modifier = Modifier.height(56.dp),
                    channel = listViewModel.channel,
                    currentUser = ChatClient.instance().getCurrentUser()!!,
                    onBackPressed = onBack
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
                title = "Delete message",
                message = "Delete message?",
                onPositiveAction = { listViewModel.deleteMessage(deleteAction.message) },
                onDismiss = { listViewModel.dismissMessageAction(deleteAction) }
            )
        }
    }
}

@Composable
public fun BackHandler(enabled: Boolean = true, onBack: () -> Unit) {
    // Safely update the current `onBack` lambda when a new one is provided
    val currentOnBack by rememberUpdatedState(onBack)
    // Remember in Composition a back callback that calls the `onBack` lambda
    val backCallback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                currentOnBack()
            }
        }
    }
    // On every successful composition, update the callback with the `enabled` value
    SideEffect {
        backCallback.isEnabled = enabled
    }
    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, backDispatcher) {
        // Add callback to the backDispatcher
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        // When the effect leaves the Composition, remove the callback
        onDispose {
            backCallback.remove()
        }
    }
}