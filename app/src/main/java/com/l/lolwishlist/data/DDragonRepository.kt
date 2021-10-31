package com.l.lolwishlist.data

import com.l.lolwishlist.api.DDragonService
import com.l.lolwishlist.model.ChampionDetails
import com.l.lolwishlist.model.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class DDragonRepository(
    private val service: DDragonService
) {

    @ExperimentalCoroutinesApi
    fun getVersion() = callbackFlow {
        try {
            val response = service.getVersions()
            trySend(response)
        }
        catch (e: Exception) {
            trySend(null)
        }
        awaitClose {  }
    }


    @ExperimentalCoroutinesApi
    fun getChampionsBase(version: String) = callbackFlow {
        try {
            val response = service.getChampionsBase(version)
            trySend(response)
        }
        catch (e: Exception) {
            trySend(null)
        }
        awaitClose {  }
    }

    @ExperimentalCoroutinesApi
    fun getChampionDetails(version: String, championId: String) = callbackFlow {
        try {
            trySend(Result.Loading<ChampionDetails>())
            val response = service.getChampionDetails(version, championId)
            trySend(Result.Success<ChampionDetails>(response))
        }
        catch (e: Exception) {
            trySend(Result.Error<ChampionDetails>(e))
        }
        awaitClose {  }
    }
}