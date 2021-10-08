package com.app.consultationpoint.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import timber.log.Timber
import java.util.*

object BiometricUtils {
    private fun hasBiometricCapability(context: Context): Int {
        val biometricManager = BiometricManager.from(context)
        Timber.d("Biometric capability ${biometricManager.canAuthenticate()}")
        return biometricManager.canAuthenticate()
    }

    fun isBiometricReady(context: Context) =
        hasBiometricCapability(context) == BiometricManager.BIOMETRIC_SUCCESS

    fun isBiometricNotEnrolled(context: Context) =
        hasBiometricCapability(context) == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

    private fun setBiometricPromptInfo(
        title: String,
        subTitle: String,
        description: String,
        allowDeviceCredential: Boolean
    ): BiometricPrompt.PromptInfo {
        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
//            .setSubtitle(subTitle)
            .setDescription(description)

        builder.apply {
            if (allowDeviceCredential)
                setDeviceCredentialAllowed(true)
            else
                setNegativeButtonText("Cancel")
        }

        return builder.build()
    }

    private fun initBiometricPrompt(
        activity: AppCompatActivity,
        listener: BiometricAuthListener
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                listener.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Timber.d("Authentication failed for an unknown reason")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                listener.onBiometricAuthenticationSuccess(result)
            }
        }

        return BiometricPrompt(activity, executor, callback)
    }

    fun showBiometricPrompt(
        title: String = "Login to Alpha Healthcare",
        subTitle: String = "Login with your biometric credential",
        description: String = "Input your Fingerprint or FaceID to ensure it's you!",
        activity: AppCompatActivity,
        listener: BiometricAuthListener,
        cryptoObject: BiometricPrompt.CryptoObject? = null,
        allowDeviceCredential: Boolean = false
    ) {
        val promptInfo = setBiometricPromptInfo(title, subTitle, description, allowDeviceCredential)

        val biometricPrompt = initBiometricPrompt(activity, listener)

        biometricPrompt.apply {
            if (cryptoObject == null)
                authenticate(promptInfo)
            else
                authenticate(promptInfo, cryptoObject)
        }
    }

    interface BiometricAuthListener {
        fun onAuthenticationError(errorCode: Int, errString: CharSequence)
        fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult)
    }
}