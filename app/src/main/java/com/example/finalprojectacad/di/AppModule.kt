package com.example.finalprojectacad.di

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.finalprojectacad.R
import com.example.finalprojectacad.db.AppLocalDatabase
import com.example.finalprojectacad.db.dao.CarDao
import com.example.finalprojectacad.db.dao.RouteDao
import com.example.finalprojectacad.other.utilities.PopulateDatabase
import com.example.finalprojectacad.ui.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
    ) = synchronized(this){ // i almost sure that is scope need synchronize(yes, but how is actually work)
        Room.databaseBuilder(
            appContext,
            AppLocalDatabase::class.java,
            "local_db"
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

    @Singleton
    @Provides
    fun provideCarDao(db: AppLocalDatabase): CarDao {
        return  db.carDao()
    }

    @Singleton
    @Provides
    fun provideRouteDao(db: AppLocalDatabase): RouteDao {
        return db.routeDao()
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}