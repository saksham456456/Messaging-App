package com.synq.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
    private var isBiometricPromptShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Wait until we decide if we need biometrics
        splashScreen.setKeepOnScreenCondition { !isAuthenticated && tokenManager.getToken() != null && canAuthenticateWithBiometrics() && !isBiometricPromptShowing }

        if (tokenManager.getToken() != null && canAuthenticateWithBiometrics()) {
            isBiometricPromptShowing = true
            showBiometricPrompt()
        } else {
            isAuthenticated = true
            loadUi()
        }
    }

    private fun loadUi() {
        if (!isAuthenticated) return
        setContent {
            SynqTheme {
                val navController = rememberNavController()
                val targetDestination = if (tokenManager.getToken() != null) {
                    if (tokenManager.isProfileComplete()) Screen.ChatList.route else Screen.ProfileSetup.route
                } else {
                    Screen.Auth.route
                }

                // We show the custom animated splash screen if the user is naturally launching the app
                SynqNavGraph(
                    navController = navController,
                    startDestination = targetDestination,
                    isInitialLaunch = true
                )
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
                    Toast.makeText(applicationContext, "Authentication required", Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isAuthenticated = true
                    isBiometricPromptShowing = false
                    loadUi()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
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
