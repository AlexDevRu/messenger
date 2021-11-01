package com.example.chat.ui.edit_profile

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.decode.SvgDecoder
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.chat.R
import com.example.chat.ui.base.composables.ProgressButton
import com.example.chat.ui.base.composables.TextInputField
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun EditProfileScreen(
    onCancel: () -> Unit = {},
    onSuccess: (User) -> Unit,
    viewModel: EditProfileVM = getViewModel()
) {

    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .componentRegistry {
            add (SvgDecoder( LocalContext.current) )
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

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(onClick = {
                    onCancel()
                }) {
                    Icon(
                        painterResource(R.drawable.ic_baseline_arrow_back_24),
                        "contentDescription",
                        tint = Color.Black
                    )
                }
                Text("Edit Profile", fontSize = 18.sp)
            }
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

        Log.e("asd", if(state.avatar.toString().isEmpty()) viewModel.defaultAvatar else state.avatar.toString())

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            val avatarModifier = Modifier.size(150.dp).clickable {
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


            when(state.avatar) {
                is String -> {
                    ChatTheme {
                        UserAvatar(
                            user = ChatClient.instance().getCurrentUser()!!,
                            modifier = avatarModifier
                        )
                    }
                }
                is Uri -> {
                    CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                        Image(
                            painter = rememberImagePainter(
                                if(state.avatar.toString().isEmpty()) viewModel.defaultAvatar else state.avatar.toString()
                            ),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = avatarModifier.clip(CircleShape)
                        )
                    }
                }
            }

            TextInputField(
                modifier = Modifier.fillMaxWidth(),
                label = R.string.first_name,
                enabled = !state.applyChangedInProgress,
                value = state.firstName,
                errors = state.firstNameValidationError,
                onValueChanged = {
                    viewModel.setEvent(EditProfileContract.Event.OnFirstNameChanged(it))
                }
            )

            ProgressButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.setEvent(EditProfileContract.Event.OnApplyChanges)
                },
                loading = state.applyChangedInProgress,
                enabled = state.firstNameValidationError.isNullOrEmpty() && state.firstName.isNotEmpty(),
                textRes = R.string.apply_changes
            )
        }
    }
}