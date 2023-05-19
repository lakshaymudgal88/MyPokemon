package com.example.mypokemon.repository

import com.example.mypokemon.data.remote.PokemonApi
import com.example.mypokemon.data.remote.responses.Pokemon
import com.example.mypokemon.data.remote.responses.PokemonList
import com.example.mypokemon.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val pokemonApi: PokemonApi,
) {

    suspend fun getPokemonList(offSet: Int, limit: Int): Resource<PokemonList> {
        val resource = try {
            pokemonApi.getPokemonList(offSet, limit)
        } catch (e: Exception) {
            return Resource.Error(message = "An unknown error occurred!")
        }
        return Resource.Success(resource)
    }

    suspend fun getPokemon(name: String): Resource<Pokemon> {
        val resource = try {
            pokemonApi.getPokemon(name)
        } catch (e: Exception) {
            return Resource.Error(message = "An unknown error occurred!")
        }
        return Resource.Success(resource)
    }
}