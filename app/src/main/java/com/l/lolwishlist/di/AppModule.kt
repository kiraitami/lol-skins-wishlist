package com.l.lolwishlist.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.l.lolwishlist.data.local.DDragonDatabase
import com.l.lolwishlist.data.remote.DDragonService
import com.l.lolwishlist.data.repository.DDragonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson) : Retrofit = Retrofit.Builder()
        .baseUrl(DDragonService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideDDragonService(retrofit: Retrofit): DDragonService = retrofit.create(DDragonService::class.java)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) = DDragonDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideWishlistDao(db: DDragonDatabase) = db.skinsDao()

    @Singleton
    @Provides
    fun providePatchVersionDao(db: DDragonDatabase) = db.patchVersionDao()

    @Singleton
    @Provides
    fun provideDDragonRepository(
        service: DDragonService,
        database: DDragonDatabase
    ) = DDragonRepository(service, database)
}