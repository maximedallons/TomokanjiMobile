package com.gami.tomokanjimobile.ui.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gami.tomokanji.ui.theme.CustomTheme
import com.gami.tomokanjimobile.R
import com.gami.tomokanjimobile.SharedViewModel
import com.gami.tomokanjimobile.dao.HiraganaDatabaseBuilder
import com.gami.tomokanjimobile.dao.KanjiDatabaseBuilder
import com.gami.tomokanjimobile.dao.KatakanaDatabaseBuilder
import com.gami.tomokanjimobile.dao.WordDatabaseBuilder
import com.gami.tomokanjimobile.network.LoginApi
import com.gami.tomokanjimobile.ui.composables.kanas.KanaViewModel
import com.gami.tomokanjimobile.ui.composables.kanjis.KanjiViewModel
import com.gami.tomokanjimobile.ui.composables.words.WordViewModel
import kotlinx.coroutines.*
import kotlin.random.Random

@Composable
fun LoginScreen(navController: NavHostController, sharedViewModel: SharedViewModel, context: Context, kanaViewModel: KanaViewModel, kanjiViewModel: KanjiViewModel, wordViewModel: WordViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val kanjiDao = KanjiDatabaseBuilder.getInstance(context).kanjiDao()
    val wordDao = WordDatabaseBuilder.getInstance(context).wordDao()
    val hiraganaDao = HiraganaDatabaseBuilder.getInstance(context).hiraganaDao()
    val katakanaDao = KatakanaDatabaseBuilder.getInstance(context).katakanaDao()

    LaunchedEffect(Unit) {
        kanjiViewModel.fetchKanjis(kanjiDao)
        wordViewModel.fetchWords(wordDao)
        kanaViewModel.fetchHiraganas(hiraganaDao)
        kanaViewModel.fetchKatakanas(katakanaDao)

        //print currentCookie sharedPref value
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val currentCookie = sharedPreferences.getString("currentCookie", "")
        println("currentCookie: $currentCookie")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.backgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Username TextField
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = CustomTheme.colors.backgroundSecondary,
                    unfocusedTextColor = CustomTheme.colors.textPrimary,
                    unfocusedLabelColor = CustomTheme.colors.textSecondary,
                    unfocusedIndicatorColor = CustomTheme.colors.primary,

                    focusedContainerColor = CustomTheme.colors.backgroundSecondary,
                    focusedTextColor = CustomTheme.colors.textPrimary,
                    focusedLabelColor = CustomTheme.colors.textPrimary,
                    focusedIndicatorColor = CustomTheme.colors.primary,

                    cursorColor = CustomTheme.colors.textPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password TextField
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = CustomTheme.colors.backgroundSecondary,
                    unfocusedTextColor = CustomTheme.colors.textPrimary,
                    unfocusedLabelColor = CustomTheme.colors.textSecondary,
                    unfocusedIndicatorColor = CustomTheme.colors.primary,

                    focusedContainerColor = CustomTheme.colors.backgroundSecondary,
                    focusedTextColor = CustomTheme.colors.textPrimary,
                    focusedLabelColor = CustomTheme.colors.textPrimary,
                    focusedIndicatorColor = CustomTheme.colors.primary,

                    cursorColor = CustomTheme.colors.textPrimary
                ),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        val iconResource = if (passwordVisible) R.drawable.eye_open else R.drawable.eye_closed
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = iconResource),
                            tint = CustomTheme.colors.textPrimary,
                            contentDescription = "Show password"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {
                    keyboardController?.hide()
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        loading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            val success = login(sharedViewModel, context, username, password)
                            withContext(Dispatchers.Main) {
                                loading = false
                                if (success) {
                                    if(sharedViewModel.hiraganas.value.isNotEmpty()) {
                                        kanaViewModel.fetchUserMasteredHiraganas()
                                    }
                                    if(sharedViewModel.katakanas.value.isNotEmpty()) {
                                        kanaViewModel.fetchUserMasteredKatakanas()
                                    }
                                    if(sharedViewModel.kanjis.value.isNotEmpty()) {
                                        kanjiViewModel.fetchUserMasteredKanjis()
                                    }
                                    if(sharedViewModel.words.value.isNotEmpty()) {
                                        wordViewModel.fetchUserMasteredWords()
                                    }
                                    navController.navigate("home") {
                                        navController.graph.startDestinationRoute?.let { route ->
                                            popUpTo(route) {
                                                inclusive = true
                                            }
                                        }
                                        launchSingleTop = true
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Invalid username or password.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Both fields are required!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomTheme.colors.primary // Button background color
                )
            ) {
                Text(
                    text = if (loading) "Loading..." else "Login",
                    color = CustomTheme.colors.textPrimary // Button text color
                )
            }
        }
    }
}

suspend fun login(sharedViewModel: SharedViewModel, context: Context, username: String, password: String): Boolean {
    try {
        val user = LoginApi.service.login(username = username, password = password)
        sharedViewModel.setLoggedUser(user)

        val randomCookie = generateRandomString(32)

        // Save the cookie in SharedPreferences
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("currentCookie", randomCookie).apply()

        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

fun generateRandomString(length: Int): String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..length)
        .map { charset[Random.nextInt(0, charset.length)] }
        .joinToString("")
}