package com.gami.tomokanjimobile.ui.composables.home

import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController, context: Context) {
    // Your existing code...
    // Navigate to the kanji screen
    Button(onClick = { navController.navigate("kanji") }) {
        Text("Go to Kanji")
    }
}