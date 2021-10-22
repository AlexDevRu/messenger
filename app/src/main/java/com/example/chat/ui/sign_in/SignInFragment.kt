package com.example.chat.ui.sign_in

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chat.R
import com.example.chat.databinding.FragmentSignInBinding
import com.example.chat.ui.base.BaseFragment
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.livedata.ChatDomain
import kotlinx.coroutines.flow.collect
import net.cr0wd.snackalert.SnackAlert

class SignInFragment: BaseFragment<FragmentSignInBinding>(FragmentSignInBinding::inflate) {

    private val viewModel: SignInVM by lazy {
        ViewModelProvider(requireActivity()).get(SignInVM::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindProgressButton(binding.signInButton)
        binding.signInButton.attachTextChangeAnimator()

        binding.userNameEditText.doAfterTextChanged {
            if(it != null)
                viewModel.setEvent(SignInContract.Event.OnValidateUserName(it.toString()))
        }

        binding.firstNameEditText.doAfterTextChanged {
            if(it != null)
                viewModel.setEvent(SignInContract.Event.OnValidateFirstName(it.toString()))
        }

        binding.signInButton.setOnClickListener {
            viewModel.setEvent(SignInContract.Event.OnSignInClicked)
        }

        observeState()

        //ChatClient.instance().channel("")

        //ChatDomain.instance().cha

        observeEffects()
    }

    private fun observeState() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {

                Log.w("asd", "username ${it.userName.toString()}")
                Log.w("asd", "username error ${it.userNameValidationError.toString()}")

                binding.userNameInputLayout.isErrorEnabled = it.userNameValidationError != null

                binding.userNameInputLayout.error = when(it.userNameValidationError) {
                    is SignInContract.InputValidationError.LessCharactersException ->
                        getString(R.string.small_length_validation, getString(R.string.username))
                    else -> null
                }

                binding.firstNameInputLayout.isErrorEnabled = it.firstNameValidationError != null

                binding.firstNameInputLayout.error = when(it.firstNameValidationError) {
                    is SignInContract.InputValidationError.LessCharactersException ->
                        getString(R.string.small_length_validation, getString(R.string.first_name))
                    else -> null
                }

                if(it.loading) {
                    binding.signInButton.showProgress {
                        buttonTextRes = R.string.please_wait
                        progressColor = Color.BLACK
                    }
                } else {
                    binding.signInButton.hideProgress(R.string.sign_in)
                }

                binding.signInButton.isEnabled = it.userNameValidationError == null
                        && !it.userName.isNullOrEmpty() && !it.loading

                binding.userNameInputLayout.isEnabled = !it.loading
                binding.firstNameInputLayout.isEnabled = !it.loading
            }
        }
    }

    private fun observeEffects() {
        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                when (it) {
                    SignInContract.Effect.SignInSuccess -> {
                        val action = SignInFragmentDirections.actionSignInFragmentToMainFragment()
                        findNavController().navigate(action)
                    }
                    is SignInContract.Effect.SignInFailure -> {
                        Log.e("asd", it.message.toString())
                        SnackAlert.error(binding.root, it.message)
                    }
                }
            }
        }
    }

}