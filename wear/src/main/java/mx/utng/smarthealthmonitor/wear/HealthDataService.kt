package mx.utng.smarthealthmonitor.wear

import android.content.Context
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.guava.await

class HealthDataService : PassiveListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var wearDataSender: WearDataSender

    override fun onCreate() {
        super.onCreate()
        Log.d("HealthDataService", "Servicio iniciado correctamente")
        wearDataSender = WearDataSender(this) // S6: MessageClient
    }

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        Log.d("HealthDataService", "--- EVENTO RECIBIDO ---")
        
        // Intentar obtener ritmo cardíaco
        val fcPoints = dataPoints.getData(DataType.HEART_RATE_BPM)
        fcPoints.forEach { point ->
            val bpm = (point.value as? Number)?.toInt() ?: 0
            if (bpm > 0) {
                Log.d("HealthDataService", "SENSOR DICE: $bpm bpm")
                scope.launch {
                    SmartHealthRepository.updateFC(bpm)
                    wearDataSender.enviarFC(bpm)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        suspend fun registrar(context: Context) {
            val hsClient = HealthServices.getClient(context)
            val passiveClient = hsClient.passiveMonitoringClient

            val config = PassiveListenerConfig.builder()
                .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                .setShouldUserActivityInfoBeRequested(true)
                .build()

            passiveClient.setPassiveListenerServiceAsync(
                HealthDataService::class.java,
                config
            ).await()
        }
    }
}
