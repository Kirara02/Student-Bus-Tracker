package com.uniguard.bustracker.ui.screens.setting

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.uniguard.bustracker.ui.composable.TextField
import com.uniguard.bustracker.ui.screens.setting.viewmodel.SettingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                title = {
                    Text(
                        text = "Settings",
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Sharp.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
            )
        }
    )
    { paddingValues ->

        // MutableState for temporary data storage
        var tempUrl by remember { mutableStateOf("") }
        var tempIdDevice by remember { mutableStateOf("") }

        val url by viewModel.url.collectAsState(initial = "http://localhost")
        val idDevice by viewModel.idDevice.collectAsState(initial = "")

        // Update temp values when state from ViewModel changes
        LaunchedEffect(url, idDevice) {
            url.let { tempUrl = it }
            idDevice.let { tempIdDevice = it }
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            TextField(
                value = tempUrl,
                onValueChange = { tempUrl = it },
                label = "URL",
                hint = "Input your URL here (e.g. http://localhost)"
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = tempIdDevice,
                onValueChange = { tempIdDevice = it },
                label = "ID Device",
                hint = "Input your device ID here"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.updateUrl(tempUrl)
                    viewModel.updateIdDevice(tempIdDevice)

                    restartApp(context)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Settings")
            }
        }
    }
}

private fun restartApp(context: Context) {
    val packageManager = context.packageManager
    val intent = packageManager.getLaunchIntentForPackage(context.packageName)
    val componentName = intent?.component
    val mainIntent = Intent.makeRestartActivityTask(componentName)

    // Adding flags to create a clean restart
    context.startActivity(mainIntent)

    // Kill the current process
    android.os.Process.killProcess(android.os.Process.myPid())
}