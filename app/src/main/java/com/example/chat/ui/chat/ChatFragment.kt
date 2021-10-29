package com.example.chat.ui.chat

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.chat.databinding.FragmentChatBinding
import com.example.chat.ui.base.BaseFragment
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel


class ChatFragment : BaseFragment<FragmentChatBinding>(FragmentChatBinding::inflate) {

    private val args: ChatFragmentArgs by navArgs()

    private val viewModel: ChatVM by lazy {
        ViewModelProvider(requireActivity()).get(ChatVM::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMessages()

        /*binding.messagesHeaderView.setBackButtonClickListener {
            requireActivity().onBackPressed()
        }*/

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(isEnabled) {
                    deleteChannelIfEmpty()
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        })
    }

    private fun setupMessages() {
        /*val factory = MessageListViewModelFactory(cid = args.channelId)
        val messageListViewModel: MessageListViewModel by viewModels { factory }

        val messageListHeaderViewModel: MessageListHeaderViewModel by viewModels { factory }

        val messageInputViewModel: MessageInputViewModel by viewModels { factory }

        messageListHeaderViewModel.bindView(binding.messagesHeaderView, viewLifecycleOwner)*/

        /*val disposable: Disposable = channelClient.subscribeFor<NewMessageEvent> { newMessageEvent ->
            val message = newMessageEvent.message
        }*/
        // Let both message list header and message input know when we open a thread
        /*messageListViewModel.mode.observe(this) { mode ->
            when (mode) {
                is MessageListViewModel.Mode.Thread -> {
                    messageListHeaderViewModel.setActiveThread(mode.parentMessage)
                    messageInputViewModel.setActiveThread(mode.parentMessage)
                }
                MessageListViewModel.Mode.Normal -> {
                    messageListHeaderViewModel.resetThread()
                    messageInputViewModel.resetThread()
                }
            }
        }*/

        // Let the message input know when we are editing a message
        //binding.messageList.setMessageEditHandler(messageInputViewModel::postMessageToEdit)

        // Handle navigate up state
        /*messageListViewModel.state.observe(this) { state ->
            if (state is MessageListViewModel.State.NavigateUp) {
                // Handle navigate up
            }
        }

        // Handle back button behaviour correctly when you're in a thread
        val backHandler = {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
        }
        binding.messagesHeaderView.setBackButtonClickListener(backHandler)


        messageListViewModel.bindView(binding.messageList, viewLifecycleOwner)
        messageInputViewModel.bindView(binding.messageInputView, viewLifecycleOwner)*/
    }

    private fun deleteChannelIfEmpty() {
        viewModel.deleteChannel(args.channelId)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}