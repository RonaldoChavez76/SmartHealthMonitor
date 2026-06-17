package mx.utng.smarthealthmonitor.wear.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.wear.HealthDataService
import mx.utng.smarthealthmonitor.wear.presentation.theme.SmartHealthWearTheme

class WearMainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            registrarHealthServices()
        } else {
            Log.e("WearMainActivity", "Permisos denegados")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = mutableListOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.BODY_SENSORS_BACKGROUND)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add("android.permission.health.READ_HEART_RATE")
        }

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            registrarHealthServices()
        } else {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        }

        setContent {
            SmartHealthWearTheme {
                // TODO Ej.02: reemplazar con WearNavGraph
                WearDashboardScreenPlaceholder()
            }
        }
    }

    private fun registrarHealthServices() {
        lifecycleScope.launch {
            try {
                HealthDataService.registrar(applicationContext)
                Log.d("WearMainActivity", "Health Services registrado correctamente")
            } catch (e: Exception) {
                Log.e("WearMainActivity", "Error al registrar Health Services", e)
            }
        }
    }
}

@Composable
fun WearDashboardScreenPlaceholder() {
    // Placeholder mientras se crea el Dashboard real en el Ejercicio 02
    androidx.wear.compose.material.Text("Wear Dashboard Screen")
}
