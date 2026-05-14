package com.example.auris.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auris.TtsManager
import com.example.auris.ui.theme.AurisBeige

@Composable
fun ReaderScreen(ttsManager: TtsManager) {
    val sampleText = "This is the reader view. Tap anywhere to play or pause the audio. Long press on a sentence to snap the reader to this exact location. The capsule at the bottom handles your display and audio settings."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AurisBeige)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        println("Tapped: Play/Pause Audio")
                    },
                    onLongPress = {
                        println("Long Pressed: Snap to specific word")
                    }
                )
            }
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = sampleText,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 22.sp,
            lineHeight = 36.sp,
            textAlign = TextAlign.Start
        )
    }
}
