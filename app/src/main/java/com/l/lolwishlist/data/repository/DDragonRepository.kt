package com.l.lolwishlist.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.l.lolwishlist.data.local.DDragonDatabase
import com.l.lolwishlist.data.model.*
import com.l.lolwishlist.data.remote.DDragonService
import com.l.lolwishlist.utils.removeThrash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class DDragonRepository @Inject constructor(
    private val service: DDragonService,
    private val database: DDragonDatabase
) {

    private var queryJob: Job? = null

    fun getAllSkins() = flow<Result<List<Skin>>> {
        val versionFromRoom = database.patchVersionDao().loadPatchVersion().firstOrNull()?.version
        val versionFromNetwork = service.getVersions().first()
        val skinsFromRoom = database.skinsDao().loadSkins().first()

        if (versionFromRoom != versionFromNetwork) {
            try {
                val championsBase = service.getChampionsBase(versionFromNetwork).data.values.removeThrash()
                val championsDetails = buildChampionsDetails(versionFromNetwork, championsBase)
                val skinsFromNetwork = buildSkins(championsDetails)

                database.skinsDao().saveSkins(skinsFromNetwork)
                database.patchVersionDao().savePatchVersion(PatchVersion(version = versionFromNetwork))

                emit(Result.Success(skinsFromNetwork))
            }
            catch (e: IOException) {
                emit(Result.Success(skinsFromRoom))
            }
            catch (e: Exception) {
                emit(Result.Success(skinsFromRoom))
            }
        }
        else {
            emit(Result.Success(skinsFromRoom))
        }
    }
        .flowOn(Dispatchers.IO)
        .onStart {
            emit(Result.Loading())
        }

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


    @ExperimentalCoroutinesApi
    fun selectSkin(skinId: String, selected: Boolean) = callbackFlow {
        try {
            trySend(Result.Loading())
            var updates = 0

            database.withTransaction {
                updates = database.skinsDao().updateSkin(skinId, selected)
            }

            Log.d("_SELEC_", "updates: $updates")

            trySend(Result.Success(updates > 0))
        }
        catch (e: Exception) {
            trySend(Result.Error<Boolean>(e))
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