package com.example.auris.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.auris.HomeViewModel
import com.example.auris.ui.components.BookCard

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()
    val bookList by viewModel.books.collectAsState()

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.handlePdfSelection(it) }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF181818))) {
        // --- TOP SECTION (Black Header) ---
        Column(modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp, end = 24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End, // Aligns the profile icon to the right
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle, 
                    contentDescription = "Profile", 
                    tint = Color.White, 
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Hey, Benak!", color = Color.LightGray, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Larger Welcome Text
            Text(
                text = "Discover\nYour Library",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp, // Bumped up size
                lineHeight = 44.sp // Adjusted line height for the larger font
            )
        }

        // --- BOTTOM SECTION (White Rounded Sheet) ---
        Surface(
            modifier = Modifier.fillMaxSize().padding(top = 280.dp), // Pushed down to 280dp for more space
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(top = 32.dp, start = 24.dp, end = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Continue Reading", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
                    Surface(
                        shape = CircleShape, color = Color(0xFFF0F0F0),
                        modifier = Modifier.clickable { pdfPickerLauncher.launch(arrayOf("application/pdf")) }
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Outlined.Add, contentDescription = "Add", tint = Color.Black, modifier = Modifier.size(16.dp))
                            Text("Add Book", color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                if (bookList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Your library is empty. Add a PDF to begin.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 120.dp)) {
                        items(bookList) { book ->
                            BookCard(book, navController)
                        }
                    }
                }
            }
        }
    }
}
