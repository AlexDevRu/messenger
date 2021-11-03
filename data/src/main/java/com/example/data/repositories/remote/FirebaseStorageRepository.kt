package com.example.data.repositories.remote

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.domain.common.Result
import com.example.domain.repositories.remote.IFirebaseStorageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class FirebaseStorageRepository(private val app: Application): IFirebaseStorageRepository {

    companion object {
        private const val TAG = "FirebaseStorageRepo"
    }

    override suspend fun saveAvatar(userId: String, avatar: Any): String? {
        return when(avatar) {
            is Uri -> {
                val rootRef = FirebaseStorage.getInstance().reference
                val feedbackRef = rootRef.child("user_avatars")

                val bitmap = BitmapFactory.decodeStream(app.contentResolver.openInputStream(avatar))

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                val data = baos.toByteArray()

                val result = feedbackRef.child(userId).putBytes(data).await()
                val uriResult = result.metadata?.reference?.downloadUrl?.await()!!
                uriResult.toString()
            }
            else -> null
        }
    }
}