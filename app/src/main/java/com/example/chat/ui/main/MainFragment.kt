package com.example.chat.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.example.chat.R
import com.example.chat.databinding.FragmentMainBinding
import com.example.chat.ui.base.BaseFragment
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.livedata.ChatDomain

import kotlinx.coroutines.flow.collect
import net.cr0wd.snackalert.SnackAlert
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MainFragment: BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    companion object {
        private const val TAG = "ChannelFragment"
    }

    private val viewModel by sharedViewModel<MainVM>()

    private val args by navArgs<MainFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.channelsView.setChannelDeleteClickListener { channel ->
            deleteChannel(channel)
        }

        binding.channelListHeaderView.setOnUserAvatarClickListener {
            binding.drawerLayout.openDrawer(Gravity.START)
        }

        binding.channelListHeaderView.setOnActionButtonClickListener {
            val action = MainFragmentDirections.actionMainFragmentToUsersFragment()
            findNavController().navigate(action)
        }

        binding.channelsView.setChannelItemClickListener { channel ->
            val action = MainFragmentDirections.actionMainFragmentToChatFragment(channel.cid)
            findNavController().navigate(action)
        }*/

        observeState()
        observeEffects()

        if(savedInstanceState == null) {
            Log.w("asd", "savedInstanceState == null")
            viewModel.handleEvent(MainContract.Event.OnUserLoad(args.userId))
        }
    }

    private fun observeState() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                if(it.user != null) {
                    setupChannels(it.user.id)
                    setupDrawer(it.user)
                }

                /*if(it.loading) binding.channelsView.showLoadingView()
                else binding.channelsView.hideLoadingView()*/
            }
        }
    }

    private fun observeEffects() {
        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                when(it) {
                    MainContract.Effect.Logout -> {
                        val action = MainFragmentDirections.actionMainFragmentToSignInFragment()
                        findNavController().navigate(action)
                        SnackAlert.success(binding.root, R.string.logout_successfully)
                    }
                    is MainContract.Effect.ShowErrorSnackbar -> {
                        Log.e("asd", it.message.orEmpty())
                        SnackAlert.error(binding.root, it.message.orEmpty())
                    }
                }
            }
        }
    }

    private fun setupChannels(userId: String) {
        val filters = Filters.and(
            Filters.eq("type", "messaging"),
            Filters.`in`("members", listOf(userId))
        )
        /*val viewModelFactory = ChannelListViewModelFactory(
            filters,
            ChannelListViewModel.DEFAULT_SORT
        )
        val listViewModel: ChannelListViewModel by viewModels { viewModelFactory }
        val listHeaderViewModel: ChannelListHeaderViewModel by viewModels()

        listHeaderViewModel.bindView(binding.channelListHeaderView, viewLifecycleOwner)
        listViewModel.bindView(binding.channelsView, viewLifecycleOwner)*/
    }

    private fun deleteChannel(channel: Channel) {
        ChatDomain.instance().deleteChannel(channel.cid).enqueue { result ->
            if (result.isSuccess) {
                showSnackBar("Channel: ${channel.name} removed!")
            } else {
                Log.e(TAG, result.error().message.toString())
            }
        }
    }

    private fun setupDrawer(user: User) {
        Log.w("asd", "hdfkjdhsfjh $user")

        binding.navigationView.setupWithNavController(findNavController())
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.logout_menu) {
                logout()
            }
            false
        }

        /*val headerView = binding.navigationView.getHeaderView(0)
        val headerAvatar = headerView.findViewById<AvatarView>(R.id.avatarView)

        val headerId = headerView.findViewById<TextView>(R.id.id_textView)

        val headerName = headerView.findViewById<TextView>(R.id.name_textView)

        val headerDrawerBinding = DrawerHeaderBinding.bind(headerView)
        headerDrawerBinding.editProfileButton.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToEditProfileFragment()
            findNavController().navigate(action)
        }

        headerId.text = user.id
        headerName.text = user.name
        headerAvatar.setUserData(user)*/
    }

    private fun logout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            viewModel.setEvent(MainContract.Event.OnLogout)
        }
        builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
        builder.setTitle(getString(R.string.logout_question))
        builder.setMessage(getString(R.string.logout_confirm))
        builder.create().show()
    }
}