package com.example.finalprojectacad.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker

import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.finalprojectacad.data.remoteDB.FirebaseRequests
import com.example.finalprojectacad.other.utilities.SyncDatabasesClass
import com.example.finalprojectacad.repositories.MainRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncDatabaseWorker
@AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    val firebaseRequests: FirebaseRequests,
    val mainRepository: MainRepository
) : Worker(ctx, params) {


    override fun doWork(): Result {
        Log.d("worker", "start do worker jobs")
        SyncDatabasesClass(firebaseRequests, mainRepository)
            .syncOnce()
        return Result.success()
    }
}