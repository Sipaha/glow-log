package com.glowlog.app.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.glowlog.app.data.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "SyncWorker started, attempt=$runAttemptCount")
        val syncResult = syncRepository.sync()
        return when {
            syncResult.isSuccess -> {
                Log.d(TAG, "Sync completed successfully")
                Result.success()
            }
            runAttemptCount < 3 -> {
                Log.w(TAG, "Sync failed, will retry", syncResult.exceptionOrNull())
                Result.retry()
            }
            else -> {
                Log.e(TAG, "Sync failed after $runAttemptCount attempts", syncResult.exceptionOrNull())
                Result.failure()
            }
        }
    }

    companion object {
        private const val TAG = "GlowLog.Sync"
    }
}
