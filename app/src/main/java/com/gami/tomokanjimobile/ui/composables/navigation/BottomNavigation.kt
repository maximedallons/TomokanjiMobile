package com.gami.tomokanjimobile.ui.composables.navigation
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme
import java.util.*

data class CircleButton(
    val label: String,
    val onClick: () -> Unit,
    val background : Color
)

@Composable
fun PillNavigationBar(
    bottomNavigationViewModel: BottomNavigationViewModel,
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier
) {
    var isClickableGlobal by remember { mutableStateOf(true) }
    val screens = listOf("home", "kanji", "word")
    val pillColor = CustomTheme.colors.backgroundSecondary
    val pillHeight = 50.dp
    val pillHorizontalPadding = 24.dp
    val additionalCenterButtons by bottomNavigationViewModel.additionalCenterButtons.collectAsState()

    // Handle resetting the `isClickableGlobal` state after a delay
    if (!isClickableGlobal) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(750L) // 1500ms global delay
            isClickableGlobal = true // Re-enable clicks globally
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .pointerInput(Unit) { awaitPointerEventScope { while (true) { awaitPointerEvent() } } }
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        CustomTheme.colors.backgroundPrimary.copy(alpha = 0f),
                        CustomTheme.colors.backgroundPrimary
                    ),
                    startY = 0f,
                    endY = 100f
                ),
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp), // Horizontal spacing between buttons
            verticalAlignment = Alignment.CenterVertically, // Align buttons in the center vertically
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 48.dp)
        ) {
            additionalCenterButtons.forEach { button ->
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(button.background)
                        .size(40.dp) // Size for the button
                        .clickable { button.onClick() }
                ) {
                    Text(
                        text = button.label,
                        color = CustomTheme.colors.textPrimary,
                        modifier = Modifier.align(Alignment.Center) // Center the text inside the button
                    )
                }
            }
        }

        // Pill Container Background
        Box(
            modifier = Modifier
                .padding(16.dp, 0.dp, 16.dp, 30.dp)
                .height(pillHeight)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    color = pillColor,
                    shape = RoundedCornerShape(pillHeight / 2)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = pillHorizontalPadding)
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                screens.forEach { screen ->
                    BottomNavItem(
                        title = screen.capitalize(Locale.ROOT),
                        isSelected = currentRoute?.startsWith(screen) == true,
                        isClickableGlobal = isClickableGlobal, // Pass global state
                        onClick = {
                            if (currentRoute != screen) { // Prevent navigating to the same tab
                                isClickableGlobal = false // Disable clicks globally
                                bottomNavigationViewModel.clearCenterButtons()
                                navController.navigate(screen) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(
    title: String,
    isSelected: Boolean,
    isClickableGlobal: Boolean, // Receive global clickability state
    onClick: () -> Unit
) {
    val textColor = if (isSelected) CustomTheme.colors.textPrimary else CustomTheme.colors.textSecondary
    Text(
        text = title,
        color = textColor,
        modifier = Modifier
            .clickable(
                enabled = !isSelected && isClickableGlobal, // Disable click for selected tab
                indication = null, // Removes the ripple effect
                interactionSource = remember { MutableInteractionSource() } // Prevents undesired behavior
            ) {
                onClick()
            }
            .padding(8.dp),
        textAlign = TextAlign.Center
    )
}