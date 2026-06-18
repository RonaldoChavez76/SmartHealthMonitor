package mx.utng.smarthealthmonitor.wear

import android.app.Application

class SmartHealthWearApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SmartHealthRepository.init(this)
    }
}
