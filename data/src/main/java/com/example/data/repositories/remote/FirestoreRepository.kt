package com.example.data.repositories.remote

import com.example.domain.common.Result
import com.example.domain.exceptions.UserAlreadyExistsException
import com.example.domain.exceptions.WrongCredentialsException
import com.example.domain.repositories.remote.IFirestoreRepository
import com.example.domain.security.md5
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreRepository: IFirestoreRepository {

    companion object {
        private const val USERS_COLLECTION = "users"

        private const val USER_ID = "userId"
        private const val PASSWORD = "password"
    }

    private val store = Firebase.firestore

    override suspend fun checkUserCredentials(userId: String, password: String): Result<Unit> {
        return try {
            val hashedPassword = md5(password)

            val data = store.collection(USERS_COLLECTION)
                .whereEqualTo(USER_ID, userId)
                .get()
                .await()

            val document = data.documents.firstOrNull()

            val remoteUserName = document?.get(USER_ID) as String
            val remotePassword = document.get(PASSWORD) as String

            if(remoteUserName == userId && remotePassword == hashedPassword) {
                Result.Success(Unit)
            } else {
                Result.Failure(WrongCredentialsException())
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun createNewUser(userId: String, password: String): Result<Unit> {
        return try {
            val document = store.collection(USERS_COLLECTION).whereEqualTo(USER_ID, userId).get().await()

            if(document.isEmpty) {
                val hashedPassword = md5(password)

                store.collection(USERS_COLLECTION)
                    .document()
                    .set(mapOf(
                        USER_ID to userId,
                        PASSWORD to hashedPassword
                    ))
                    .await()

                Result.Success(Unit)
            } else {
                throw UserAlreadyExistsException()
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}