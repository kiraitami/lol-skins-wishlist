package com.l.lolwishlist.data.repository

import androidx.room.withTransaction
import com.l.lolwishlist.data.local.DDragonDatabase
import com.l.lolwishlist.data.remote.DDragonService
import com.l.lolwishlist.model.*
import com.l.lolwishlist.utils.removeThrash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

class DDragonRepository @Inject constructor(
    private val service: DDragonService,
    private val database: DDragonDatabase
) {

    fun getAllSkins() = flow<List<Skin>> {
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

                emit(skinsFromNetwork)
            }
            catch (e: IOException) {
                emit(skinsFromRoom)
            }
            catch (e: Exception) {
                emit(skinsFromRoom)
            }
        }
        else {
            emit(skinsFromRoom)
        }
    }
        .flowOn(Dispatchers.IO)


    @ExperimentalCoroutinesApi
    fun selectSkin(skinId: String, selected: Boolean) = callbackFlow {
        try {
            trySend(Result.Loading())
            var updates = 0

            database.withTransaction {
                updates = database.skinsDao().updateSkin(skinId, selected)
            }

            trySend(Result.Success(updates > 0))
        }
        catch (e: Exception) {
            trySend(Result.Error<Boolean>(e))
        }

        awaitClose {  }
    }
        .flowOn(Dispatchers.IO)


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