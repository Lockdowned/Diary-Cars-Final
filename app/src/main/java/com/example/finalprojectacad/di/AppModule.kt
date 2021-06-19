package com.example.finalprojectacad.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finalprojectacad.other.Constants.LOCAL_DATABASE_NAME
import com.example.finalprojectacad.ui.db.AppLocalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideLocalDatabase(
        @ApplicationContext appContext: Context
    ) = Room.databaseBuilder(
        appContext,
        AppLocalDatabase::class.java,
        LOCAL_DATABASE_NAME
    ).build() //return adn provide AppLocalDatabase

}