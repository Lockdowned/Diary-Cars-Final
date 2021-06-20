package com.example.finalprojectacad.di

import android.content.Context
import androidx.room.CoroutinesRoom
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.finalprojectacad.other.Constants.LOCAL_DATABASE_NAME
import com.example.finalprojectacad.db.AppLocalDatabase
import com.example.finalprojectacad.db.dao.CarDao
import com.example.finalprojectacad.db.entity.BrandRoom
import com.example.finalprojectacad.other.utilities.PopulateDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideLocalDatabase(
        @ApplicationContext appContext: Context,
        carDaoProvider: Provider<CarDao> // how we can get dao provider before than we create db
    ) = synchronized(this){ // i almost sure that is scope need sinhronize(yes, but how is actually work)
        Room.databaseBuilder(
            appContext,
            AppLocalDatabase::class.java,
            LOCAL_DATABASE_NAME
        ).addCallback(
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    //initialize database first time
                    CoroutineScope(SupervisorJob()).launch {
                        PopulateDatabase().insertDB(carDaoProvider.get())
                    }
                }
            }
        )
            .build()
    } //return an provide AppLocalDatabase

    private val localDatabaseCallback = object: RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)


        }
    }

    @Singleton
    @Provides
    fun provideCarDao(db: AppLocalDatabase): CarDao {
        return  db.carDao()
    }



}