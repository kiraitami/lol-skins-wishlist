package com.l.lolwishlist.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.l.lolwishlist.data.model.PatchVersion
import kotlinx.coroutines.flow.Flow

@Dao
interface PatchVersionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePatchVersion(patchVersion: PatchVersion)

    @Query("SELECT * FROM patchVersion WHERE id = :id")
    fun loadPatchVersion(id: String = "1"): Flow<PatchVersion>
}