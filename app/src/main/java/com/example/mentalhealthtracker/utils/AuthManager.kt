package com.example.mentalhealthtracker.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Get current user
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Sign in with email and password
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Sign in failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Register new user with email and password
     */
    suspend fun register(email: String, password: String, name: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                // Update user profile with name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(profileUpdates).await()
                Result.success(user)
            } ?: Result.failure(Exception("Registration failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update user password
     */
    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("No user logged in"))
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign out
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Get user display name
     */
    fun getUserDisplayName(): String {
        return auth.currentUser?.displayName ?: "User"
    }

    /**
     * Get user email
     */
    fun getUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    /**
     * Get user creation timestamp
     */
    fun getUserCreationTime(): Long {
        return auth.currentUser?.metadata?.creationTimestamp ?: 0L
    }
}