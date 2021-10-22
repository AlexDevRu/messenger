package com.example.chat.ui.users

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chat.R
import com.example.chat.databinding.FragmentUsersBinding
import com.example.chat.ui.adapters.UsersAdapter
import com.example.chat.ui.base.BaseFragment
import kotlinx.coroutines.flow.collect
import net.cr0wd.snackalert.SnackAlert

class UsersFragment: BaseFragment<FragmentUsersBinding>(FragmentUsersBinding::inflate) {

    companion object {
        private const val TAG = "UsersFragment"
    }

    private lateinit var usersAdapter: UsersAdapter

    private val viewModel: UsersVM by lazy {
        ViewModelProvider(requireActivity()).get(UsersVM::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        usersAdapter = UsersAdapter {
            viewModel.setEvent(UsersContract.Event.OnUserClick(it.id))
        }
        binding.usersRecyclerView.adapter = usersAdapter

        observeState()
        observeEffect()
    }

    private fun observeEffect() {
        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                when(it) {
                    is UsersContract.Effect.GoToChat -> {
                        val action = UsersFragmentDirections.actionUsersFragmentToChatFragment(it.cid)
                        findNavController().navigate(action)
                    }
                    is UsersContract.Effect.SearchFailure -> {
                        SnackAlert.error(binding.root, it.message)
                    }
                }
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                usersAdapter.submitList(it.users)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.users_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView

        searchView?.maxWidth = Integer.MAX_VALUE
        searchView?.setQuery(viewModel.uiState.value.query, false)
        searchView?.queryHint = getString(R.string.search)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.setEvent(UsersContract.Event.OnQueryChanged(query))
                return true
            }
        })

        searchView?.setOnCloseListener {
            viewModel.setEvent(UsersContract.Event.OnQueryChanged(null))
            false
        }
    }
}