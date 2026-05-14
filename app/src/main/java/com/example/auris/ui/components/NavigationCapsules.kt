package com.example.auris.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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

// The dark color used for the capsule backgrounds
val AurisCapsule = Color(0xFF1C1C1C)

// --- 1. GLOBAL NAV CAPSULE (Home, Explore, Collection) ---

@Composable
fun GlobalCapsule(navController: NavController, currentRoute: String) {
    Row(
        modifier = Modifier
            .height(64.dp) // Matched to feel as substantial as the Reader navbar
            .clip(CircleShape)
            .background(AurisCapsule)
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
    // State to handle the toggle switch visually
    var isListeningMode by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Inner row for the items with spacing between the individual pills
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 1. Time / Progress Indicator Pill
            Surface(
                shape = CircleShape,
                color = AurisCapsule,
                modifier = Modifier.height(56.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Time",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "10:24",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }

            // 2. The Read / Listen Toggle Pill
            Surface(
                shape = CircleShape,
                color = AurisCapsule,
                modifier = Modifier.height(56.dp)
            ) {
                Row(
                    // Inner padding to give the white selection bubble room to float
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Read Segment
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (!isListeningMode) Color.White else Color.Transparent)
                            .clickable { isListeningMode = false }
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Read",
                            color = if (!isListeningMode) Color.Black else Color.LightGray,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }

                    // Listen Segment
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isListeningMode) Color.White else Color.Transparent)
                            .clickable { isListeningMode = true }
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Listen",
                            color = if (isListeningMode) Color.Black else Color.LightGray,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // 3. Brightness Circle Button
            Surface(
                shape = CircleShape,
                color = AurisCapsule,
                modifier = Modifier.size(56.dp)
            ) {
                IconButton(onClick = { /* TODO: Open Brightness Slider */ }) {
                    Icon(
                        imageVector = Icons.Outlined.LightMode,
                        contentDescription = "Brightness",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // 4. Settings/Formatting Circle Button
            Surface(
                shape = CircleShape,
                color = AurisCapsule,
                modifier = Modifier.size(56.dp)
            ) {
                IconButton(onClick = { /* TODO: Open Display Settings */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}