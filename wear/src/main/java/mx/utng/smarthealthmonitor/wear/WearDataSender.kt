package mx.utng.smarthealthmonitor.wear

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

class WearDataSender(private val context: Context) {

    suspend fun enviarFC(bpm: Int) {
        Log.d("WearDataSender", "Intentando enviar FC: $bpm")
        enviarMensaje("/smarthealthmonitor/fc", bpm.toString())
    }

    suspend fun enviarPasos(pasos: Int) {
        Log.d("WearDataSender", "Intentando enviar Pasos: $pasos")
        enviarMensaje("/smarthealthmonitor/pasos", pasos.toString())
    }

    private suspend fun enviarMensaje(path: String, data: String) {
        try {
            // 1. Intentamos obtener TODOS los nodos conectados directamente
            val allNodes = Wearable.getNodeClient(context).connectedNodes.await()
            Log.d("WearDataSender", "Nodos conectados encontrados: ${allNodes.size}")

            if (allNodes.isEmpty()) {
                Log.w("WearDataSender", "FALLO: No hay dispositivos vinculados por Bluetooth virtual.")
                return
            }

            // 2. Enviamos el mensaje a todos los nodos encontrados
            allNodes.forEach { node ->
                Log.d("WearDataSender", "Enviando a: ${node.displayName}")
                Wearable.getMessageClient(context).sendMessage(
                    node.id,
                    path,
                    data.toByteArray()
                ).await()
            }
        } catch (e: Exception) {
            Log.e("WearDataSender", "Error al enviar mensaje", e)
        }
    }
}
