package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.notification.PrayerNotificationHelper
import com.example.notification.PrayerNotificationSchedulerService
import java.util.concurrent.TimeUnit

/**
 * WorkManager CoroutineWorker that runs periodically in the background to calculate
 * solar prayer timings based on the user's location and ensure local prayer notifications
 * and alarms are scheduled accurately.
 */
class PrayerNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting PrayerNotificationWorker execution...")
        return try {
            val settings = PrayerNotificationHelper.getSettings(applicationContext)
            if (!settings.alertsEnabled) {
                Log.i(TAG, "Prayer alerts are disabled by user settings. Cancelling alarms.")
                PrayerNotificationHelper.cancelAllPrayerAlarms(applicationContext)
                return Result.success(workDataOf("status" to "disabled"))
            }

            // Schedule prayer notifications using location-aware algorithm
            val resultSummary = PrayerNotificationSchedulerService.scheduleNotificationsForLocation(
                context = applicationContext,
                forceGps = false
            )
            Log.i(TAG, "PrayerNotificationWorker completed successfully: $resultSummary")
            Result.success(workDataOf("summary" to resultSummary))
        } catch (e: Exception) {
            Log.e(TAG, "PrayerNotificationWorker failed: ${e.message}", e)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure(workDataOf("error" to (e.message ?: "Unknown error")))
            }
        }
    }

    companion object {
        const val TAG = "PrayerNotificationWorker"
        const val UNIQUE_PERIODIC_WORK_NAME = "deen365_prayer_notification_work"

        /**
         * Enqueues or updates the periodic WorkManager job to refresh prayer notifications
         * every 6 hours with a 15-minute flex interval.
         */
        fun enqueuePeriodicPrayerNotificationWork(context: Context) {
            try {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresBatteryNotLow(false)
                    .build()

                val periodicRequest = PeriodicWorkRequestBuilder<PrayerNotificationWorker>(
                    repeatInterval = 6,
                    repeatIntervalTimeUnit = TimeUnit.HOURS,
                    flexTimeInterval = 15,
                    flexTimeIntervalUnit = TimeUnit.MINUTES
                )
                    .setConstraints(constraints)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                    .addTag(TAG)
                    .build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    UNIQUE_PERIODIC_WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicRequest
                )
                Log.d(TAG, "Enqueued periodic WorkManager job: $UNIQUE_PERIODIC_WORK_NAME")
            } catch (e: Throwable) {
                Log.w(TAG, "WorkManager could not be enqueued: ${e.message}")
            }
        }

        /**
         * Cancels periodic prayer notification worker if user turns off notifications completely.
         */
        fun cancelPrayerNotificationWork(context: Context) {
            try {
                WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_PERIODIC_WORK_NAME)
                Log.d(TAG, "Cancelled unique WorkManager job: $UNIQUE_PERIODIC_WORK_NAME")
            } catch (e: Throwable) {
                Log.w(TAG, "WorkManager cancel error: ${e.message}")
            }
        }
    }
}
