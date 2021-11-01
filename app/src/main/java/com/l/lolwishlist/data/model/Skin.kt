package com.l.lolwishlist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.l.lolwishlist.data.remote.DDragonService

@Entity
data class Skin(
    @PrimaryKey val id: String,
    val name: String,
    val num: Int,
    val championName: String = "",
    val championId: String = "",
    val selected: Boolean = false,
) {
    val imageUrl: String
        get() = DDragonService.getLoginScreenImageUrl(championId, num)
}