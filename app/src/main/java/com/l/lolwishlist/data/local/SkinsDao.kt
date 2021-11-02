package com.l.lolwishlist.data.local

import androidx.room.*
import com.l.lolwishlist.data.model.Skin
import kotlinx.coroutines.flow.Flow

@Dao
interface SkinsDao {
    @Insert(entity = Skin::class, onConflict = OnConflictStrategy.REPLACE)
    fun saveSkins(skin: List<Skin>)

    @Query("SELECT * FROM skin")
    fun loadSkins(): Flow<List<Skin>>

    @Query("SELECT * FROM skin WHERE selected = 1")
    fun loadSelectedSkins(): Flow<List<Skin>>

    @Insert(entity = Skin::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateSkin(skin: Skin)
}