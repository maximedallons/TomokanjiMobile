package com.gami.tomokanjimobile.ui.composables.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gami.tomokanji.ui.theme.CustomTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DividerLinks(dividers : List<Pair<String, Int>>, listState: LazyGridState, currentDividerIndex : Int, coroutineScope: CoroutineScope) {
    return LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(dividers.size) { index ->
            val divider = dividers[index]
            val isActive = index == dividers.indexOfLast { currentDividerIndex >= it.second }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .clickable {
                        coroutineScope.launch {
                            // Scroll to the position of the divider
                            listState.scrollToItem(divider.second)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Conditional styling to change the view based on whether it's active or not
                if (isActive) {
                    // Active Divider with Circle Shape
                    Box(
                        modifier = Modifier
                            .size(30.dp, 18.dp) // Circle size
                            .background(
                                color = CustomTheme.colors.primary,
                                shape = RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = divider.first,
                            fontSize = 12.sp,
                            color = CustomTheme.colors.textPrimary // Text inside the circle
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(30.dp, 18.dp) // Circle size
                            .background(
                                color = CustomTheme.colors.backgroundSecondary,
                                shape = RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = divider.first,
                            fontSize = 12.sp,
                            color = CustomTheme.colors.textPrimary // Text inside the circle
                        )
                    }
                }
            }
        }
    }
}