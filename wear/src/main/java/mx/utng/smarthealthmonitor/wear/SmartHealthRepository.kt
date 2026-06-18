package mx.utng.smarthealthmonitor.wear

import android.content.Context
import kotlinx.coroutines.flow.*
import mx.utng.smarthealthmonitor.wear.data.db.LecturaFC
import mx.utng.smarthealthmonitor.wear.data.db.LecturaFCDao
import mx.utng.smarthealthmonitor.wear.data.db.SmartHealthDB

object SmartHealthRepository {
    private val _fcFlow = MutableStateFlow(0)
    val fcFlow: StateFlow<Int> = _fcFlow.asStateFlow()

    private val _pasosFlow = MutableStateFlow(0)
    val pasosFlow: StateFlow<Int> = _pasosFlow.asStateFlow()

    private var dao: LecturaFCDao? = null

    fun init(context: Context) {
        dao = SmartHealthDB.getDatabase(context).lecturaDao()
    }

    suspend fun updateFC(bpm: Int) {
        android.util.Log.d("SmartHealthRepository", "REPOSITORIO RECIBE: $bpm")
        _fcFlow.value = bpm
        dao?.insertar(LecturaFC(valorBpm = bpm))
    }

    fun updatePasos(pasos: Int) {
        _pasosFlow.value = pasos
    }

    fun obtenerHistorial(): Flow<List<LecturaFC>> = 
        dao?.obtenerUltimas() ?: emptyFlow()
}
