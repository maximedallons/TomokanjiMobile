package com.gami.tomokanjimobile.ui.composables.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ScrollToTopFAB(coroutineScope: CoroutineScope, listState: LazyGridState, modifier: Modifier) {
    FloatingActionButton(
        onClick = {
            coroutineScope.launch {
                listState.animateScrollToItem(0)
            }
        },
        modifier,
        containerColor = CustomTheme.colors.secondary,
        contentColor = CustomTheme.colors.textPrimary
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.arrow_up),
            tint = CustomTheme.colors.textPrimary,
            contentDescription = "Scroll to top"
        )
    }
}