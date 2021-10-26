package com.example.chat.ui.edit_profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chat.R
import com.example.chat.databinding.FragmentEditProfileBinding
import com.example.chat.ui.base.BaseFragment
import com.example.chat.ui.main.MainContract
import com.example.chat.ui.main.MainVM
import com.example.chat.ui.validation.InputValidationError
import com.example.chat.utils.load
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import kotlinx.coroutines.flow.collect
import net.cr0wd.snackalert.SnackAlert
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditProfileFragment: BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::inflate) {

    private val viewModel by viewModel<EditProfileVM>()

    private val mainViewModel by sharedViewModel<MainVM>()

    private val pickImageActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result?.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            if(data != null) {
                Log.w("asd", "avatar uri ${data.path}")

                val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(data))

                viewModel.setEvent(EditProfileContract.Event.OnImageUpload(bitmap))
            }
        }
    }


    private val readExternalStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        if(it) {
            pickImage()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindProgressButton(binding.applyChangesButton)
        binding.applyChangesButton.attachTextChangeAnimator()

        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.applyChangesButton.setOnClickListener {
            viewModel.handleEvent(EditProfileContract.Event.OnApplyChanges)
        }

        binding.firstNameEditText.doAfterTextChanged {
            viewModel.setEvent(EditProfileContract.Event.OnFirstNameChanged(it.toString()))
        }

        binding.avatarView.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                readExternalStoragePermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } else {
                pickImage()
            }
        }

        binding.firstNameEditText.setText(viewModel.uiState.value.firstName)

        observeState()
        observeEffects()
    }

    private fun observeEffects() {
        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                when(it) {
                    is EditProfileContract.Effect.UserUpdatedSuccessfully -> {
                        mainViewModel.setEvent(MainContract.Event.OnUserUpdated(it.user))
                        SnackAlert.success(binding.root, R.string.user_updated_successfully)
                        findNavController().navigateUp()
                    }
                    is EditProfileContract.Effect.UserUpdateFailure -> {
                        SnackAlert.error(binding.root, it.message.orEmpty())
                    }
                }
            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("image/*")
        pickImageActivityResultLauncher.launch(intent)
    }

    private fun getMessageByValidationError(error: InputValidationError) = when(error) {
        is InputValidationError.LessCharactersError ->
            getString(R.string.small_length_validation, getString(R.string.first_name), error.minCharacters)
        is InputValidationError.NoNumbersError ->
            getString(R.string.password_no_numbers_validation)
        is InputValidationError.SameCaseError ->
            getString(R.string.password_same_case_validation)
    }

    private fun observeState() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                binding.firstNameInputLayout.isErrorEnabled = it.firstNameValidationError != null

                val firstNameErrorMessages = it.firstNameValidationError?.joinToString("\n") { e ->
                    getMessageByValidationError(e)
                }

                binding.firstNameInputLayout.error = firstNameErrorMessages

                when(it.avatar) {
                    is String -> {
                        binding.avatarView.load(
                            if(it.avatar.isEmpty())
                                "https://getstream.imgix.net/images/random_svg/${it.firstName.firstOrNull()?.titlecase()}.svg"
                            else it.avatar
                        )
                    }
                    is Bitmap -> {
                        binding.avatarView.setImageBitmap(it.avatar)
                    }
                }

                if(it.applyChangedInProgress) {
                    binding.applyChangesButton.showProgress {
                        buttonTextRes = R.string.please_wait
                        progressColor = Color.BLACK
                    }
                } else {
                    binding.applyChangesButton.hideProgress(R.string.apply_changes)
                }

                binding.applyChangesButton.isEnabled = it.firstNameValidationError == null && !it.applyChangedInProgress
            }
        }
    }
}