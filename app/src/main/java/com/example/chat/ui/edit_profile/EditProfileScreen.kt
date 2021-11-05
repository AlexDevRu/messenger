package com.example.chat.ui.edit_profile

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.chat.R
import com.example.chat.ui.base.composables.ProgressButton
import com.example.chat.ui.base.composables.TextInputField
import com.example.chat.ui.base.composables.Toolbar
import com.example.chat.ui.models.Screen
import com.example.data.mappers.toDataModel
import com.example.domain.models.ChatUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun EditProfileScreen(
    onCancel: () -> Unit = {},
    onSuccess: (ChatUser) -> Unit,
    hasToolbar: Boolean = true,
    //onImageUpload: (Uri) -> Unit,
    //applyChangesInProgress: Boolean,
    viewModel: EditProfileVM = getViewModel()
) {

    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .componentRegistry {
            add (SvgDecoder( LocalContext.current) )
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder(LocalContext.current))
            } else {
                add(GifDecoder())
            }
        }
        .build()

    val cropImage = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            if(uriContent != null) {
                viewModel.setEvent(EditProfileContract.Event.OnImageUpload(uriContent))
            }
        } else {
            val exception = result.error
        }
    }

    val pickImage = {
        cropImage.launch(
            CropImageContractOptions(null, CropImageOptions())
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(100,100)
                .setFixAspectRatio(true)
        )
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if(it.values.all { it }) {
            pickImage()
        }
    }

    val state by viewModel.uiState.collectAsState()
    val effect by viewModel.effect.collectAsState(null)

    val scaffoldState = rememberScaffoldState()
    val snackbarCoroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if(hasToolbar) Toolbar(Screen.EditProfile.displayText, onCancel)
        }
    ) {
        LaunchedEffect(key1 = effect, block = {
            when(effect) {
                is EditProfileContract.Effect.UserUpdatedSuccessfully -> {
                    onSuccess((effect as EditProfileContract.Effect.UserUpdatedSuccessfully).user)
                    onCancel()
                }
                is EditProfileContract.Effect.UserUpdateFailure -> {
                    snackbarCoroutineScope.launch {
                        scaffoldState.snackbarHostState
                            .showSnackbar(
                                (effect as EditProfileContract.Effect.UserUpdateFailure)
                                    .message.orEmpty()
                            )
                    }
                }
            }
        })

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            val avatarModifier = Modifier
                .size(150.dp)
                .clickable {
                    if (state.applyChangedInProgress) return@clickable
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        permissionsLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                            )
                        )
                    } else {
                        pickImage()
                    }
                }

            /*when(state.avatar) {
                is String -> {
                    UserAvatar(
                        user = Firebase.auth.currentUser!!.toDataModel(),
                        modifier = avatarModifier
                    )
                }
                is Uri -> {
                    CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                        Image(
                            painter = rememberImagePainter(state.avatar.toString()),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = avatarModifier.clip(CircleShape)
                        )
                    }
                }
            }*/

            CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                Image(
                    painter = rememberImagePainter(state.avatar.toString(), builder = {
                        placeholder(R.drawable.loading_buffering)
                        error(R.drawable.user_placeholder)
                    }),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = avatarModifier.clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextInputField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.first_name,
                maxCount = EditProfileVM.maxCharacters,
                enabled = !state.applyChangedInProgress,
                value = state.userName,
                errors = state.userNameValidationError,
                onValueChanged = {
                    viewModel.setEvent(EditProfileContract.Event.OnFirstNameChanged(it))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProgressButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.setEvent(EditProfileContract.Event.OnApplyChanges)
                },
                loading = state.applyChangedInProgress,
                enabled = state.userNameValidationError.isNullOrEmpty() &&
                        state.userName.isNotEmpty(),
                textRes = R.string.apply_changes
            )
        }
    }
}