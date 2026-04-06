package com.example.faqrpay.ui.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun QRScreen(
    viewModel: QRViewModel = viewModel(factory = QRViewModelFactory(LocalContext.current))
) {
    // 1. Add scroll state so user can scroll if screen is too small
    val scrollState = rememberScrollState()

    // 2. Wrap everything in ONE parent Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState), // Handle overflow
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nová platba", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = viewModel.amountInput,
            onValueChange = { viewModel.amountInput = it },
            label = { Text("Částka") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            enabled = viewModel.qrBitmap == null // Disable input once QR is generated
        )

        // Only show "Generate" button if no QR is active
        if (viewModel.qrBitmap == null) {
            Button(
                onClick = { viewModel.generatePayment() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Generovat QR kód")
            }
        }

        if (viewModel.isLoading) {
            CircularProgressIndicator(Modifier.padding(top = 16.dp))
        }

        // 3. This is now INSIDE the main Column, so it naturally appears below
        viewModel.qrBitmap?.let { bitmap ->
            Spacer(Modifier.height(24.dp))

            // The QR code itself
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Platba",
                modifier = Modifier.size(250.dp)
            )

            Spacer(Modifier.height(16.dp))

            // VALIDATION SECTION (Also inside the main Column)
            if (viewModel.isPaidSuccessfully) {
                // SUCCESS STATE
                Text("Platba přijata!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Green
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { viewModel.reset() }) {
                    Text("Nová platba")
                }
            } else {
                // PENDING STATE
                if (viewModel.validationAttempted && viewModel.cooldownSeconds > 0) {
                    Text("Platba dosud nedorazila", color = Color.Red)
                    Text("Zkusit znovu za: ${viewModel.cooldownSeconds}s")
                    Spacer(Modifier.height(8.dp))
                }

                Row {
                    Button(
                        onClick = { viewModel.validateStatus() },
                        enabled = !viewModel.isLoading && viewModel.cooldownSeconds == 0
                    ) {
                        Text("Ověřit zda dorazilo")
                    }

                    Spacer(Modifier.width(8.dp)) // Fixed typo (removed the '1')

                    OutlinedButton(onClick = { viewModel.reset() }) {
                        Text("Zrušit")
                    }
                }
            }
        }

        viewModel.errorMessage?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }
    }
}