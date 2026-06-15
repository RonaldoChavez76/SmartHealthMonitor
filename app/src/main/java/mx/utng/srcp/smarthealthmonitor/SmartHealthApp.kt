package mx.utng.srcp.smarthealthmonitor

import android.app.Application
import mx.utng.srcp.smarthealthmonitor.data.SmartHealthRepository

class SmartHealthApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar Room a través del Repositorio
        SmartHealthRepository.init(this)
    }
}
