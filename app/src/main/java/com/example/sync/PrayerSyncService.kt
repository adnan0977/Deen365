package com.example.sync

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.data.local.DeenDatabase
import com.example.data.local.PrayerRecordEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PrayerSyncService : Service() {

    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_SYNC_ALL
        Log.d(TAG, "PrayerSyncService onStartCommand with action=$action (startId=$startId)")

        serviceScope.launch {
            try {
                performBackgroundSync(startId)
            } catch (e: Exception) {
                Log.e(TAG, "Background prayer sync error: ${e.message}", e)
                _syncStatus.value = PrayerSyncStatus.Error(e.localizedMessage ?: "Sync failed")
                stopSelf(startId)
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun performBackgroundSync(startId: Int) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            Log.i(TAG, "No authenticated user found for Firestore prayer sync.")
            _syncStatus.value = PrayerSyncStatus.Error("Sign in to sync your prayer history across devices")
            stopSelf(startId)
            return
        }

        val uid = user.uid
        _syncStatus.value = PrayerSyncStatus.Syncing

        val db = DeenDatabase.getDatabase(applicationContext)
        val trackerDao = db.trackerDao()
        val firestore = FirebaseFirestore.getInstance()
        val prayerRecordsCol = firestore.collection("users")
            .document(uid)
            .collection("prayer_records")

        val deviceName = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"

        // 1. Fetch local records and upload to Firestore
        val localRecords = trackerDao.getAllPrayerRecordsSync()
        var uploadedCount = 0

        for (record in localRecords) {
            val recordMap = hashMapOf<String, Any>(
                "dateStr" to record.dateStr,
                "userId" to uid,
                "fajr" to record.fajr,
                "dhuhr" to record.dhuhr,
                "asr" to record.asr,
                "maghrib" to record.maghrib,
                "isha" to record.isha,
                "completedCount" to record.completedCount,
                "updatedAt" to record.updatedAt,
                "deviceModel" to if (record.deviceModel.isNotBlank()) record.deviceModel else deviceName
            )
            prayerRecordsCol.document(record.dateStr).set(recordMap, SetOptions.merge()).await()
            trackerDao.markPrayerRecordAsSynced(record.dateStr, record.updatedAt)
            uploadedCount++
        }

        // 2. Fetch remote records from Firestore (records from any user device)
        val remoteSnapshot = prayerRecordsCol.get().await()
        var downloadedCount = 0
        val localMap = localRecords.associateBy { it.dateStr }.toMutableMap()
        val mergedListToSave = mutableListOf<PrayerRecordEntity>()

        for (doc in remoteSnapshot.documents) {
            val dateStr = doc.getString("dateStr") ?: doc.id
            if (dateStr.isBlank()) continue

            val remoteFajr = doc.getBoolean("fajr") ?: false
            val remoteDhuhr = doc.getBoolean("dhuhr") ?: false
            val remoteAsr = doc.getBoolean("asr") ?: false
            val remoteMaghrib = doc.getBoolean("maghrib") ?: false
            val remoteIsha = doc.getBoolean("isha") ?: false
            val remoteUpdatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
            val remoteDevice = doc.getString("deviceModel") ?: ""

            val local = localMap[dateStr]
            if (local == null) {
                // Completely new historical record logged on another device
                mergedListToSave.add(
                    PrayerRecordEntity(
                        dateStr = dateStr,
                        fajr = remoteFajr,
                        dhuhr = remoteDhuhr,
                        asr = remoteAsr,
                        maghrib = remoteMaghrib,
                        isha = remoteIsha,
                        updatedAt = remoteUpdatedAt,
                        isSynced = true,
                        deviceModel = remoteDevice
                    )
                )
                downloadedCount++
            } else {
                // Merge records so no prayer completed on any device is lost
                val mergedFajr = local.fajr || remoteFajr
                val mergedDhuhr = local.dhuhr || remoteDhuhr
                val mergedAsr = local.asr || remoteAsr
                val mergedMaghrib = local.maghrib || remoteMaghrib
                val mergedIsha = local.isha || remoteIsha
                val mergedUpdatedAt = maxOf(local.updatedAt, remoteUpdatedAt)
                val resolvedDevice = if (local.deviceModel.isNotBlank()) local.deviceModel else remoteDevice

                if (mergedFajr != local.fajr || mergedDhuhr != local.dhuhr ||
                    mergedAsr != local.asr || mergedMaghrib != local.maghrib ||
                    mergedIsha != local.isha
                ) {
                    mergedListToSave.add(
                        local.copy(
                            fajr = mergedFajr,
                            dhuhr = mergedDhuhr,
                            asr = mergedAsr,
                            maghrib = mergedMaghrib,
                            isha = mergedIsha,
                            updatedAt = mergedUpdatedAt,
                            isSynced = true,
                            deviceModel = resolvedDevice
                        )
                    )
                    downloadedCount++
                }
            }
        }

        if (mergedListToSave.isNotEmpty()) {
            trackerDao.insertOrUpdatePrayerRecords(mergedListToSave)
        }

        val completedTimestamp = System.currentTimeMillis()
        _lastSyncTime.value = completedTimestamp
        _syncStatus.value = PrayerSyncStatus.Success(
            uploadedCount = uploadedCount,
            downloadedCount = downloadedCount,
            timestamp = completedTimestamp
        )

        Log.i(TAG, "Prayer background sync finished successfully: $uploadedCount uploaded, $downloadedCount merged.")
        stopSelf(startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    companion object {
        private const val TAG = "PrayerSyncService"
        const val ACTION_SYNC_ALL = "com.example.deen365.action.SYNC_PRAYER_HISTORY"

        private val _syncStatus = MutableStateFlow<PrayerSyncStatus>(PrayerSyncStatus.Idle)
        val syncStatus: StateFlow<PrayerSyncStatus> = _syncStatus.asStateFlow()

        private val _lastSyncTime = MutableStateFlow<Long>(0L)
        val lastSyncTime: StateFlow<Long> = _lastSyncTime.asStateFlow()

        fun startSync(context: Context) {
            val intent = Intent(context, PrayerSyncService::class.java).apply {
                action = ACTION_SYNC_ALL
            }
            try {
                context.startService(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Unable to start PrayerSyncService: ${e.message}", e)
            }
        }

        fun updateStatusManually(status: PrayerSyncStatus) {
            _syncStatus.value = status
        }
    }
}
