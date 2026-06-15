package mx.utng.srcp.smarthealthmonitor.wear

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object WearRepository {
    private val _fcFlow = MutableStateFlow(0)
    val fcFlow: StateFlow<Int> = _fcFlow.asStateFlow()

    private val _pasosFlow = MutableStateFlow(0)
    val pasosFlow: StateFlow<Int> = _pasosFlow.asStateFlow()

    fun updateFC(bpm: Int) {
        _fcFlow.value = bpm
    }

    fun updatePasos(pasos: Int) {
        _pasosFlow.value = pasos
    }
}
