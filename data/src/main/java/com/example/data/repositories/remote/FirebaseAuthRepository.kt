package com.example.data.repositories.remote

import com.example.domain.common.Result
import com.example.domain.repositories.remote.IFirebaseAuthRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository: IFirebaseAuthRepository {

    private val auth = Firebase.auth

    override suspend fun checkUserCredentials(email: String, password: String): Result<String> {
        return try {
            val task = auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(task.user!!.uid)
        } catch(e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun createNewUser(email: String, password: String): Result<String> {
        return try {
            val task = auth.createUserWithEmailAndPassword(email, password).await()
            Result.Success(task.user!!.uid)
        } catch(e: Exception) {
            Result.Failure(e)
        }
    }
}