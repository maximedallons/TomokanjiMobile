package com.gami.tomokanjimobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
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
import com.gami.tomokanjimobile.ui.composables.kanas.*
import com.gami.tomokanjimobile.ui.composables.kanjis.KanjiDetail
import com.gami.tomokanjimobile.ui.composables.kanjis.KanjiViewModel
import com.gami.tomokanjimobile.ui.composables.kanjis.KanjiViewModelFactory
import com.gami.tomokanjimobile.ui.composables.navigation.BottomNavigationViewModel
import com.gami.tomokanjimobile.ui.composables.navigation.PillNavigationBar
import com.gami.tomokanjimobile.ui.composables.words.WordDetail
import com.gami.tomokanjimobile.ui.composables.words.WordScreen
import com.gami.tomokanjimobile.ui.composables.words.WordViewModel
import com.gami.tomokanjimobile.ui.composables.words.WordViewModelFactory
import com.gami.tomokanjimobile.ui.theme.CustomColorScheme
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedViewModel: SharedViewModel by viewModels()
        val kanjiViewModel: KanjiViewModel by viewModels {
            KanjiViewModelFactory(sharedViewModel)
        }
        val wordViewModel: WordViewModel by viewModels() {
            WordViewModelFactory(sharedViewModel)
        }
        val kanaViewModel: KanaViewModel by viewModels() {
            KanaViewModelFactory(sharedViewModel)
        }
        val bottomNavigationViewModel: BottomNavigationViewModel by viewModels()

        enableEdgeToEdge()
        setContent {
            MainScreen(sharedViewModel, bottomNavigationViewModel, kanaViewModel, kanjiViewModel, wordViewModel)
        }
    }


    @Composable
    fun MainScreen(sharedViewModel: SharedViewModel, bottomNavigationViewModel: BottomNavigationViewModel, kanaViewModel : KanaViewModel, kanjiViewModel: KanjiViewModel, wordViewModel: WordViewModel) {
        val navController = rememberNavController()

        CustomTheme {
            Box(modifier = Modifier.fillMaxSize().background(CustomTheme.colors.backgroundPrimary)) { // Ensures that the NavHost takes up only the required space
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController, sharedViewModel, LocalContext.current, kanaViewModel, kanjiViewModel, wordViewModel) }
                    composable("home") { HomeScreen(sharedViewModel, kanaViewModel, kanjiViewModel, wordViewModel, navController, LocalContext.current) }
                    composable("kana") {
                        KanaScreen(kanaViewModel, bottomNavigationViewModel, navController)
                    }
                    composable(
                        "kana_hiragana_detail/{id}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.IntType },
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id")

                        if(id != null) {
                            HiraganaDetail(id, navController, sharedViewModel, bottomNavigationViewModel, kanaViewModel)
                        }
                    }
                    composable(
                        "kana_katakana_detail/{id}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.IntType },
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id")

                        if(id != null) {
                            KatakanaDetail(id, navController, sharedViewModel, bottomNavigationViewModel, kanaViewModel)
                        }
                    }
                    composable("kanji") {
                        KanjiScreen(kanjiViewModel, bottomNavigationViewModel, navController)
                    }
                    composable(
                        route = "kanji_detail/{id}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.IntType },
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id")

                        if(id != null) {
                            KanjiDetail(id, navController, sharedViewModel, bottomNavigationViewModel, kanjiViewModel)
                        }
                    }
                    composable("word") {
                        WordScreen(wordViewModel, bottomNavigationViewModel, navController)
                    }
                    composable(
                        route = "word_detail/{id}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.IntType },
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id")

                        if (id != null) {
                            WordDetail(id, navController, sharedViewModel, bottomNavigationViewModel, wordViewModel)
                        }
                    }
                }

                val currentRoute = findCurrentRoute(navController)
                if (currentRoute != "login" && currentRoute != "loading") {
                    PillNavigationBar(
                        bottomNavigationViewModel = bottomNavigationViewModel,
                        navController = navController,
                        currentRoute = currentRoute,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
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