package com.example.data.repositories.remote

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.domain.repositories.remote.IFirebaseStorageRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class FirebaseStorageRepository(private val app: Application): IFirebaseStorageRepository {

    companion object {
        private const val TAG = "FirebaseStorageRepo"
        private const val USERS_PHOTOS_FOLDER = "user_avatars"
        private const val IMAGE_QUALITY = 80
    }

    override suspend fun saveAvatar(avatar: Any): String {
        return withContext(Dispatchers.IO) {
            when(avatar) {
                is Uri -> {
                    val rootRef = FirebaseStorage.getInstance().reference
                    val feedbackRef = rootRef.child(USERS_PHOTOS_FOLDER)

                    val bitmap = BitmapFactory.decodeStream(app.contentResolver.openInputStream(avatar))

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, baos)
                    val data = baos.toByteArray()

                    val result = feedbackRef.child(Firebase.auth.currentUser!!.uid).putBytes(data).await()
                    val uriResult = result.metadata?.reference?.downloadUrl?.await()!!
                    uriResult.toString()
                }
                is String -> avatar
                else -> throw Exception()
            }
        }
    }
}