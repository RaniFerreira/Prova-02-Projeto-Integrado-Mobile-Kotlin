package com.example.apptreino.di

import android.content.Context
import com.example.apptreino.data.AppDatabase
import com.example.apptreino.data.repository.CargaHistoricoRepository
import com.example.apptreino.data.repository.CorridaRepository
import com.example.apptreino.data.repository.ExercicioRepository
import com.example.apptreino.data.repository.TreinoRepository

class AppContainer(context: Context) {

    private val database: AppDatabase = AppDatabase.getInstance(context)

    val treinoRepository: TreinoRepository by lazy {
        TreinoRepository(database.treinoDao())
    }

    val exercicioRepository: ExercicioRepository by lazy {
        ExercicioRepository(database.exercicioDao())
    }

    val cargaHistoricoRepository: CargaHistoricoRepository by lazy {
        CargaHistoricoRepository(database.cargaHistoricoDao())
    }

    val corridaRepository: CorridaRepository by lazy {
        CorridaRepository(database.corridaDao())
    }
}
