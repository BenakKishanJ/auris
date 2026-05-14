package com.example.auris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.auris.ui.components.GlobalCapsule
import com.example.auris.ui.components.ReaderCapsule
import com.example.auris.ui.screens.*

class MainActivity : ComponentActivity() {
    private lateinit var ttsManager: TtsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ttsManager = TtsManager(this)

        setContent {
            val aurisTypography = Typography(
                headlineMedium = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                titleMedium = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                ),
                bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Serif,
                    color = Color.DarkGray
                )
            )
            MaterialTheme(typography = aurisTypography) {
                LaunchedEffect(Unit) { ttsManager.initEngine() }
                AurisAppNavigation(ttsManager)
            }
        }
    }
}

@Composable
fun AurisAppNavigation(ttsManager: TtsManager) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            AnimatedContent(
                targetState = currentRoute == "reader",
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) + slideInVertically { it / 2 }) togetherWith
                    (fadeOut(animationSpec = tween(300)) + slideOutVertically { it / 2 })
                }, label = "capsule_swap"
            ) { isReaderContext ->
                Box(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isReaderContext) {
                        ReaderCapsule()
                    } else {
                        GlobalCapsule(navController, currentRoute)
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("explore") { ExploreScreen() }
            composable("collection") { CollectionScreen() }
            composable("reader") { ReaderScreen(ttsManager) }
        }
    }
}
