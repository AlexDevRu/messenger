package com.example.data.repositories.remote

import android.util.Log
import com.example.domain.repositories.remote.IFirestoreRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreRepository: IFirestoreRepository {

    companion object {
        private const val USERS_COLLECTION = "users"

        private const val USER_ID = "userId"
        private const val PHONE_NUMBER = "phoneNumber"

        private const val MAX_SIZE_WHERE_IN = 10
    }

    private val store = Firebase.firestore

    init {
        store.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    override suspend fun savePhoneByUserId(phoneNumber: String) {
        val uid = Firebase.auth.currentUser!!.uid
        store.collection(USERS_COLLECTION)
            .document(uid)
            .set(mapOf(
                USER_ID to uid,
                PHONE_NUMBER to phoneNumber
            ))
            .await()
    }

    override suspend fun getPhoneByUserId(userId: String): String {
        val data = store.collection(USERS_COLLECTION)
            .whereEqualTo(USER_ID, userId)
            .get()
            .await()

        val document = data.documents.firstOrNull()

        return document?.get(PHONE_NUMBER) as String
    }

    private suspend fun filterByField(fieldName: String, values: List<String>): Map<String, String> {

        val phonesMap = mutableMapOf<String, String>()

        var startIndex = 0

        while(startIndex < values.size) {

            val size = if((values.size - startIndex) >= MAX_SIZE_WHERE_IN) MAX_SIZE_WHERE_IN else values.size - startIndex
            val partValues = values.subList(startIndex, startIndex + size)

            Log.e("asd", "size $size, startIndex $startIndex")

            val data = store.collection(USERS_COLLECTION)
                .whereIn(fieldName, partValues)
                .get()
                .await()

            data.documents.forEach {
                val uid = it.get(USER_ID) as String
                val phoneNumber = it.get(PHONE_NUMBER) as String
                phonesMap[uid] = phoneNumber
            }

            startIndex += MAX_SIZE_WHERE_IN
        }

        return phonesMap
    }

    override suspend fun getPhonesByUserIds(userIds: List<String>) = filterByField(USER_ID, userIds)

    override suspend fun getUsersByPhones(phoneNumbers: List<String>) = filterByField(PHONE_NUMBER, phoneNumbers)
}