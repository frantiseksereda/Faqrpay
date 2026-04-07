package com.example.faqrpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.faqrpay.ui.history.HistoryScreen
import com.example.faqrpay.ui.qr.QRScreen
import com.example.faqrpay.ui.settings.SettingsScreen
import com.example.faqrpay.ui.settings.SettingsViewModel
import com.example.faqrpay.ui.theme.FaqrpayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FaqrpayTheme {
                FaqrpayApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun FaqrpayApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.QR) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentDestination) {
                    AppDestinations.QR -> {
                        QRScreen()

                    }
                    AppDestinations.TRANSACTIONS -> {
                        HistoryScreen()
                    }
                    AppDestinations.SETTINGS -> {
                        // SHOW YOUR REAL SETTINGS SCREEN HERE
                        SettingsScreen()
                    }
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    QR("Platba", R.drawable.qr_code_24px),
    TRANSACTIONS("Historie", R.drawable.receipt_long_24px),
    SETTINGS("Nastavení", R.drawable.settings_24px),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FaqrpayTheme {
        Greeting("Android")
    }
}