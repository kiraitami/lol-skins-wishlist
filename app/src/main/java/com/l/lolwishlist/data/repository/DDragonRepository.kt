package com.l.lolwishlist.data.repository

import androidx.room.withTransaction
import com.l.lolwishlist.data.local.DDragonDatabase
import com.l.lolwishlist.data.model.ChampionBase
import com.l.lolwishlist.data.model.ChampionDetails
import com.l.lolwishlist.data.model.Skin
import com.l.lolwishlist.data.networkBoundResource
import com.l.lolwishlist.data.remote.DDragonService
import com.l.lolwishlist.utils.removeThrash
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

private const val version = "11.21.1"

class DDragonRepository @Inject constructor(
    private val service: DDragonService,
    private val database: DDragonDatabase
) {
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