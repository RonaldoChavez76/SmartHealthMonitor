package mx.utng.srcp.smarthealthmonitor.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.utng.srcp.smarthealthmonitor.LoginScreen
import mx.utng.srcp.smarthealthmonitor.ui.screens.AlertaScreen
import mx.utng.srcp.smarthealthmonitor.ui.screens.DashboardScreen
import mx.utng.srcp.smarthealthmonitor.ui.screens.HistorialScreen
import mx.utng.srcp.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.utng.srcp.smarthealthmonitor.ui.viewmodel.DashboardViewModel

@Composable
fun SmartHealthNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // -- Login ---------------------------------------
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true // eliminar Login del back stack
                        }
                    }
                }
            )
        }

        // -- Dashboard -----------------------------------
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onHistorialClick = {
                    navController.navigate(Screen.Historial.route)
                },
                onAlertClick = {
                    navController.navigate(Screen.Alerta.route)
                }
            )
        }

        // -- Historial -----------------------------------
        composable(Screen.Historial.route) {
            HistorialScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // -- Alerta --------------------------------------
        composable(Screen.Alerta.route) {
            val viewModel: DashboardViewModel = viewModel()
            val fc by viewModel.fc.collectAsState()
            
            AlertaScreen(
                fc = fc,
                onDismiss = { navController.popBackStack() },
                onConfirm = {
                    // Aquí iría la lógica para notificar a contactos
                    // Por ahora solo cerramos después de confirmar
                    navController.popBackStack()
                }
            )
        }
    }
}

// Pantalla temporal para destinos no implementados aún
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEnConstruccion(titulo: String, onBack: () -> Unit) {
    SmartHealthMonitorTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(titulo) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar"
                            )
                        }
                    }
                )
            }
        ) { pad ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pad),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Próximamente: $titulo",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
