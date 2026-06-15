/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up-to-date changes to the libraries and their usages.
 */

package mx.utng.srcp.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.utng.srcp.smarthealthmonitor.wear.HealthDataService
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import mx.utng.srcp.wear.R
import mx.utng.srcp.wear.presentation.theme.SmartHealthMonitorTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import mx.utng.srcp.smarthealthmonitor.wear.WearDataSender
import mx.utng.srcp.smarthealthmonitor.wear.WearRepository
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.wear.compose.material3.TitleCard
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            registrarHealthServices()
        } else {
            Log.e("MainActivity", "Permisos denegados")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = mutableListOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        
        // Agregar permisos adicionales para APIs modernas
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
            WearApp("Android")
        }
    }

    private fun registrarHealthServices() {
        lifecycleScope.launch {
            try {
                HealthDataService.registrar(applicationContext)
                Log.d("MainActivity", "Health Services registrado correctamente")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al registrar Health Services", e)
            }
        }
    }
}

@Composable
fun WearApp(greetingName: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val wearDataSender = remember { WearDataSender(context) }
    
    // Observar los datos locales
    val currentFC by WearRepository.fcFlow.collectAsState()
    val currentPasos by WearRepository.pasosFlow.collectAsState()

    SmartHealthMonitorTheme {
        AppScaffold {
            val listState = rememberTransformingLazyColumnState()
            val transformationSpec = rememberTransformationSpec()
            ScreenScaffold(
                scrollState = listState,
                edgeButton = {
                    EdgeButton(
                        onClick = { 
                            val fc = (60..100).random()
                            WearRepository.updateFC(fc)
                            scope.launch { wearDataSender.enviarFC(fc) }
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                    ) {
                        Text("Simular FC")
                    }
                },
            ) { contentPadding -> 
                TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
                    item {
                        ListHeader(
                            modifier = Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text(text = "Monitor de Salud")
                        }
                    }

                    // Nueva Tarjeta para mostrar el BPM actual en el reloj
                    item {
                        TitleCard(
                            onClick = { },
                            title = { Text("Frecuencia Cardíaca") },
                            modifier = Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (currentFC > 0) "$currentFC" else "--",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentFC > 100) Color.Red else MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(text = "bpm", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { 
                                val fc = (60..140).random()
                                WearRepository.updateFC(fc)
                                scope.launch { wearDataSender.enviarFC(fc) }
                            },
                            modifier = Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text("Simular Ritmo")
                        }
                    }
                    item {
                        Button(
                            onClick = { 
                                val p = (100..5000).random()
                                WearRepository.updatePasos(p)
                                scope.launch { wearDataSender.enviarPasos(p) }
                            },
                            modifier = Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text("Simular Pasos ($currentPasos)")
                        }
                    }
                    item {
                        Button(
                            onClick = { 
                                WearRepository.updateFC(0)
                                scope.launch { wearDataSender.enviarFC(0) }
                            },
                            modifier = Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text("Limpiar Datos")
                        }
                    }
                }
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}