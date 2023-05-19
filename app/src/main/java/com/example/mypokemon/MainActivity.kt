package com.example.mypokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mypokemon.constants.Constants.DOMINANT_COLOR
import com.example.mypokemon.constants.Constants.POKEMON_NAME
import com.example.mypokemon.screen.PokemonDetailScreen
import com.example.mypokemon.screen.PokemonListScreen
import com.example.mypokemon.ui.theme.MyPokemonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPokemonTheme {
                PokemonNavigation()
            }
        }
    }
}

@Composable
private fun PokemonNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
//        startDestination = PokemonScreen.POKEMON_LIST_SCREEN.name
        startDestination = "pokemon_list_screen"
    ) {
        composable(
//            route = PokemonScreen.POKEMON_LIST_SCREEN.name
            "pokemon_list_screen"
        ) {
            PokemonListScreen(navController = navController)
        }
        composable(
            "pokemon_detail_screen/{dominantColor}/{pokemonName}",
            arguments = listOf(
                navArgument("dominantColor") {
                    type = NavType.IntType
                },
                navArgument("pokemonName") {
                    type = NavType.StringType
                }
            )
//            route = "${PokemonScreen.POKEMON_DETAILS_SCREEN.name}/$DOMINANT_COLOR/$POKEMON_NAME}",
//            route = PokemonScreen.POKEMON_DETAILS_SCREEN.name,
//            arguments = listOf(
//                navArgument(DOMINANT_COLOR) { type = NavType.IntType },
//                navArgument(POKEMON_NAME) { type = NavType.StringType }
//            )
        ) {
            val dominantColor = remember {
                val color = it.arguments?.getInt("dominantColor")
                color?.let { Color(it) } ?: Color.White
            }
            val pokemonName = remember {
                it.arguments?.getString("pokemonName")
            }
            PokemonDetailScreen(
                navController = navController,
                dominantColor = dominantColor,
                pokemonName = pokemonName?.toLowerCase(Locale("en")) ?: ""
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    MyPokemonTheme {
        PokemonNavigation()
    }
}

enum class PokemonScreen {
    POKEMON_LIST_SCREEN,
    POKEMON_DETAILS_SCREEN
}