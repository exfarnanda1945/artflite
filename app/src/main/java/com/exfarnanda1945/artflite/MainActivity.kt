package com.exfarnanda1945.artflite

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.exfarnanda1945.artflite.ui.theme.ArtfliteTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {
    private val _isCameraGranted = MutableStateFlow(false)
    private val cameraIsGranted = _isCameraGranted.asStateFlow()

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            _isCameraGranted.value = isGranted

            Toast.makeText(
                this@MainActivity,
                "Camera permission is ${if (isGranted) "Granted" else "Denied"}",
                Toast.LENGTH_SHORT
            ).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) -> {
                _isCameraGranted.value = true
            }

            else -> cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
        setContent {
            ArtfliteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding), contentAlignment = Alignment.Center
                    ) {
                        CameraPreviewScreen(isCameraGranted = cameraIsGranted)
                    }
                }
            }
        }
    }
}