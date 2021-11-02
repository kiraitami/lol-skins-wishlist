package com.l.lolwishlist.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.l.lolwishlist.data.model.Skin
import kotlinx.coroutines.flow.Flow

@Dao
interface SkinsDao {
    @Insert(entity = Skin::class, onConflict = OnConflictStrategy.IGNORE)
    fun saveSkins(skin: List<Skin>)

    @Query("SELECT * FROM skin")
    fun loadSkins(): Flow<List<Skin>>

    @Query("SELECT * FROM skin WHERE selected = 1")
    fun loadMyWishlistSkins(): Flow<List<Skin>>

    @Query("SELECT * FROM skin WHERE championId LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun querySkinsByName(query: String): Flow<List<Skin>>

    @Query("UPDATE skin SET selected = :selected WHERE id = :id")
    fun updateSkin(id:String, selected: Boolean): Int
}