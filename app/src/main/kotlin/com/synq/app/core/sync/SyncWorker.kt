package com.synq.app.core.sync
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.synq.app.domain.repository.ChatRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.synq.app.core.util.SynqLog

@HiltWorker class SyncWorker @AssistedInject constructor(@Assisted context: Context, @Assisted workerParams: WorkerParameters, private val chatRepository: ChatRepository) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = try {
        chatRepository.syncChats()
        Result.success()
    } catch (e: Exception) {
        SynqLog.e("SyncWorker", "Sync worker failed", e)
        Result.retry()
    }
}
