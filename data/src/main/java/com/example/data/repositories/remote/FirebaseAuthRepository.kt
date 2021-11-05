package com.example.data.repositories.remote

import android.net.Uri
import android.util.Log
import com.example.domain.exceptions.UserNotFoundException
import com.example.domain.models.ChatUser
import com.example.domain.repositories.remote.IFirebaseAuthRepository
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository: IFirebaseAuthRepository {

    private val auth = Firebase.auth

    override suspend fun checkUserCredentials(email: String, password: String): String {
        val task = auth.signInWithEmailAndPassword(email, password).await()
        return task.user?.uid ?: throw UserNotFoundException()
    }

    override suspend fun createNewUser(email: String, password: String): String {
        val task = auth.createUserWithEmailAndPassword(email, password).await()
        return task.user?.uid ?: throw UserNotFoundException()
    }

    override suspend fun linkWithCredentials(verificationId: String, smsCode: String) {
        Log.e("asd", "linkWithCredentials")
        val credential = PhoneAuthProvider.getCredential(verificationId, smsCode)
        if(Firebase.auth.currentUser?.phoneNumber != null) {
            Firebase.auth.currentUser?.updatePhoneNumber(credential)
        } else {
            Firebase.auth.currentUser?.linkWithCredential(credential)?.await()
        }
    }

    override suspend fun updateCurrentUser(name: String, photoUrl: String): ChatUser {
        auth.currentUser!!.updateProfile(
            UserProfileChangeRequest.Builder().apply {
                displayName = name
                photoUri = Uri.parse(photoUrl)
            }.build()
        ).await()

        val user = Firebase.auth.currentUser!!
        return ChatUser(
            id = user.uid,
            email = user.email!!,
            userName = user.displayName.orEmpty(),
            phone = user.phoneNumber,
            avatar = user.photoUrl?.toString() ?: ""
        )
    }

    override suspend fun linkWithCredentials(credentials: Any) {
        Firebase.auth.currentUser!!.linkWithCredential(credentials as PhoneAuthCredential).await()
    }

    override fun logout() {
        auth.signOut()
    }
}