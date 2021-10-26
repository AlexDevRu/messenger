package com.example.chat.ui.sign_in

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chat.R
import com.example.chat.databinding.FragmentSignInBinding
import com.example.chat.ui.base.BaseFragment
import com.example.chat.ui.validation.InputValidationError
import com.example.domain.exceptions.UserAlreadyExistsException
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import kotlinx.coroutines.flow.collect
import net.cr0wd.snackalert.SnackAlert
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInFragment: BaseFragment<FragmentSignInBinding>(FragmentSignInBinding::inflate) {

    private val viewModel by viewModel<SignInVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindProgressButton(binding.signButton)
        binding.signButton.attachTextChangeAnimator()

        binding.userNameEditText.doAfterTextChanged {
            if(it != null)
                viewModel.setEvent(SignInContract.Event.OnValidateUserName(it.toString()))
        }

        binding.passwordEditText.doAfterTextChanged {
            if(it != null)
                viewModel.setEvent(SignInContract.Event.OnValidatePassword(it.toString()))
        }

        binding.rememberMeCheckbox.setOnCheckedChangeListener { _, b ->
            viewModel.handleEvent(SignInContract.Event.OnSignInStatusChanged(b))
        }

        binding.signButton.setOnClickListener {
            viewModel.setEvent(SignInContract.Event.OnSignInClicked)
        }

        binding.toggleModeButton.setOnClickListener {
            viewModel.setEvent(SignInContract.Event.OnModeChanged)
        }

        observeState()
        observeEffects()
    }

    private fun getMessageByValidationError(error: InputValidationError) = when(error) {
        is InputValidationError.LessCharactersError ->
            getString(R.string.small_length_validation, getString(R.string.username), error.minCharacters)
        is InputValidationError.NoNumbersError ->
            getString(R.string.password_no_numbers_validation)
        is InputValidationError.SameCaseError ->
            getString(R.string.password_same_case_validation)
    }

    private fun observeState() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {

                binding.userNameInputLayout.isErrorEnabled = it.userNameValidationError != null

                val userNameErrorMessages = it.userNameValidationError?.joinToString("\n") { e ->
                    getMessageByValidationError(e)
                }

                binding.userNameInputLayout.error = userNameErrorMessages

                binding.passwordInputLayout.isErrorEnabled = it.passwordValidationError != null

                val passwordErrorMessages = it.passwordValidationError?.joinToString("\n") { e ->
                    getMessageByValidationError(e)
                }

                binding.passwordInputLayout.error = passwordErrorMessages

                binding.rememberMeCheckbox.isChecked = it.saveSignInStatus

                val signButtonText = when(it.mode) {
                    SignInContract.SIGN_MODE.SIGN_IN -> R.string.sign_in
                    SignInContract.SIGN_MODE.SIGN_UP -> R.string.sign_up
                }
                val toggleButtonText = when(it.mode) {
                    SignInContract.SIGN_MODE.SIGN_IN -> R.string.sign_in_button_text
                    SignInContract.SIGN_MODE.SIGN_UP -> R.string.sign_up_button_text
                }

                binding.signButton.setText(signButtonText)
                binding.toggleModeButton.setText(toggleButtonText)


                if(it.loading) {
                    binding.signButton.showProgress {
                        buttonTextRes = R.string.please_wait
                        progressColor = Color.BLACK
                    }
                } else {
                    binding.signButton.hideProgress(signButtonText)
                }

                binding.signButton.isEnabled = it.userNameValidationError == null
                        && !it.userName.isNullOrEmpty()
                        && it.passwordValidationError == null && !it.password.isNullOrEmpty()
                        && !it.loading

                binding.userNameInputLayout.isEnabled = !it.loading
                binding.passwordInputLayout.isEnabled = !it.loading
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
                        when(it.throwable) {
                            is UserAlreadyExistsException -> SnackAlert.error(binding.root, R.string.user_already_exists_exception)
                            else -> SnackAlert.error(binding.root, it.throwable?.message.orEmpty())
                        }
                    }
                }
            }
        }
    }

}