package com.l.lolwishlist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PatchVersion(
    @PrimaryKey val id: String = "1",
    val version: String
)
