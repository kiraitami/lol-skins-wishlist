package com.l.lolwishlist.data.local

import androidx.room.*
import com.l.lolwishlist.data.model.Skin
import kotlinx.coroutines.flow.Flow

@Dao
interface SkinsDao {
    @Insert(entity = Skin::class, onConflict = OnConflictStrategy.REPLACE)
    fun saveSkins(skin: List<Skin>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSkin(skin: Skin)

    @Query("SELECT * FROM skin")
    fun loadSkins(): Flow<List<Skin>>

    @Query("DELETE FROM skin WHERE id = :id")
    fun deleteSkin(id: String)
}