package mx.utng.smarthealthmonitor.wear.data.db

import android.content.Context
import androidx.room.*

@Database(entities = [LecturaFC::class], version = 1, exportSchema = false)
abstract class SmartHealthDB : RoomDatabase() {
    abstract fun lecturaDao(): LecturaFCDao

    companion object {
        @Volatile
        private var INSTANCE: SmartHealthDB? = null

        fun getDatabase(context: Context): SmartHealthDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartHealthDB::class.java,
                    "smarthealthmonitor_wear_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
