package com.gami.tomokanjimobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.navigation.Container
import com.gami.tomokanjimobile.navigation.Nav
import com.gami.tomokanjimobile.ui.composables.kanjis.KanjiScreen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val appContext = applicationContext

            CustomTheme {
                val selectedScreen = remember { mutableStateOf<Screen>(Screen.Home(appContext)) }
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val screens = mapOf(
                        Screen.Home(appContext) to Screen.Home(appContext).content,
                        Screen.Kanji(appContext) to Screen.Kanji(appContext).content,
                        Screen.Words(appContext) to Screen.Words(appContext).content
                    )
                    screens[selectedScreen.value]?.let { content ->
                        Container(content, Modifier.weight(1f).fillMaxSize())
                    }
                    Nav(selectedScreen, appContext)
                }
            }
        }
    }

    sealed class Screen(val content: @Composable () -> Unit, val name: String) {
        data class Home(val context: android.content.Context) : Screen({ KanjiScreen(context = context) }, "Home")
        data class Kanji(val context: android.content.Context) : Screen({ KanjiScreen(context = context) }, "Kanji")
        data class Words(val context: android.content.Context) : Screen({ KanjiScreen(context = context) }, "Words")
    }
}