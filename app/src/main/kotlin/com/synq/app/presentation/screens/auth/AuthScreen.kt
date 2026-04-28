package com.synq.app.presentation.screens.auth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.synq.app.core.theme.TealAccent
@Composable fun AuthScreen(viewModel: AuthViewModel = hiltViewModel(), onAuthSuccess: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.isSuccess) { if (uiState.isSuccess) onAuthSuccess() }
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("Synq", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = TealAccent, modifier = Modifier.padding(bottom = 8.dp))
            Text("Connect instantly. Always in sync.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 32.dp))
            if (!uiState.isOtpSent) {
                OutlinedTextField(value = uiState.phoneNumber, onValueChange = viewModel::updatePhoneNumber, label = { Text("Phone Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = viewModel::requestOtp, modifier = Modifier.fillMaxWidth(), enabled = !uiState.isLoading) { Text("Continue") }
            } else {
                OutlinedTextField(value = uiState.otp, onValueChange = viewModel::updateOtp, label = { Text("Enter OTP") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = viewModel::verifyOtp, modifier = Modifier.fillMaxWidth(), enabled = !uiState.isLoading) { Text("Verify") }
            }
            if (uiState.isLoading) { Spacer(modifier = Modifier.height(16.dp)); CircularProgressIndicator() }
            uiState.errorMessage?.let { error -> Spacer(modifier = Modifier.height(16.dp)); Text(text = error, color = MaterialTheme.colorScheme.error) }
        }
    }
}
