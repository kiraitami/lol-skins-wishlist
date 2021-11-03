package com.l.lolwishlist.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson

data class ChampionsBaseResponse(
    val data: Map<String, ChampionBase>
)

data class ChampionDetailsResponse(
    val data: Map<String, ChampionDetails>
)

@Entity
data class ChampionBase(
    @PrimaryKey val id: String,
    val name: String,
)

@Entity
data class ChampionDetails(
    @PrimaryKey val id: String,
    val name: String,
    val skins: List<Skin>
)

class Converter {
    @TypeConverter
    fun listToJson(value: List<Skin>) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Skin>::class.java)
}
