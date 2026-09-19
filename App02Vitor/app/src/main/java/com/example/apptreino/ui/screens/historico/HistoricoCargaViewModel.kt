package com.example.apptreino.ui.screens.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptreino.data.relation.ExercicioComHistorico
import com.example.apptreino.data.repository.CargaHistoricoRepository
import com.example.apptreino.data.repository.ExercicioRepository
import com.example.apptreino.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HistoricoCargaViewModel(
    private val exercicioRepository: ExercicioRepository,
    private val cargaHistoricoRepository: CargaHistoricoRepository,
    private val exercicioId: Long
) : ViewModel() {

    private val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    val uiState: StateFlow<UiState<HistoricoCargaUiState>> =
        exercicioRepository.buscarExercicioComHistorico(exercicioId)
            .map<ExercicioComHistorico?, UiState<HistoricoCargaUiState>> { exercicioComHistorico ->
                if (exercicioComHistorico == null) {
                    UiState.Error("Exercicio não encontrado")
                } else {
                    UiState.Success(
                        HistoricoCargaUiState(
                            nomeExercicio = exercicioComHistorico.exercicio.nome,
                            historico = exercicioComHistorico.historico.map { item ->
                                CargaHistoricoUiModel(
                                    historicoId = item.historicoId,
                                    dataFormatada = formatoData.format(item.dataRegistro),
                                    cargaKg = item.cargaKg,
                                    series = item.series,
                                    repeticoes = item.repeticoes
                                )
                            }
                        )
                    )
                }
            }
            .catch { erro -> emit(UiState.Error(erro.message ?: "Erro ao carregar o historico")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    private val _salvarState = MutableStateFlow<SalvarState>(SalvarState.Idle)
    val salvarState: StateFlow<SalvarState> = _salvarState.asStateFlow()

    fun registrarCarga(cargaKgTexto: String, seriesTexto: String, repeticoesTexto: String) {
        viewModelScope.launch {
            _salvarState.value = SalvarState.Loading
            try {
                val cargaKg = cargaKgTexto.replace(',', '.').toDoubleOrNull()
                    ?: throw IllegalArgumentException("Informe uma carga válida")
                val series = seriesTexto.toIntOrNull()
                    ?: throw IllegalArgumentException("Informe um número de séries válido")
                val repeticoes = repeticoesTexto.toIntOrNull()
                    ?: throw IllegalArgumentException("Informe um número de repetições válido")

                cargaHistoricoRepository.registrarCarga(exercicioId, cargaKg, series, repeticoes)
                _salvarState.value = SalvarState.Success
            } catch (erro: Exception) {
                _salvarState.value = SalvarState.Error(erro.message ?: "Erro ao salvar o registro")
            }
        }
    }

    fun resetSalvarState() {
        _salvarState.value = SalvarState.Idle
    }
}
