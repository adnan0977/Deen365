package com.example.notification

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import com.example.domain.CalculationMethod
import com.example.domain.DeviceLocationProvider
import com.example.domain.PrayerSchedule
import com.example.domain.PrayerTimeCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Background Service responsible for location-aware scheduling of the five daily prayer
 * notifications (Fajr, Dhuhr, Asr, Maghrib, Isha).
 *
 * It dynamically obtains the user's real-time device location via GPS/Network provider
 * (or user-selected city fallback), calculates astronomical solar prayer timings,
 * and configures exact or high-priority RTC alarms via AlarmManager.
 */
class PrayerNotificationSchedulerService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_SCHEDULE_PRAYERS
        val forceGps = intent?.getBooleanExtra(EXTRA_FORCE_GPS, false) ?: false
        val testPrayerName = intent?.getStringExtra(EXTRA_TEST_PRAYER) ?: "Asr"

        Log.d(TAG, "onStartCommand action=$action forceGps=$forceGps startId=$startId")

        serviceScope.launch {
            try {
                when (action) {
                    ACTION_SCHEDULE_PRAYERS, ACTION_RESCHEDULE_BOOT -> {
                        scheduleNotificationsForLocation(applicationContext, forceGps)
                    }
                    ACTION_CANCEL_ALL -> {
                        PrayerNotificationHelper.cancelAllPrayerAlarms(applicationContext)
                        saveScheduleStatus(
                            context = applicationContext,
                            isScheduled = false,
                            cityName = "Alarms Disabled",
                            summary = "Prayer alarms disabled by user",
                            nextPrayerInfo = null
                        )
                    }
                    ACTION_TEST_NOTIFICATION -> {
                        val settings = PrayerNotificationHelper.getSettings(applicationContext)
                        val prefs = getPrefs(applicationContext)
                        val cityName = prefs.getString(KEY_LAST_CITY, "Your Location") ?: "Your Location"
                        PrayerNotificationHelper.triggerInstantTestPrayerNotification(
                            context = applicationContext,
                            prayerName = testPrayerName,
                            cityName = cityName
                        )
                    }
                    ACTION_TEST_FASTING_NOTIFICATION -> {
                        val prefs = getPrefs(applicationContext)
                        val cityName = prefs.getString(KEY_LAST_CITY, "Your Location") ?: "Your Location"
                        PrayerNotificationHelper.triggerInstantTestFastingNotification(
                            context = applicationContext,
                            cityName = cityName
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error executing prayer scheduling task: ${e.message}", e)
            } finally {
                stopSelf(startId)
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    companion object {
        const val TAG = "PrayerSchedulerService"

        suspend fun scheduleNotificationsForLocation(context: Context, forceGps: Boolean = false): String {
            val settings = PrayerNotificationHelper.getSettings(context)

            if (!settings.alertsEnabled) {
                Log.i(TAG, "Prayer alerts disabled in settings. Cancelling all existing alarms.")
                PrayerNotificationHelper.cancelAllPrayerAlarms(context)
                val summary = "Prayer notifications are turned off in settings."
                saveScheduleStatus(
                    context = context,
                    isScheduled = false,
                    cityName = "Alarms Disabled",
                    summary = summary,
                    nextPrayerInfo = null
                )
                return summary
            }

            // 1. Resolve Location: Try GPS first if permission is granted
            var prayerSchedule: PrayerSchedule? = null
            var resolvedLocationName = ""
            var isGpsUsed = false

            if (DeviceLocationProvider.hasLocationPermission(context)) {
                try {
                    val locResult = DeviceLocationProvider.getDeviceLocation(context)
                    if (locResult != null) {
                        resolvedLocationName = if (locResult.localityName.isNotBlank()) {
                            "${locResult.localityName}, ${locResult.countryName}"
                        } else {
                            "GPS (${String.format(Locale.US, "%.2f, %.2f", locResult.latitude, locResult.longitude)})"
                        }
                        isGpsUsed = true

                        prayerSchedule = PrayerTimeCalculator.calculateScheduleForCoordinates(
                            latitude = locResult.latitude,
                            longitude = locResult.longitude,
                            timezone = locResult.timezoneHours,
                            cityName = resolvedLocationName,
                            method = getSavedCalculationMethod(context),
                            isUsingGpsLocation = true
                        )
                        Log.d(TAG, "Calculated schedule from GPS coordinates: $resolvedLocationName")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "GPS location lookup failed: ${e.message}, falling back to preset city.")
                }
            }

            // 2. Fallback to saved / default city preset if GPS was unavailable
            if (prayerSchedule == null) {
                val savedCity = getSavedCity(context)
                resolvedLocationName = savedCity.name
                prayerSchedule = PrayerTimeCalculator.calculateSchedule(
                    city = savedCity,
                    method = getSavedCalculationMethod(context)
                )
                Log.d(TAG, "Calculated schedule from city preset: ${savedCity.name}")
            }

            // 3. Configure Alarms via AlarmManager
            PrayerNotificationHelper.scheduleDailyPrayerAlarms(context, prayerSchedule, settings)
            // Also refresh daily Quran reading reminder schedule
            PrayerNotificationHelper.scheduleDailyQuranReminder(context)

            // 4. Retrieve next alarm info
            val nextAlarmInfo = PrayerNotificationHelper.getNextScheduledAlarmInfo(context, prayerSchedule, settings)

            val activePrayersCount = listOf(
                settings.notifyFajr,
                settings.notifyDhuhr,
                settings.notifyAsr,
                settings.notifyMaghrib,
                settings.notifyIsha
            ).count { it }

            val fastingNote = if (settings.notifySunsetFastingReminder) " • Sunset Fasting Reminder Active" else ""
            val summary = "Active for $resolvedLocationName • $activePrayersCount of 5 prayers scheduled$fastingNote (Next: ${nextAlarmInfo?.prayerName ?: "Fajr"} at ${nextAlarmInfo?.formattedTime ?: ""})"

            saveScheduleStatus(
                context = context,
                isScheduled = true,
                cityName = resolvedLocationName,
                summary = summary,
                nextPrayerInfo = nextAlarmInfo?.let { "${it.prayerName} at ${it.formattedTime} (${it.remainingFormatted})" }
            )

            // 5. Broadcast update to active UI components
            val broadcastIntent = Intent(ACTION_PRAYERS_SCHEDULED_BROADCAST).apply {
                putExtra(EXTRA_LOCATION_NAME, resolvedLocationName)
                putExtra(EXTRA_SUMMARY, summary)
                putExtra(EXTRA_IS_GPS, isGpsUsed)
                putExtra(EXTRA_NEXT_PRAYER, nextAlarmInfo?.prayerName)
                putExtra(EXTRA_NEXT_PRAYER_TIME, nextAlarmInfo?.formattedTime)
                setPackage(context.packageName)
            }
            context.sendBroadcast(broadcastIntent)

            Log.i(TAG, "Prayer notification scheduling completed: $summary")
            return summary
        }

        fun saveScheduleStatus(
            context: Context,
            isScheduled: Boolean,
            cityName: String,
            summary: String,
            nextPrayerInfo: String?
        ) {
            val nowFormatted = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(Date())
            getPrefs(context).edit()
                .putBoolean(KEY_IS_SCHEDULED, isScheduled)
                .putString(KEY_LAST_CITY, cityName)
                .putString(KEY_LAST_SUMMARY, summary)
                .putString(KEY_LAST_TIMESTAMP, nowFormatted)
                .putString(KEY_NEXT_PRAYER_INFO, nextPrayerInfo ?: "None")
                .apply()
        }

        const val ACTION_SCHEDULE_PRAYERS = "com.example.deen365.ACTION_SCHEDULE_PRAYERS"
        const val ACTION_RESCHEDULE_BOOT = "com.example.deen365.ACTION_RESCHEDULE_BOOT"
        const val ACTION_CANCEL_ALL = "com.example.deen365.ACTION_CANCEL_ALL"
        const val ACTION_TEST_NOTIFICATION = "com.example.deen365.ACTION_TEST_NOTIFICATION"
        const val ACTION_TEST_FASTING_NOTIFICATION = "com.example.deen365.ACTION_TEST_FASTING_NOTIFICATION"

        const val ACTION_PRAYERS_SCHEDULED_BROADCAST = "com.example.deen365.PRAYERS_SCHEDULED_BROADCAST"

        const val EXTRA_FORCE_GPS = "extra_force_gps"
        const val EXTRA_TEST_PRAYER = "extra_test_prayer"

        const val EXTRA_LOCATION_NAME = "extra_location_name"
        const val EXTRA_SUMMARY = "extra_summary"
        const val EXTRA_IS_GPS = "extra_is_gps"
        const val EXTRA_NEXT_PRAYER = "extra_next_prayer"
        const val EXTRA_NEXT_PRAYER_TIME = "extra_next_prayer_time"

        private const val PREFS_SCHEDULER = "prayer_scheduler_service_prefs"
        const val KEY_IS_SCHEDULED = "key_is_scheduled"
        const val KEY_LAST_CITY = "key_last_city"
        const val KEY_LAST_SUMMARY = "key_last_summary"
        const val KEY_LAST_TIMESTAMP = "key_last_timestamp"
        const val KEY_NEXT_PRAYER_INFO = "key_next_prayer_info"

        fun getPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFS_SCHEDULER, Context.MODE_PRIVATE)
        }

        fun getLatestScheduleSummary(context: Context): String {
            val prefs = getPrefs(context)
            return prefs.getString(KEY_LAST_SUMMARY, "Local notifications active based on your location")
                ?: "Local notifications active based on your location"
        }

        fun isAlarmsScheduled(context: Context): Boolean {
            return getPrefs(context).getBoolean(KEY_IS_SCHEDULED, true)
        }

        /**
         * Enqueues or starts the background scheduling service.
         */
        fun startScheduling(context: Context, forceGps: Boolean = false) {
            val intent = Intent(context, PrayerNotificationSchedulerService::class.java).apply {
                action = ACTION_SCHEDULE_PRAYERS
                putExtra(EXTRA_FORCE_GPS, forceGps)
            }
            context.startService(intent)
        }

        fun cancelAll(context: Context) {
            val intent = Intent(context, PrayerNotificationSchedulerService::class.java).apply {
                action = ACTION_CANCEL_ALL
            }
            context.startService(intent)
        }

        fun sendTestNotification(context: Context, prayerName: String = "Asr") {
            val intent = Intent(context, PrayerNotificationSchedulerService::class.java).apply {
                action = ACTION_TEST_NOTIFICATION
                putExtra(EXTRA_TEST_PRAYER, prayerName)
            }
            context.startService(intent)
        }

        fun sendTestFastingNotification(context: Context) {
            val intent = Intent(context, PrayerNotificationSchedulerService::class.java).apply {
                action = ACTION_TEST_FASTING_NOTIFICATION
            }
            context.startService(intent)
        }

        private fun getSavedCity(context: Context): com.example.domain.CityPreset {
            val prefs = context.getSharedPreferences("deen365_prefs", Context.MODE_PRIVATE)
            val cityName = prefs.getString("selected_city_name", null)
            return PrayerTimeCalculator.POPULAR_CITIES.find { it.name == cityName }
                ?: PrayerTimeCalculator.POPULAR_CITIES[0]
        }

        private fun getSavedCalculationMethod(context: Context): CalculationMethod {
            val prefs = context.getSharedPreferences("deen365_prefs", Context.MODE_PRIVATE)
            val methodId = prefs.getString("selected_method_id", CalculationMethod.KARACHI.name)
            return try {
                CalculationMethod.valueOf(methodId ?: CalculationMethod.KARACHI.name)
            } catch (_: Exception) {
                CalculationMethod.KARACHI
            }
        }
    }
}
