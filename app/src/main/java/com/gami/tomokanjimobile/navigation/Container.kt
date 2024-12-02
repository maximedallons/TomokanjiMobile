package com.gami.tomokanjimobile.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*

@Composable
fun Container(view : @Composable () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
    ) {
        view()
    }
}