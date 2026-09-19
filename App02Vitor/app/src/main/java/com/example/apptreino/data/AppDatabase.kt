package com.example.apptreino.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.apptreino.data.dao.CargaHistoricoDao
import com.example.apptreino.data.dao.CorridaDao
import com.example.apptreino.data.dao.ExercicioDao
import com.example.apptreino.data.dao.TreinoDao
import com.example.apptreino.data.entity.CargaHistoricoEntity
import com.example.apptreino.data.entity.CorridaEntity
import com.example.apptreino.data.entity.ExercicioEntity
import com.example.apptreino.data.entity.TreinoEntity

@Database(
    entities = [
        TreinoEntity::class,
        ExercicioEntity::class,
        CargaHistoricoEntity::class,
        CorridaEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun treinoDao(): TreinoDao
    abstract fun exercicioDao(): ExercicioDao
    abstract fun cargaHistoricoDao(): CargaHistoricoDao
    abstract fun corridaDao(): CorridaDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_treino.db"
                ).fallbackToDestructiveMigration()
                    .build().also { instancia = it }
            }
    }
}
