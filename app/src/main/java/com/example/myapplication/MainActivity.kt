package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlin.random.Random

// MainActivity é a atividade principal que configura o conteúdo da tela
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Passa a função finish() para TelaDoJogo
                TelaDoJogo(onFinish = { finish() })
            }
        }
    }
}

// TelaDoJogo é o composable que define a interface do jogo
@Composable
fun TelaDoJogo(onFinish: () -> Unit) {
    // Estado do número de cliques e do alvo do jogo
    var contagemCliques by rememberSaveable { mutableStateOf(0) }
    var cliquesAlvo by rememberSaveable { mutableStateOf(Random.nextInt(1, 51)) }
    var estadoDoJogo by rememberSaveable { mutableStateOf("jogando") }
    var idImagem by rememberSaveable { mutableStateOf(R.drawable.image_initial) }
    var mostrarConfirmacao by rememberSaveable { mutableStateOf(false) }

    // Função para atualizar o estado do jogo com base no número de cliques
    fun atualizarJogo() {
        val progresso = contagemCliques.toFloat() / cliquesAlvo
        if (progresso >= 1f) {
            // Se o progresso atingir 100%, o jogo é vencido
            estadoDoJogo = "vitória"
            idImagem = R.drawable.image_victory
            mostrarConfirmacao = true
        } else {
            // Atualiza a imagem com base no progresso
            idImagem = when {
                progresso >= 0.66f -> R.drawable.image_final
                progresso >= 0.33f -> R.drawable.image_median
                else -> R.drawable.image_initial
            }
        }
    }

    // Função para reiniciar o jogo
    fun reiniciarJogo() {
        contagemCliques = 0
        cliquesAlvo = Random.nextInt(1, 51)
        estadoDoJogo = "jogando"
        idImagem = R.drawable.image_initial
        mostrarConfirmacao = false
    }

    // Função para encerrar o jogo e mostrar a imagem de derrota
    fun encerrarJogo() {
        estadoDoJogo = "desistiu"
        idImagem = R.drawable.image_defeat
        mostrarConfirmacao = false
    }

    // Layout principal da tela do jogo
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Exibe a imagem centralizada e com tamanho ajustável
        Image(
            painter = painterResource(id = idImagem),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Exibe o conteúdo baseado no estado atual do jogo
        when (estadoDoJogo) {
            "jogando" -> {
                // Exibe os botões quando o jogo está em andamento
                Button(onClick = {
                    contagemCliques++
                    atualizarJogo()
                }) {
                    Text("Clique aqui!")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    encerrarJogo()
                }) {
                    Text("Desistir")
                }
            }
            "vitória" -> {
                // Mostra a confirmação após vencer
                if (mostrarConfirmacao) {
                    ConfirmacaoJogo(
                        mensagem = "Parabéns, você venceu! Deseja jogar novamente?",
                        onReiniciar = { reiniciarJogo() },
                        onDesistir = { encerrarJogo() },
                        onNao = { onFinish() } // Finaliza a atividade ao clicar em "Não"
                    )
                } else {
                    // Exibe a mensagem de vitória e botões para jogar novamente ou sair
                    Text("Parabéns, você venceu!")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { reiniciarJogo() }) {
                        Text("Jogar Novamente")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { encerrarJogo() }) {
                        Text("Sair")
                    }
                }
            }
            "desistiu" -> {
                // Exibe a mensagem e botões quando o usuário desiste
                Text("Você desistiu!")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { reiniciarJogo() }) {
                    Text("Jogar Novamente")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onFinish() }) { // Finaliza a atividade ao clicar em "Sair"
                    Text("Sair")
                }
            }
        }
    }
}

// ConfirmacaoJogo exibe a confirmação para jogar novamente ou desistir
@Composable
fun ConfirmacaoJogo(mensagem: String, onReiniciar: () -> Unit, onDesistir: () -> Unit, onNao: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mensagem de confirmação
        Text(mensagem)
        Spacer(modifier = Modifier.height(16.dp))
        // Botões para "Sim", "Desistir" e "Não"
        Row {
            Button(onClick = onReiniciar) {
                Text("Sim")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onDesistir) {
                Text("Desistir")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onNao) {
                Text("Não")
            }
        }
    }
}