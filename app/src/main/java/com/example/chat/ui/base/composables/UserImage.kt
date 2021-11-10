package com.example.chat.ui.base.composables

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.mappers.toDataModel
import com.example.domain.models.ChatUser
import io.getstream.chat.android.compose.ui.common.avatar.UserAvatar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CustomImageVM(
    user: ChatUser?
) {
    private val _user = MutableStateFlow(user)
    val user: StateFlow<ChatUser?> = _user

    val value: ChatUser? get() = user.value

    fun setImageData(image: Uri) {
        _user.value = user.value?.copy(avatar = image)
    }

    fun setUser(user: ChatUser) {
        _user.value = user
    }
}

@Composable
fun CustomImage(
    customImageVM: CustomImageVM,
    clickable: Boolean
) {

    val user by customImageVM.user.collectAsState()

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
                customImageVM.setImageData(uriContent)
            }
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

    var avatarModifier = Modifier.size(150.dp)

    if(clickable) {
        avatarModifier = avatarModifier.clickable {
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
    }

    when(user?.avatar) {
        is String -> {
            UserAvatar(
                user = user!!.toDataModel(),
                modifier = avatarModifier
            )
        }
        is Uri -> {
            CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                Image(
                    painter = rememberImagePainter(user?.avatar.toString()),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = avatarModifier.clip(CircleShape)
                )
            }
        }
    }
}