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
        val fcDataPoints = dataPoints.getData(DataType.HEART_RATE_BPM)
        Log.d("HealthDataService", "--- EVENTO DE SENSOR RECIBIDO ---")
        Log.d("HealthDataService", "Puntos detectados: ${fcDataPoints.size}")

        fcDataPoints.forEach { dataPoint ->
            Log.d("HealthDataService", "Punto crudo: $dataPoint")
            val bpmValue = when (dataPoint) {
                is SampleDataPoint<*> -> (dataPoint.value as? Number)?.toDouble()
                else -> null
            }
            
            if (bpmValue != null) {
                val bpm = bpmValue.toInt()
                Log.d("HealthDataService", "¡BPM PROCESADO Y ENVIADO!: $bpm")
                scope.launch {
                    SmartHealthRepository.updateFC(bpm)
                    wearDataSender.enviarFC(bpm)
                }
            } else {
                Log.w("HealthDataService", "No se pudo extraer el valor del punto")
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
