package com.example.rastreadorprazo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rastreadorprazo.data.Obrigacao
import com.example.rastreadorprazo.data.StatusObrigacao
import com.example.rastreadorprazo.data.TipoObrigacao
import com.example.rastreadorprazo.ui.ObrigacoesViewModel
import com.example.rastreadorprazo.ui.components.CampoSelecionavel
import com.example.rastreadorprazo.ui.components.TimePickerDialog
import com.example.rastreadorprazo.util.formatarData
import com.example.rastreadorprazo.util.formatarHora
import com.example.rastreadorprazo.util.formatarValorParaEdicao
import com.example.rastreadorprazo.util.parseValorBr
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaObrigacaoScreen(
    navController: NavController,
    viewModel: ObrigacoesViewModel,
    obrigacaoId: Long?
) {
    val emEdicao = obrigacaoId != null
    val obrigacaoExistente by if (obrigacaoId != null) {
        viewModel.obterPorId(obrigacaoId).collectAsState(initial = null)
    } else {
        remember { mutableStateOf<Obrigacao?>(null) }
    }

    var titulo by rememberSaveable { mutableStateOf("") }
    var tipo by rememberSaveable { mutableStateOf(TipoObrigacao.PRAZO) }
    var valorTexto by rememberSaveable { mutableStateOf("") }
    var favorecido by rememberSaveable { mutableStateOf("") }
    var dataVencimento by remember { mutableStateOf<LocalDate?>(null) }
    var horaLembrete by remember { mutableStateOf<LocalTime?>(LocalTime.of(9, 0)) }
    var jaPreenchido by rememberSaveable { mutableStateOf(false) }

    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val escopo = rememberCoroutineScope()

    LaunchedEffect(obrigacaoExistente) {
        val obrigacao = obrigacaoExistente
        if (obrigacao != null && !jaPreenchido) {
            titulo = obrigacao.titulo
            tipo = obrigacao.tipo
            valorTexto = formatarValorParaEdicao(obrigacao.valor)
            favorecido = obrigacao.favorecido
            dataVencimento = obrigacao.dataVencimento
            horaLembrete = obrigacao.horaLembrete
            jaPreenchido = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (emEdicao) "Editar Obrigação" else "Nova Obrigação") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Tipo", style = MaterialTheme.typography.labelLarge)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = tipo == TipoObrigacao.PRAZO,
                        onClick = { tipo = TipoObrigacao.PRAZO },
                        shape = SegmentedButtonDefaults.itemShape(0, 2)
                    ) { Text("Prazo") }
                    SegmentedButton(
                        selected = tipo == TipoObrigacao.FATURA,
                        onClick = { tipo = TipoObrigacao.FATURA },
                        shape = SegmentedButtonDefaults.itemShape(1, 2)
                    ) { Text("Fatura") }
                }
            }

            OutlinedTextField(
                value = valorTexto,
                onValueChange = { novo -> if (novo.all { it.isDigit() || it == ',' }) valorTexto = novo },
                label = { Text("Valor (R$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = favorecido,
                onValueChange = { favorecido = it },
                label = { Text("Favorecido") },
                modifier = Modifier.fillMaxWidth()
            )

            CampoSelecionavel(
                valor = dataVencimento?.let { formatarData(it) } ?: "",
                rotulo = "Data de Vencimento",
                icone = Icons.Filled.DateRange,
                onClick = { mostrarDatePicker = true }
            )

            CampoSelecionavel(
                valor = horaLembrete?.let { formatarHora(it) } ?: "",
                rotulo = "Horário do Lembrete",
                icone = Icons.Filled.AccessTime,
                onClick = { mostrarTimePicker = true }
            )

            Button(
                onClick = {
                    val valor = parseValorBr(valorTexto)
                    when {
                        titulo.isBlank() -> mostrarErro(escopo, snackbarHostState, "Informe o título")
                        favorecido.isBlank() -> mostrarErro(escopo, snackbarHostState, "Informe o favorecido")
                        valor == null || valor <= 0.0 -> mostrarErro(escopo, snackbarHostState, "Informe um valor válido")
                        dataVencimento == null -> mostrarErro(escopo, snackbarHostState, "Selecione a data de vencimento")
                        horaLembrete == null -> mostrarErro(escopo, snackbarHostState, "Selecione o horário do lembrete")
                        else -> {
                            val obrigacao = Obrigacao(
                                id = obrigacaoId ?: 0,
                                titulo = titulo.trim(),
                                tipo = tipo,
                                valor = valor,
                                favorecido = favorecido.trim(),
                                dataVencimento = dataVencimento!!,
                                horaLembrete = horaLembrete!!,
                                lembreteAtivo = true,
                                status = obrigacaoExistente?.status ?: StatusObrigacao.PENDENTE,
                                dataCriacao = obrigacaoExistente?.dataCriacao ?: LocalDateTime.now()
                            )
                            viewModel.salvar(obrigacao)
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar")
            }
        }
    }

    if (mostrarDatePicker) {
        val estadoData = rememberDatePickerState(
            initialSelectedDateMillis = (dataVencimento ?: LocalDate.now())
                .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoData.selectedDateMillis?.let { millis ->
                        dataVencimento = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    mostrarDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoData)
        }
    }

    if (mostrarTimePicker) {
        val horaBase = horaLembrete ?: LocalTime.of(9, 0)
        val estadoHora = rememberTimePickerState(
            initialHour = horaBase.hour,
            initialMinute = horaBase.minute,
            is24Hour = true
        )
        TimePickerDialog(
            onDismissRequest = { mostrarTimePicker = false },
            onConfirm = {
                horaLembrete = LocalTime.of(estadoHora.hour, estadoHora.minute)
                mostrarTimePicker = false
            }
        ) {
            TimePicker(state = estadoHora)
        }
    }
}

private fun mostrarErro(
    escopo: kotlinx.coroutines.CoroutineScope,
    snackbarHostState: SnackbarHostState,
    mensagem: String
) {
    escopo.launch { snackbarHostState.showSnackbar(mensagem) }
}
