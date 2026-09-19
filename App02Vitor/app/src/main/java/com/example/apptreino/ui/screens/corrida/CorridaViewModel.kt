package com.example.apptreino.ui.screens.corrida

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptreino.data.entity.CorridaEntity
import com.example.apptreino.data.repository.CorridaRepository
import com.example.apptreino.ui.state.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class CorridaViewModel(
    private val corridaRepository: CorridaRepository
) : ViewModel() {

    private val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    val uiState: StateFlow<UiState<List<CorridaUiModel>>> =
        corridaRepository.listarCorridas()
            .map<List<CorridaEntity>, UiState<List<CorridaUiModel>>> { corridas ->
                if (corridas.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(corridas.map { it.paraUiModel() })
                }
            }
            .catch { erro -> emit(UiState.Error(erro.message ?: "Erro ao carregar as corridas")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    private fun CorridaEntity.paraUiModel(): CorridaUiModel {
        val minutos = tempoTotalSegundos / 60
        val paceMinutos = paceSegundosPorKm / 60
        val paceSegundos = paceSegundosPorKm % 60
        return CorridaUiModel(
            corridaId = corridaId,
            dataFormatada = formatoData.format(dataRegistro),
            distanciaFormatada = "%.1f km".format(Locale.US, distanciaKm),
            tempoFormatado = "Tempo: ${minutos}min",
            paceFormatado = "Pace: %d'%02d\"/km".format(paceMinutos, paceSegundos)
        )
    }

    fun registrarCorrida(distanciaKm: Double, tempoTotalSegundos: Int) {
        if (distanciaKm <= 0 || tempoTotalSegundos <= 0) return
        val paceSegundosPorKm = (tempoTotalSegundos / distanciaKm).roundToInt()
        viewModelScope.launch {
            corridaRepository.registrarCorrida(distanciaKm, tempoTotalSegundos, paceSegundosPorKm)
        }
    }
}
