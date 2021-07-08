package com.example.finalprojectacad.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.finalprojectacad.data.remoteDB.FirebaseRequests
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncDatabaseWorker
@AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    val firebaseRequests: FirebaseRequests
) : Worker(ctx, params) {


//    @Inject
//    lateinit var firebaseRequests: FirebaseRequests

    override fun doWork(): Result {
        Log.d("HEY", "doWork: Do something")
        firebaseRequests.testExistence() //why null?
        return Result.success()
    }


}