package mx.utng.srcp.smarthealthmonitor.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "lecturas_fc")
data class LecturaFC(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val valorBpm: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val hora: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    val esNormal: Boolean = valorBpm in 60..100
)
