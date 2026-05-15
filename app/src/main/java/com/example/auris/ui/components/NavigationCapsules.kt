package com.example.auris.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Forward10
import androidx.compose.material.icons.outlined.Replay10
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.ui.draw.shadow


// Color definition
val CapsuleDark = Color(0xFF1A1A1C)
val CapsuleBorder = Color(0xFF2E2E32)


// --- 1. GLOBAL NAV CAPSULE (Home, Explore, Collection) ---

@Composable
fun GlobalCapsule(navController: NavController, currentRoute: String) {
    Row(
        modifier = Modifier
            .height(64.dp) // Matched to feel as substantial as the Reader navbar
            .clip(CircleShape)
            .background(CapsuleDark)
            .padding(horizontal = 6.dp), // Inner padding so the bubbles don't touch the edges
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CapsuleNavItem(
            icon = Icons.Outlined.Home,
            label = "Home",
            isSelected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        CapsuleNavItem(
            icon = Icons.Outlined.Search,
            label = "Explore",
            isSelected = currentRoute == "explore",
            onClick = { navController.navigate("explore") }
        )
        CapsuleNavItem(
            icon = Icons.Outlined.LibraryBooks,
            label = "Collection",
            isSelected = currentRoute == "collection",
            onClick = { navController.navigate("collection") }
        )
    }
}

@Composable
fun CapsuleNavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    // Smoothly animate colors when the user switches tabs
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Transparent,
        label = "nav_bg_color"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else Color.LightGray,
        label = "nav_content_color"
    )

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp), // Large, tactile tap targets
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Spacing between icon and label
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 15.sp
            )
        }
    }
}


// --- 2. READER NAV CAPSULE (Time, Read/Listen Toggle, Controls) ---

@Composable
fun ReaderCapsule() {
    var isPlaying by remember { mutableStateOf(false) }
    var isFocusMode by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 1. Navigation Pill ─────────────────────────────────────────────
            Surface(
                shape = CircleShape,
                color = CapsuleDark,
                border = BorderStroke(0.5.dp, CapsuleBorder),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .height(56.dp)
                    .clickable { /* TODO: Open TOC Sheet */ }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FormatListBulleted,
                        contentDescription = "Table of Contents",
                        tint = Color.White.copy(alpha = 0.40f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .width(0.5.dp)
                            .height(18.dp)
                            .background(Color.White.copy(alpha = 0.12f))
                    )
                    Text(
                        text = "12 / 340",
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }

            // 2. Media Player Pill ───────────────────────────────────────────
            Surface(
                shape = CircleShape,
                color = CapsuleDark,
                border = BorderStroke(0.5.dp, CapsuleBorder),
                shadowElevation = 12.dp,
                modifier = Modifier.height(56.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { /* TODO: Rewind 10s */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Replay10,
                            contentDescription = "Rewind 10s",
                            tint = Color.White.copy(alpha = 0.42f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { isPlaying = !isPlaying },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = CapsuleDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { /* TODO: Forward 10s */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Forward10,
                            contentDescription = "Forward 10s",
                            tint = Color.White.copy(alpha = 0.42f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 3. Focus Mode Pill ─────────────────────────────────────────────
            Surface(
                shape = CircleShape,
                color = if (isFocusMode) Color.White else CapsuleDark,
                border = BorderStroke(0.5.dp, if (isFocusMode) Color.Transparent else CapsuleBorder),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .size(56.dp)
                    .clickable { isFocusMode = !isFocusMode }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.DocumentScanner,
                        contentDescription = "Focus Mode",
                        tint = if (isFocusMode) CapsuleDark else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // 4. Settings Pill ───────────────────────────────────────────────
            Surface(
                shape = CircleShape,
                color = CapsuleDark,
                border = BorderStroke(0.5.dp, CapsuleBorder),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .size(56.dp)
                    .clickable { /* TODO: Open Display Settings */ }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "Display Settings",
                        tint = Color.White.copy(alpha = 0.55f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}