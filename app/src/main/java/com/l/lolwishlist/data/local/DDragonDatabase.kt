package com.l.lolwishlist.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.l.lolwishlist.data.model.Converter
import com.l.lolwishlist.data.model.PatchVersion
import com.l.lolwishlist.data.model.Skin

@Database(entities = [Skin::class, PatchVersion::class], version = 11, exportSchema = false)
@TypeConverters(Converter::class)
abstract class DDragonDatabase : RoomDatabase() {

    abstract fun skinsDao(): SkinsDao
    abstract fun patchVersionDao(): PatchVersionDao

    companion object {
        @Volatile private var INSTANCE: DDragonDatabase? = null

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, DDragonDatabase::class.java, "ddragon")
                .fallbackToDestructiveMigration()
                .build()

        fun getDatabase(context: Context): DDragonDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }
    }
}