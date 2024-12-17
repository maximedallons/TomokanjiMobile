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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gami.tomokanji.ui.theme.CustomTheme

@Composable
fun BottomNavItem(title: String, isSelected: Boolean, onClick: () -> Unit, circleButtonsState : MutableState<List<CircleButton>>) {
    val textColor = if (isSelected) CustomTheme.colors.textPrimary else CustomTheme.colors.textSecondary
    Text(
        text = title,
        color = textColor,
        modifier = Modifier
            .clickable(
                indication = null, // Removes the ripple effect
                interactionSource = remember { MutableInteractionSource() } // Prevents any undesired behavior
            ) {
                circleButtonsState.value = emptyList()
                onClick()
            }
            .padding(8.dp),
        textAlign = TextAlign.Center
    )
}

// Data class for CircleButtons
data class CircleButton(
    val label: String,
    val onClick: () -> Unit,
    val background : Color
)

@Composable
fun PillNavigationBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier,
    circleButtonsState: MutableState<List<CircleButton>> // Changed to a MutableState for dynamic control
) {
    val screens = listOf("home", "kanji", "word") // Navigation items
    val pillColor = CustomTheme.colors.backgroundSecondary
    val pillHeight = 50.dp // Height of the pill shape
    val pillHorizontalPadding = 24.dp // Padding inside the pill container

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        CustomTheme.colors.backgroundPrimary.copy(alpha = 0f), // Fully transparent at the top
                        CustomTheme.colors.backgroundPrimary // Solid at the bottom
                    ),
                    startY = 0f,
                    endY = 100f
                ),
            )
    ) {
        // Circle Buttons Container
        val circleButtons = circleButtonsState.value // Observe state changes
        if (circleButtons.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = pillHeight) // Place above the pill
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                circleButtons.forEach { button ->
                    Box(
                        modifier = Modifier
                            .size(40.dp) // Size of the circle button
                            .offset { IntOffset(0, -40) } // Offset to place the buttons above the pill
                            .clip(CircleShape)
                            .background(button.background)
                            .clickable { button.onClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = button.label,
                            color = CustomTheme.colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp)) // Space between buttons
                }
            }
        }

        // Pill Container Background
        Box(
            modifier = Modifier
                .padding(16.dp, 30.dp)
                .height(pillHeight)
                .fillMaxWidth()
                .align(Alignment.Center)
                .background(
                    color = pillColor,
                    shape = RoundedCornerShape(pillHeight / 2) // Create capsule shape
                )
        ) {
            // Navigation Items inside the pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = pillHorizontalPadding) // Space from left and right edges
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                screens.forEach { screen ->
                    BottomNavItem(
                        title = screen.capitalize(),
                        isSelected = currentRoute?.startsWith(screen) == true,
                        onClick = {
                            navController.navigate(screen) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        circleButtonsState
                    )
                }
            }
        }
    }
}