package com.example.dicefight

import AboutDialog
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun MainScreen(navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isPortrait) {
            // PORTRAIT MODE UI (Default)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainScreenContent(navController)
            }
        } else {
            // LANDSCAPE MODE UI
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                MainScreenContent(navController)
            }
        }
    }
}

@Composable
fun MainScreenContent(navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    // ✅ Fix: Ensure `showDialog` is properly declared
    var showDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = if (isPortrait) 170.dp else 20.dp, // More top padding in portrait mode
                start = 16.dp,
                end = 16.dp
            )
    ) {
        // Dynamic Image Size Based on Orientation
        val imageHeight = if (isPortrait) 300.dp else 200.dp  // Smaller image in landscape

        Image(
            painter = painterResource(id = R.drawable.dice_game_banner),
            contentDescription = "Dice Game Banner",
            contentScale = if (isPortrait) ContentScale.FillWidth else ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(if (isPortrait) 20.dp else 30.dp))  // Adjust spacing

        // Title
        Text(
            text = "Dice Fight",
            fontSize = if (isPortrait) 30.sp else 24.sp,  // Smaller text in landscape
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(if (isPortrait) 20.dp else 10.dp))

        // Dynamic Button Size
        val buttonWidth = if (isPortrait) 0.6f else 0.4f  // Smaller buttons in landscape
        val buttonHeight = if (isPortrait) 50.dp else 40.dp

        // New Game Button
        Button(
            onClick = { navController.navigate("gameScreen") },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF664FA3), // Custom purple background
            ),
            modifier = Modifier
                .fillMaxWidth(buttonWidth)
                .height(buttonHeight)
        ) {
            Text(text = "New Game", fontSize = if (isPortrait) 18.sp else 14.sp)
        }

        Spacer(modifier = Modifier.height(if (isPortrait) 15.dp else 8.dp))


        // About Button
        Button(
            onClick = { showDialog = true }, // Opens the popup
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(Color.Gray),
            modifier = Modifier
                .fillMaxWidth(buttonWidth)
                .height(buttonHeight)
        ) {
            Text(text = "About Me", fontSize = if (isPortrait) 18.sp else 14.sp)
        }



        // ✅ Show About Dialog when 'showDialog' is true
        if (showDialog) {
            AboutDialog(onDismiss = { showDialog = false })
        }
    }
}

