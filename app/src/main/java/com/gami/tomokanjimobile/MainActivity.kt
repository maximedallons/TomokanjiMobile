package com.gami.tomokanjimobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.ui.composables.kanjis.KanjiScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.data.Word
import com.gami.tomokanjimobile.ui.composables.LoginScreen
import com.gami.tomokanjimobile.ui.composables.home.HomeScreen
import com.gami.tomokanjimobile.ui.composables.kanjis.KanjiDetail
import com.gami.tomokanjimobile.ui.composables.kanjis.KanjiViewModel
import com.gami.tomokanjimobile.ui.composables.navigation.CircleButton
import com.gami.tomokanjimobile.ui.composables.navigation.PillNavigationBar
import com.gami.tomokanjimobile.ui.composables.words.WordDetail
import com.gami.tomokanjimobile.ui.composables.words.WordScreen
import com.gami.tomokanjimobile.ui.composables.words.WordViewModel
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kanjiViewModel: KanjiViewModel by viewModels()
        val wordViewModel: WordViewModel by viewModels()

        enableEdgeToEdge()
        setContent {
            MainScreen(kanjiViewModel, wordViewModel)
        }
    }

    @Composable
    fun MainScreen(kanjiViewModel: KanjiViewModel, wordViewModel: WordViewModel) {
        val navController = rememberNavController()
        val circleButtonsState = remember { mutableStateOf<List<CircleButton>>(emptyList()) }

        CustomTheme {
            Box(modifier = Modifier.fillMaxSize()) { // Ensures that the NavHost takes up only the required space
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("home") { HomeScreen(navController, LocalContext.current) }
                    composable("kanji") { KanjiScreen(kanjiViewModel, navController, circleButtonsState, LocalContext.current) }
                    composable(
                        route = "kanji_detail/{kanjiJson}/{mastered}",
                        arguments = listOf(
                            navArgument("kanjiJson") { type = NavType.StringType },
                            navArgument("mastered") { type = NavType.BoolType }
                        )
                    ) { backStackEntry ->
                        val kanjiJson = backStackEntry.arguments?.getString("kanjiJson")
                        val mastered = backStackEntry.arguments?.getBoolean("mastered") ?: false

                        if (!kanjiJson.isNullOrEmpty()) {
                            val kanji = Json.decodeFromString<Kanji>(kanjiJson)
                            KanjiDetail(kanji, mastered, navController, kanjiViewModel)
                        }
                    }
                    composable("word") { WordScreen(wordViewModel, navController, circleButtonsState, LocalContext.current) }
                    composable(
                        route = "word_detail/{wordJson}/{mastered}",
                        arguments = listOf(
                            navArgument("wordJson") { type = NavType.StringType },
                            navArgument("mastered") { type = NavType.BoolType }
                        )
                    ) { backStackEntry ->
                        val wordJson = backStackEntry.arguments?.getString("wordJson")
                        val mastered = backStackEntry.arguments?.getBoolean("mastered") ?: false

                        if (!wordJson.isNullOrEmpty()) {
                            val word = Json.decodeFromString<Word>(wordJson)
                            WordDetail(word, mastered, navController, wordViewModel)
                        }
                    }
                }

                val currentRoute = findCurrentRoute(navController)
                if (currentRoute != "login") {
                    PillNavigationBar(
                        navController = navController,
                        currentRoute = currentRoute,
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                        circleButtonsState = circleButtonsState
                    )
                }
            }
        }
    }

    @Composable
    private fun findCurrentRoute(navController: NavController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
    }
}