package com.example.data.repositories.remote

import com.example.domain.common.Result
import com.example.domain.repositories.remote.IFirestoreRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreRepository: IFirestoreRepository {

    companion object {
        private const val USERS_COLLECTION = "users"

        private const val USER_ID = "userId"
        private const val PHONE = "phone"
    }

    private val store = Firebase.firestore

    override suspend fun savePhoneByUserId(userId: String, phone: String) {
        store.collection(USERS_COLLECTION)
            .document(userId)
            .set(mapOf(
                USER_ID to userId,
                PHONE to phone
            ))
            .await()
    }

    override suspend fun getPhoneByUserId(userId: String): String {
        val data = store.collection(USERS_COLLECTION)
            .whereEqualTo(USER_ID, userId)
            .get()
            .await()

        val document = data.documents.firstOrNull()

        val phone = document?.get(PHONE) as String

        return phone
    }
}