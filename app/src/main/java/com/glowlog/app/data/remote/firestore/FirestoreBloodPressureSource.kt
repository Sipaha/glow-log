package com.glowlog.app.data.remote.firestore

import com.glowlog.app.data.remote.firestore.dto.BloodPressureReadingDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreBloodPressureSource @Inject constructor(
    firestore: FirebaseFirestore
) : BaseFirestoreSource<BloodPressureReadingDto>(
    firestore,
    "blood_pressure_readings",
    BloodPressureReadingDto::class.java
)
