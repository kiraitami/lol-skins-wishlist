package com.l.lolwishlist.data.repository

import androidx.room.withTransaction
import com.l.lolwishlist.data.local.DDragonDatabase
import com.l.lolwishlist.data.model.ChampionBase
import com.l.lolwishlist.data.model.ChampionDetails
import com.l.lolwishlist.data.model.Result
import com.l.lolwishlist.data.model.Skin
import com.l.lolwishlist.data.networkBoundResource
import com.l.lolwishlist.data.remote.DDragonService
import com.l.lolwishlist.utils.removeThrash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val version = "11.21.1"

class DDragonRepository @Inject constructor(
    private val service: DDragonService,
    private val database: DDragonDatabase
) {

    private var queryJob: Job? = null

    @ExperimentalCoroutinesApi
    fun getAllSkins() = networkBoundResource(
        fromRoom = { database.skinsDao().loadSkins() },
        fromNetwork = {
            val championsBase = service.getChampionsBase(version).data.values.removeThrash()
            val championsDetails = buildChampionsDetails(version, championsBase)
            buildSkins(championsDetails)
        },
        saveNetworkResult = {
            database.withTransaction {
                database.skinsDao().saveSkins(it)
            }
        }
    )

    @ExperimentalCoroutinesApi
    suspend fun getSelectedSkins() = callbackFlow {
        try {
            trySend(Result.Loading<List<Skin>>())

            database.withTransaction {
                val selectedSkins = database.skinsDao().loadMyWishlistSkins().first()
                trySend(Result.Success<List<Skin>>(selectedSkins))
            }
        }
        catch (e: Exception) {
            trySend(Result.Error<List<Skin>>(e))
        }
        awaitClose {  }
    }

    @ExperimentalCoroutinesApi
    suspend fun querySkinsByName(query: String) = callbackFlow {
        queryJob?.cancel()
        queryJob = launch (Dispatchers.IO) {
            try {
                trySend(Result.Loading<List<Skin>>())

                database.withTransaction {
                    database.skinsDao().querySkinsByName(query)
                        .onEach {
                            trySend(Result.Success<List<Skin>>(it))
                        }
                        .launchIn(this)
                }
            }
            catch (e: Exception) {
                trySend(Result.Error<List<Skin>>(e))
            }
        }

        awaitClose {  }
    }



    private suspend fun buildChampionsDetails(version: String, championsBase: List<ChampionBase>): List<ChampionDetails> {
        val championsDetails = mutableListOf<ChampionDetails>()

        championsBase.forEach { championBase ->
            val details = service.getChampionDetails(version, championBase.id).data.values.firstOrNull()
            details?.let {
                championsDetails.add(it)
            }
        }

        return championsDetails
    }

    private fun buildSkins(championsDetails: List<ChampionDetails>): List<Skin> {
        val skins = mutableListOf<Skin>()

        championsDetails.forEach { champion ->
            champion.skins.filterNot { it.name.equals("default", true) }.forEach {
                skins.add(
                    Skin(
                        id = it.id,
                        name = it.name,
                        num = it.num,
                        championName = champion.name,
                        championId = champion.id
                    )
                )
            }
        }

        return skins
    }
}