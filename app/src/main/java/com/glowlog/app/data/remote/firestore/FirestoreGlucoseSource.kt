package com.glowlog.app.data.remote.firestore

import com.glowlog.app.data.remote.firestore.dto.GlucoseReadingDto
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreGlucoseSource @Inject constructor(
    firestore: FirebaseFirestore
) : BaseFirestoreSource<GlucoseReadingDto>(
    firestore,
    "glucose_readings",
    GlucoseReadingDto::class.java
)
