package com.l.lolwishlist.data.remote

import com.l.lolwishlist.model.ChampionDetailsResponse
import com.l.lolwishlist.model.ChampionsBaseResponse
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
    ): ChampionsBaseResponse

    @GET("cdn/{version}/data/en_US/champion/{championId}.json")
    suspend fun getChampionDetails(
        @Path("version") version: String,
        @Path("championId") championId: String
    ): ChampionDetailsResponse

    companion object {
        const val BASE_URL = "https://ddragon.leagueoflegends.com"

        fun getLoginScreenImageUrl(championId: String, skinNum: Int) = BASE_URL + "/cdn/img/champion/loading/${championId}_${skinNum}.jpg"
        fun getSplashArtImageUrl(championId: String, skinNum: Int) = BASE_URL + "/cdn/img/champion/splash/${championId}_${skinNum}.jpg"

        fun create(): DDragonService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DDragonService::class.java)
    }
}