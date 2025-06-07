package com.panopoker.ui.mesa

import androidx.compose.runtime.*
import com.panopoker.model.CartaGlowInfo
import com.panopoker.model.CartasComunitarias
import com.panopoker.model.Jogador
import com.panopoker.model.MesaDto
import com.panopoker.model.PerfilResponse
import com.panopoker.model.ShowdownDto
import kotlinx.coroutines.Job

data class MesaState(
    val faseDaRodada: MutableState<String?>,
    val jogadores: MutableState<List<Jogador>>,
    val cartas: MutableState<CartasComunitarias?>,
    val minhasCartas: MutableState<List<String>>,
    val maoFormada: MutableState<String>,
    val jogadorDaVezId: MutableState<Int?>,
    val stackJogador: MutableFloatState,
    val raiseValue: MutableFloatState,
    val mostrarSlider: MutableState<Boolean>,
    val showdownInfo: MutableState<ShowdownDto?>,
    val mesa: MutableState<MesaDto?>,
    val cartasComunitarias: MutableState<List<String>>,
    val estadoRodada: MutableState<String>,
    val mostrarDialog: MutableState<Boolean>,
    val perfilSelecionado: MutableState<PerfilResponse?>,
    val cartasGlowComunitarias: MutableState<List<CartaGlowInfo>>,
    val cartasGlowDoJogador: MutableState<Map<Int, List<CartaGlowInfo>>>,
    val lastRodadaId: MutableState<Int>,
    val progressoTimer: MutableState<Float>,
    val timerJob: MutableState<Job?>,
    val lastJogadorDaVezId: MutableState<Int?>,
    val showSemFichasDialog: MutableState<Boolean>,
    val showVencedores: MutableState<Boolean>,
    val showdownInfoPersistente: MutableState<ShowdownDto?>,
    val timestampRecebidoLocalmente: MutableLongState,
    val mostrarDialogManutencao: MutableState<Boolean>
)

@Composable
fun rememberMesaState(): MesaState {
    return MesaState(
        faseDaRodada = remember { mutableStateOf(null) },
        jogadores = remember { mutableStateOf(emptyList()) },
        cartas = remember { mutableStateOf(null) },
        minhasCartas = remember { mutableStateOf(emptyList()) },
        maoFormada = remember { mutableStateOf("") },
        jogadorDaVezId = remember { mutableStateOf(null) },
        stackJogador = remember { mutableFloatStateOf(1f) },
        raiseValue = remember { mutableFloatStateOf(0f) },
        mostrarSlider = remember { mutableStateOf(false) },
        showdownInfo = remember { mutableStateOf(null) },
        mesa = remember { mutableStateOf(null) },
        cartasComunitarias = remember { mutableStateOf(emptyList()) },
        estadoRodada = remember { mutableStateOf("") },
        mostrarDialog = remember { mutableStateOf(false) },
        perfilSelecionado = remember { mutableStateOf(null) },
        cartasGlowComunitarias = remember { mutableStateOf(emptyList()) },
        cartasGlowDoJogador = remember { mutableStateOf(emptyMap()) },
        lastRodadaId = remember { mutableStateOf(-1) },
        progressoTimer = remember { mutableStateOf(1f) },
        timerJob = remember { mutableStateOf(null) },
        lastJogadorDaVezId = remember { mutableStateOf(null) },
        showSemFichasDialog = remember { mutableStateOf(false) },
        showVencedores = remember { mutableStateOf(false) },
        showdownInfoPersistente = remember { mutableStateOf(null) },
        timestampRecebidoLocalmente = remember { mutableLongStateOf(0L) },
        mostrarDialogManutencao = remember { mutableStateOf(false) }
    )
}
