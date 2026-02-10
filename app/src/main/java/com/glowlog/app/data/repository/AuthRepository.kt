package com.glowlog.app.data.repository

import com.glowlog.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<UserProfile?>
    val isSignedIn: Flow<Boolean>
    suspend fun signInWithGoogle(idToken: String): Result<UserProfile>
    suspend fun signOut()
}
