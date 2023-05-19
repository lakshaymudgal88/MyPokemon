package com.example.mypokemon.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mypokemon.data.remote.responses.Pokemon
import com.example.mypokemon.repository.PokemonRepository
import com.example.mypokemon.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonInfoViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository,
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return pokemonRepository.getPokemon(pokemonName)
    }
}