package com.example.auris.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.auris.ui.theme.AurisBeige

@Composable
fun ExploreScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(AurisBeige),
        contentAlignment = Alignment.Center
    ) {
        Text("Explore Open Source Directory (Coming Soon)", style = MaterialTheme.typography.bodyLarge)
    }
}
