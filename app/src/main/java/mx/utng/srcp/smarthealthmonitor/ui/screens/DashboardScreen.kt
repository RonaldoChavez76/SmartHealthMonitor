package mx.utng.srcp.smarthealthmonitor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.utng.srcp.smarthealthmonitor.data.SmartHealthRepository
import mx.utng.srcp.smarthealthmonitor.data.models.LecturaFC
import mx.utng.srcp.smarthealthmonitor.data.models.MockData
import mx.utng.srcp.smarthealthmonitor.ui.components.FilaHistorial
import mx.utng.srcp.smarthealthmonitor.ui.components.TarjetaDato
import mx.utng.srcp.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme
import mx.utng.srcp.smarthealthmonitor.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onHistorialClick: () -> Unit = {},
    onAlertClick: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel() // inyección automática
) {
    // collectAsState() convierte StateFlow en State de Compose
    val fc by viewModel.fc.collectAsState()
    val pasos by viewModel.pasos.collectAsState()
    val historial = viewModel.historial

    SmartHealthMonitorTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "SmartHealth",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAlertClick,
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Enviar alerta de emergencia",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            }
        ) { paddingValues ->
            // Δ paddingValues OBLIGATORIO
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // -- Tarjeta FC --
                item {
                    TarjetaDato(
                        valor = "$fc",
                        unidad = "bpm",
                        label = "Frecuencia cardíaca",
                        colorValor = MaterialTheme.colorScheme.error
                    )
                }

                // -- Tarjeta Pasos --
                item {
                    TarjetaDato(
                        valor = "%,d".format(pasos),
                        unidad = "pasos",
                        label = "Pasos del día",
                        colorValor = MaterialTheme.colorScheme.primary
                    )
                }

                // -- Encabezado historial --
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historial reciente",
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextButton(onClick = onHistorialClick) {
                            Text("Ver todo")
                        }
                    }
                }

                // -- Lista del historial --
                items(historial, key = { it.id }) { lectura ->
                    FilaHistorial(lectura = lectura)
                }

                // Botón de simulación - SOLO PARA DEBUG
                item {
                    if (mx.utng.srcp.smarthealthmonitor.BuildConfig.DEBUG) {
                        OutlinedButton(
                            onClick = {
                                // Simular lectura del wearable
                                val fcSimulado = (60..110).random()
                                SmartHealthRepository.actualizarFC(fcSimulado)
                                SmartHealthRepository.actualizarPasos((3000..8000).random())
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Simular dato del wearable (DEBUG)")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Dashboard - Light",
    showSystemUi = true, device = "id:pixel_6")
@Preview(showBackground = true, name = "Dashboard - Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DashboardScreenPreview() {
    SmartHealthMonitorTheme {
        DashboardScreen()
    }
}
