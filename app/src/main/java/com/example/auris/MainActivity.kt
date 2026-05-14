package com.example.auris
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var ttsManager: TtsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ttsManager = TtsManager(this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TtsTesterScreen(ttsManager)
                }
            }
        }
    }
}

// Data class to map the Sherpa-ONNX integer IDs to human-readable names
data class VoiceOption(val id: Int, val name: String)

val availableVoices = listOf(
    VoiceOption(0, "Default (Am. Female)"),
    VoiceOption(1, "Bella (Am. Female)"),
    VoiceOption(2, "Nicole (Am. Female)"),
    VoiceOption(3, "Sarah (Am. Female)"),
    VoiceOption(4, "Sky (Am. Female)"),
    VoiceOption(5, "Adam (Am. Male)"),
    VoiceOption(6, "Michael (Am. Male)"),
    VoiceOption(7, "Emma (Brit. Female)"),
    VoiceOption(8, "Isabella (Brit. Female)"),
    VoiceOption(9, "George (Brit. Male)"),
    VoiceOption(10, "Lewis (Brit. Male)")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TtsTesterScreen(ttsManager: TtsManager) {
    val coroutineScope = rememberCoroutineScope()

    // UI States
    var textToRead by remember { mutableStateOf("Welcome to Auris. The offline Kokoro engine is successfully running.") }
    var isEngineReady by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }
    var isAudioReady by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    // Dropdown States
    var expanded by remember { mutableStateOf(false) }
    var selectedVoice by remember { mutableStateOf(availableVoices[0]) }

    LaunchedEffect(Unit) {
        ttsManager.initEngine()
        isEngineReady = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = textToRead,
            onValueChange = {
                textToRead = it
                isAudioReady = false
            },
            label = { Text("Text to Read") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

        // DROPDOWN MENU
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedVoice.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Voice") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableVoices.forEach { voice ->
                    DropdownMenuItem(
                        text = { Text(voice.name) },
                        onClick = {
                            selectedVoice = voice
                            expanded = false
                            isAudioReady = false // Reset audio if they change the voice
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // GENERATE BUTTON
        Button(
            onClick = {
                coroutineScope.launch {
                    isGenerating = true
                    isAudioReady = false
                    // Pass the newly selected voice ID here
                    ttsManager.generateAudio(textToRead, voiceId = selectedVoice.id)
                    isGenerating = false
                    isAudioReady = true
                }
            },
            enabled = isEngineReady && !isGenerating,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(if (!isEngineReady) "Loading AI Engine..." else if (isGenerating) "Synthesizing Audio..." else "Generate Audio")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PLAY BUTTON
        Button(
            onClick = {
                coroutineScope.launch {
                    isPlaying = true
                    ttsManager.playAudio()
                    isPlaying = false
                }
            },
            enabled = isAudioReady && !isPlaying,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(if (isPlaying) "Playing..." else "Play Audio")
        }
    }
}