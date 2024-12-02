package com.gami.tomokanjimobile.ui.composables.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme

@Composable
fun HomeScreen(navController: NavController, context: Context) {
    Column(
        Modifier.fillMaxSize()
    ) {
        Row(
            Modifier
                .background(CustomTheme.colors.backgroundPrimary)
                .fillMaxWidth().padding(0.dp, 64.dp, 0.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Dashboard",
                fontSize = 24.sp,
                color = CustomTheme.colors.textPrimary
            )
        }

        // Fill the remaining space
        Box(
            Modifier
                .weight(1f)  // Take remaining vertical space
                .fillMaxWidth()  // Ensure the box fills the available width
                .background(CustomTheme.colors.backgroundPrimary),
            contentAlignment = Alignment.Center,
        ) {
            Button(
                onClick = { navController.navigate("kanji") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomTheme.colors.primary,
                    contentColor = Color.White
                ),
                ) {
                Text("Go to Kanji")
            }
        }
    }
}