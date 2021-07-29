package com.beta.finalprojectacad.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.beta.finalprojectacad.data.remoteDB.FirebaseRequests
import com.beta.finalprojectacad.other.utilities.SyncDatabasesClass
import com.beta.finalprojectacad.repositories.MainRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncDatabaseWorker
@AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    val firebaseRequests: FirebaseRequests,
    val mainRepository: MainRepository
) : CoroutineWorker(ctx, params) {


    override suspend fun doWork(): Result {
        Log.d("worker", "start do worker jobs")
        SyncDatabasesClass(firebaseRequests, mainRepository)
            .syncOnce()
        return Result.success()
    }
}