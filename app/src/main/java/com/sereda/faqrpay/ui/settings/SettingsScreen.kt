package com.sereda.faqrpay.ui.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sereda.faqrpay.domain.SettingsRepository
import com.sereda.faqrpay.util.ServiceLocator

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current)
    )
) {
    var textFieldValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var confirmPasswordValue by remember { mutableStateOf("") }

    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (viewModel.authState) {
            SettingsViewModel.AuthState.SETUP -> {
                Text("Nastavte heslo", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Heslo nastavujeme proto, aby nemohl kdokoli zobrazit a změnit používaný API token (a tím například změnit cílový účet pro platby)",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = passwordValue,
                    onValueChange = { passwordValue = it },
                    label = { Text("Nové heslo") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = confirmPasswordValue,
                    onValueChange = { confirmPasswordValue = it },
                    label = { Text("Potvrďte heslo") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = viewModel.errorMessage != null
                )

                viewModel.errorMessage?.let {
                    Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.setupNewPassword(passwordValue, confirmPasswordValue)
                    }
                ) {
                    Text("Uložit a pokračovat")
                }

            }

            SettingsViewModel.AuthState.LOCKED -> {
                //Icon(Icon, contentDescription = null, modifier = Modifier.size(64.dp))
                Text("Zadejte heslo", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    label = { Text("Heslo...") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = viewModel.errorMessage != null
                )
                Spacer(modifier = Modifier.height(24.dp))
                viewModel.errorMessage?.let { Text(it, color = Color.Red) }
                Button(onClick = { viewModel.unlock(textFieldValue) }) {
                    Text("Odemknout")
                }
            }

            SettingsViewModel.AuthState.UNLOCKED -> {
                // THIS IS YOUR ACTUAL SETTINGS GUI
                ActualSettingsContent(viewModel)
            }
        }
    }
}

@Composable
fun ActualSettingsContent(viewModel: SettingsViewModel) {
    // 1. Local state for the text fields while the user is typing
    var accountInput by remember { mutableStateOf(viewModel.currentAccount) }
    var tokenInput by remember { mutableStateOf(viewModel.currentToken) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Nastavení bankovního účtu",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- ACCOUNT NUMBER SECTION ---
        Text("Číslo účtu:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (viewModel.isLoading) "Načítání..." else "${viewModel.currentAccount}/2010",
            style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Text("IBAN:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (viewModel.isLoading) "Načítání..." else viewModel.currentIban,
            style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // --- TOKEN SECTION ---
        Text("Fio API Token", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = tokenInput,
            onValueChange = { tokenInput = it },
            label = { Text("Zadejte Fio API token") },
            modifier = Modifier.fillMaxWidth(),
            // Securely hide the token characters
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- SAVE ACTION ---
        Button(
            onClick = { viewModel.saveSettings(token = tokenInput) },
            modifier = Modifier.fillMaxWidth()
        ) {
            //Icon(Icons.Default.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Uložit nastavení")
            }
        }

        // Show a success message if defined in ViewModel
        viewModel.successMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = Color(0xFF4CAF50), style = MaterialTheme.typography.bodyMedium)
        }

        viewModel.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            // Using your ServiceLocator to get the repository/manager
            val repo = SettingsRepository(
                ServiceLocator.getSecureSettings(context),
                apiService = ServiceLocator.fioApiService
            )
            return SettingsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}