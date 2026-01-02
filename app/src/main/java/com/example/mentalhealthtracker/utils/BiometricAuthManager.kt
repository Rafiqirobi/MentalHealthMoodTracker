package com.example.mentalhealthtracker.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Manager class for handling biometric authentication
 * Supports fingerprint and face recognition
 */
class BiometricAuthManager(private val activity: FragmentActivity) {

    private val biometricManager = BiometricManager.from(activity)

    /**
     * Check if biometric authentication is available on this device
     * @return BiometricStatus indicating availability
     */
    fun checkBiometricAvailability(): BiometricStatus {
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
        )) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricStatus.AVAILABLE

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricStatus.NO_HARDWARE

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricStatus.HARDWARE_UNAVAILABLE

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricStatus.NONE_ENROLLED

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                BiometricStatus.SECURITY_UPDATE_REQUIRED

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                BiometricStatus.UNSUPPORTED

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                BiometricStatus.UNKNOWN_ERROR

            else -> BiometricStatus.UNKNOWN_ERROR
        }
    }

    /**
     * Show biometric authentication prompt
     * @param title Title of the prompt
     * @param subtitle Subtitle of the prompt
     * @param description Description text
     * @param negativeButtonText Text for negative button
     * @param onSuccess Callback when authentication succeeds
     * @param onError Callback when authentication error occurs
     * @param onFailed Callback when authentication fails
     */
    fun authenticate(
        title: String = "Biometric Authentication",
        subtitle: String = "Verify your identity",
        description: String = "Use your fingerprint or face to access your mood data",
        negativeButtonText: String = "Cancel",
        onSuccess: () -> Unit,
        onError: (errorCode: Int, errorMessage: String) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    // Don't treat user cancellation as an error
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                        errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        onFailed()
                    } else {
                        onError(errorCode, errString.toString())
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)
            .setConfirmationRequired(true)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Get user-friendly message for biometric status
     */
    fun getStatusMessage(status: BiometricStatus): String {
        return when (status) {
            BiometricStatus.AVAILABLE ->
                "Biometric authentication is available"

            BiometricStatus.NO_HARDWARE ->
                "This device doesn't support biometric authentication"

            BiometricStatus.HARDWARE_UNAVAILABLE ->
                "Biometric hardware is currently unavailable"

            BiometricStatus.NONE_ENROLLED ->
                "No biometric credentials enrolled. Please set up fingerprint or face recognition in your device settings"

            BiometricStatus.SECURITY_UPDATE_REQUIRED ->
                "Security update required to use biometric authentication"

            BiometricStatus.UNSUPPORTED ->
                "Biometric authentication is not supported on this device"

            BiometricStatus.UNKNOWN_ERROR ->
                "Unknown error occurred with biometric authentication"
        }
    }

    /**
     * Enum representing biometric availability status
     */
    enum class BiometricStatus {
        AVAILABLE,
        NO_HARDWARE,
        HARDWARE_UNAVAILABLE,
        NONE_ENROLLED,
        SECURITY_UPDATE_REQUIRED,
        UNSUPPORTED,
        UNKNOWN_ERROR
    }
}