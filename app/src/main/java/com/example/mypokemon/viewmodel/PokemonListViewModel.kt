package com.example.mypokemon.viewmodel

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.mypokemon.data.models.PokemonListEntity
import com.example.mypokemon.repository.PokemonRepository
import com.example.mypokemon.util.Constants.PAGE_SIZE
import com.example.mypokemon.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository,
) : ViewModel() {

    private var currPage = 0
    var pokemonList = mutableStateOf<List<PokemonListEntity>>(listOf())
    var loadError = mutableStateOf("")
    var endReached = mutableStateOf(false)
    var isLoading = mutableStateOf(false)

    private var cachedPokemonList = listOf<PokemonListEntity>()
    private var isSearchingStarting = true
    var isSearching = mutableStateOf(false)

    init {
        paginatedPokemon()
    }

    fun searchPokemonList(query: String) {
        val listToSearch = if(isSearchingStarting) {
            pokemonList.value
        } else {
            cachedPokemonList
        }

        viewModelScope.launch(Dispatchers.Default) {
            if(query.isEmpty()) {
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchingStarting = true
                return@launch
            }

        }

        val results = listToSearch.filter {
            it.pokemonName.contains(query.trim(), true) ||
                    it.number.toString() == query.trim()
        }
        if(isSearchingStarting) {
            cachedPokemonList = pokemonList.value
            isSearchingStarting = false
        }
        pokemonList.value = results
        isSearching.value = true
    }

    fun paginatedPokemon() {
        viewModelScope.launch {
            isLoading.value = true
            when (val result = pokemonRepository.getPokemonList(PAGE_SIZE + currPage, PAGE_SIZE)) {
                is Resource.Success -> {
                    val pokemonEntries = result.data!!.results.mapIndexed { index, entry ->
                        val number = if (entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokemonListEntity(entry.name.capitalize(Locale("en")), url, number.toInt())
                    }
                    currPage++
                    isLoading.value = false
                    loadError.value = ""
                    pokemonList.value += pokemonEntries
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }
                else -> null
            }
        }
    }

    fun calculateDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bitmap).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}