package com.example.auris // Ensure this matches your package

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*

import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.LibraryBooks

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.outlined.Add

// --- 1. DESIGN SYSTEM (COLORS & FONTS) ---
val AurisBeige = Color(0xFFF9F8F6)
val AurisSurface = Color(0xFFEBEAE6)
val AurisCapsule = Color(0xFF1C1C1C)

class MainActivity : ComponentActivity() {
    private lateinit var ttsManager: TtsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ttsManager = TtsManager(this)

        setContent {
            MaterialTheme(
                // We will implement custom Serif fonts later, using default Serif for now
                typography = Typography(
                    headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
                    bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Serif)
                )
            ) {
                AurisAppNavigation(ttsManager)
            }
        }
    }
}

// --- 2. THE MASTER ROUTER & SCAFFOLD ---
@Composable
fun AurisAppNavigation(ttsManager: TtsManager) {
    val navController = rememberNavController()
    // Observe the current route to know which capsule to show
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    // Initialize the engine in the background when the app starts
    LaunchedEffect(Unit) { ttsManager.initEngine() }

    Scaffold(
        containerColor = AurisBeige,
        bottomBar = {
            // The magic "Swapping" animation for the capsule
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
        // The Navigation Graph
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

// --- 3. THE FLOATING CAPSULES ---

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
                modifier = Modifier.height(56.dp) // Larger height
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

@Composable
fun CapsuleIcon(icon: ImageVector, description: String, isSelected: Boolean, onClick: () -> Unit) {
    val iconColor = if (isSelected) Color.White else Color.Gray
    IconButton(onClick = onClick, modifier = Modifier.size(28.dp)) {
        Icon(imageVector = icon, contentDescription = description, tint = iconColor)
    }
}

// --- 4. THE SCREENS ---

@Composable
fun HomeScreen(navController: NavController) {
    // Grab our ViewModel
    val viewModel: HomeViewModel = viewModel()

    // Collect the database list as Compose State
    val bookList by viewModel.books.collectAsState()

    // The Native Android File Picker Launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.handlePdfSelection(it) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        // Top Bar Area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Good afternoon, Benak.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Continue Reading", style = MaterialTheme.typography.titleMedium, color = Color.Gray)

            // The ADD BOOK Button
            Surface(
                shape = CircleShape,
                color = AurisCapsule,
                modifier = Modifier.clickable {
                    // Launch picker, looking only for PDFs
                    pdfPickerLauncher.launch(arrayOf("application/pdf"))
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(18.dp))
                    Text("Add Book", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Library List
        if (bookList.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("Your library is empty. Add a PDF to begin.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                // Add bottom padding so the floating capsule doesn't block the last book
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(bookList) { book ->
                    // The Book Card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clickable {
                                // TODO: Pass the selected book URI to the Reader screen
                                navController.navigate("reader")
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = AurisSurface
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(book.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, maxLines = 1)
                            Spacer(modifier = Modifier.height(16.dp))
                            // Placeholder progress bar
                            LinearProgressIndicator(progress = { 0.0f }, modifier = Modifier.fillMaxWidth(), color = AurisCapsule)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Explore Open Source Directory (Coming Soon)", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun CollectionScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("My Book Collection (Coming Soon)", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ReaderScreen(ttsManager: TtsManager) {
    val sampleText = "This is the reader view. Tap anywhere to play or pause the audio. Long press on a sentence to snap the reader to this exact location. The capsule at the bottom handles your display and audio settings."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // TODO: Toggle TTS Play/Pause
                        println("Tapped: Play/Pause Audio")
                    },
                    onLongPress = {
                        // TODO: Snap audio to specific sentence
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