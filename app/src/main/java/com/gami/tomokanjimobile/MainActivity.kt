package com.gami.tomokanjimobile

import KanjiViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.ui.composables.navigation.Nav
import com.gami.tomokanjimobile.ui.composables.kanjis.KanjiScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gami.tomokanjimobile.data.Kanji
import com.gami.tomokanjimobile.data.Word
import com.gami.tomokanjimobile.ui.composables.home.HomeScreen
import com.gami.tomokanjimobile.ui.composables.kanjis.KanjiDetail
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
            val navController = rememberNavController()

            CustomTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) { // Ensures that the NavHost takes up only the required space
                        NavHost(navController = navController, startDestination = "home") {
                            composable("home") { HomeScreen(navController, applicationContext) }
                            composable("kanji") { KanjiScreen(kanjiViewModel, navController, applicationContext) }
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
                            composable("word") { WordScreen(wordViewModel, navController, applicationContext) }
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
                    }

                    val currentRoute = findCurrentRoute(navController)
                    Nav(navController, currentRoute)
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