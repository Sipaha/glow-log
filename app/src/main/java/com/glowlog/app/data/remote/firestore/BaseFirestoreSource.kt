package com.glowlog.app.data.remote.firestore

import com.glowlog.app.data.remote.firestore.dto.FirestoreDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

abstract class BaseFirestoreSource<T : FirestoreDto>(
    private val firestore: FirebaseFirestore,
    private val collectionName: String,
    private val dtoClass: Class<T>
) {
    private fun collection(userId: String) =
        firestore.collection("users").document(userId).collection(collectionName)

    suspend fun pushReadings(userId: String, readings: List<T>) {
        for (chunk in readings.chunked(BATCH_LIMIT)) {
            val batch = firestore.batch()
            chunk.forEach { dto ->
                val docRef = collection(userId).document(dto.id)
                batch.set(docRef, dto)
            }
            batch.commit().await()
        }
    }

    suspend fun pullReadings(userId: String, sinceTimestamp: Long): List<T> {
        return collection(userId)
            .whereGreaterThan("updatedAt", sinceTimestamp)
            .get()
            .await()
            .toObjects(dtoClass)
    }

    companion object {
        private const val BATCH_LIMIT = 500
    }
}
