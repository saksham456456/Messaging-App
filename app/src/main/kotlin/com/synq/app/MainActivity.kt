package com.synq.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.synq.app.core.network.TokenManager
import com.synq.app.core.theme.SynqTheme
import com.synq.app.presentation.navigation.Screen
import com.synq.app.presentation.navigation.SynqNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : androidx.fragment.app.FragmentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    private var isAuthenticated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // If user has a token (is logged in), prompt biometric authentication before showing UI
        if (tokenManager.getToken() != null && canAuthenticateWithBiometrics()) {
            showBiometricPrompt()
        } else {
            // Either not logged in, or biometrics not available. Proceed to normal routing.
            isAuthenticated = true
            loadUi()
        }
    }

    private fun loadUi() {
        if (!isAuthenticated) return // Wait until authenticated
        setContent {
            SynqTheme {
                val navController = rememberNavController()
                val startDestination = if (tokenManager.getToken() != null) {
                    if (tokenManager.isProfileComplete()) Screen.ChatList.route else Screen.ProfileSetup.route
                } else {
                    Screen.Auth.route
                }
                SynqNavGraph(navController = navController, startDestination = startDestination)
            }
        }
    }

    private fun canAuthenticateWithBiometrics(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // If they cancel or fail, close the app for security
                    Toast.makeText(applicationContext, "Authentication required", Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isAuthenticated = true
                    loadUi()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Prompt handles retry internally
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Synq")
            .setSubtitle("Confirm your identity to view your messages")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
