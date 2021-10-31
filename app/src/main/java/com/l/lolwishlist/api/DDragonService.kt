package com.l.lolwishlist.api

import com.l.lolwishlist.model.ChampionDetails
import com.l.lolwishlist.model.ChampionsResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface DDragonService {

    @GET("api/versions.json")
    suspend fun getVersions(): List<String>

    @GET("cdn/{version}/data/en_US/champion.json")
    suspend fun getChampionsBase(
        @Path("version") version: String
    ): ChampionsResponse

    @GET("cdn/{version}/data/en_US/champion/{championId}.json")
    suspend fun getChampionDetails(
        @Path("version") version: String,
        @Path("championId") championId: String
    ): ChampionDetails

    companion object {
        private const val BASE_URL = "https://ddragon.leagueoflegends.com"

        fun create(): DDragonService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DDragonService::class.java)
    }
}