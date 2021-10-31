package com.l.lolwishlist.model

data class ChampionsResponse(
    val data: Map<String, ChampionBase>
)

data class ChampionBase(
    val id: String,
    val name: String,
)

data class ChampionDetails(
    val id: String,
    val name: String,
    val skins: List<Skin>
)
