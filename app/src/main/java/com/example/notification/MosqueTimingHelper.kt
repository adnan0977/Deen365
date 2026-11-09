package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.domain.MosquePrayerTiming
import com.example.domain.PrayerSchedule
import com.example.domain.PrayerTimeCalculator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Helper to manage manual local mosque / masjid timings,
 * per-prayer active mode toggles, and pre-alarm walking reminders.
 */
object MosqueTimingHelper {
    private const val TAG = "MosqueTimingHelper"
    private const val PREFS_NAME = "deen365_mosque_timings"

    const val CHANNEL_WALK_ID = "mosque_walking_channel"
    const val ACTION_MOSQUE_WALKING_ALERT = "com.example.deen365.ACTION_MOSQUE_WALKING_ALERT"
    const val EXTRA_MOSQUE_NAME = "extra_mosque_name"
    const val EXTRA_MINUTES_BEFORE = "extra_minutes_before"

    private const val KEY_MOSQUE_NAME = "mosque_name"
    private const val KEY_GLOBAL_MOSQUE_ACTIVE = "global_mosque_active"
    private const val PREFIX_TIME = "time_"
    private const val PREFIX_ACTIVE = "active_"
    private const val PREFIX_ALARM_MODE = "alarm_mode_"
    private const val PREFIX_PRE_ALARM = "pre_alarm_"

    private const val WALK_REQ_BASE = 3500

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getMosqueName(context: Context): String {
        return getPrefs(context).getString(KEY_MOSQUE_NAME, "My Local Masjid") ?: "My Local Masjid"
    }

    fun setMosqueName(context: Context, name: String) {
        getPrefs(context).edit().putString(KEY_MOSQUE_NAME, name.trim()).apply()
    }

    fun isGlobalMosqueActive(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_GLOBAL_MOSQUE_ACTIVE, false)
    }

    fun setGlobalMosqueActive(context: Context, active: Boolean) {
        val prefs = getPrefs(context)
        val editor = prefs.edit().putBoolean(KEY_GLOBAL_MOSQUE_ACTIVE, active)
        // Also toggle all prayers to match global state
        val prayers = listOf("fajr", "dhuhr", "asr", "maghrib", "isha", "jummah")
        for (p in prayers) {
            editor.putBoolean(PREFIX_ACTIVE + p, active)
        }
        editor.apply()
    }

    /**
     * Retrieves all mosque prayer timings populated with current schedule defaults.
     */
    fun getMosqueTimings(context: Context, schedule: PrayerSchedule): List<MosquePrayerTiming> {
        val prefs = getPrefs(context)
        val isGlobal = isGlobalMosqueActive(context)

        val prayerDefs = listOf(
            Triple("fajr", "Fajr", "الفجر") to (schedule.fajr.ifEmpty { "05:15 AM" } to "05:45 AM"),
            Triple("sunrise", "Sunrise / Ishraq", "الشروق") to (schedule.sunrise.ifEmpty { "06:30 AM" } to "06:45 AM"),
            Triple("dhuhr", "Dhuhr", "الظهر") to (schedule.dhuhr.ifEmpty { "12:30 PM" } to "01:30 PM"),
            Triple("asr", "Asr", "العصر") to (schedule.asr.ifEmpty { "04:15 PM" } to "05:00 PM"),
            Triple("maghrib", "Maghrib", "المغرب") to (schedule.maghrib.ifEmpty { "06:45 PM" } to "06:50 PM"),
            Triple("isha", "Isha", "العشاء") to (schedule.isha.ifEmpty { "08:15 PM" } to "08:45 PM"),
            Triple("jummah", "Jumu'ah", "الجمعة") to ("12:30 PM" to "01:30 PM")
        )

        return prayerDefs.map { (meta, times) ->
            val (id, name, arabic) = meta
            val (defCalc, defMosque) = times

            val savedManualTime = prefs.getString(PREFIX_TIME + id, defMosque) ?: defMosque
            val savedActive = prefs.getBoolean(PREFIX_ACTIVE + id, isGlobal)
            val savedPreAlarm = prefs.getInt(PREFIX_PRE_ALARM + id, if (id == "fajr" || id == "jummah") 15 else 10)
            val savedAlarmMode = prefs.getString(PREFIX_ALARM_MODE + id, AlertSoundMode.FULL_ADHAN.name)
            val soundMode = try {
                AlertSoundMode.valueOf(savedAlarmMode ?: AlertSoundMode.FULL_ADHAN.name)
            } catch (_: Exception) {
                AlertSoundMode.FULL_ADHAN
            }

            MosquePrayerTiming(
                prayerId = id,
                displayName = name,
                arabicName = arabic,
                defaultCalculatedTime = defCalc,
                manualMosqueTime = savedManualTime,
                isMosqueTimingActive = savedActive,
                isAlarmEnabled = true,
                alarmSoundMode = soundMode,
                preAlarmMinutes = savedPreAlarm
            )
        }
    }

    fun savePrayerTiming(
        context: Context,
        prayerId: String,
        manualTime: String,
        isActive: Boolean,
        preAlarmMinutes: Int,
        alarmSoundMode: AlertSoundMode = AlertSoundMode.FULL_ADHAN
    ) {
        getPrefs(context).edit()
            .putString(PREFIX_TIME + prayerId, manualTime.trim())
            .putBoolean(PREFIX_ACTIVE + prayerId, isActive)
            .putInt(PREFIX_PRE_ALARM + prayerId, preAlarmMinutes)
            .putString(PREFIX_ALARM_MODE + prayerId, alarmSoundMode.name)
            .apply()
    }

    /**
     * Schedules pre-alarm walking reminders for prayers that have mosque timing active
     * and preAlarmMinutes > 0.
     */
    fun scheduleMosqueWalkingAlarms(context: Context, timings: List<MosquePrayerTiming>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        createWalkingChannel(context)

        val mosqueName = getMosqueName(context)
        val now = System.currentTimeMillis()

        timings.forEachIndexed { index, timing ->
            val reqCode = WALK_REQ_BASE + index

            if (!timing.isMosqueTimingActive || timing.preAlarmMinutes <= 0) {
                // Cancel walking alarm
                val cancelIntent = Intent(context, PrayerNotificationReceiver::class.java).apply {
                    action = ACTION_MOSQUE_WALKING_ALERT
                }
                val pi = PendingIntent.getBroadcast(
                    context,
                    reqCode,
                    cancelIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
                )
                alarmManager.cancel(pi)
                return@forEachIndexed
            }

            // Parse time to millis
            var targetMillis = parseTimeToMillis(timing.manualMosqueTime)
            // Subtract pre-alarm walk minutes
            val walkOffsetMillis = timing.preAlarmMinutes * 60 * 1000L
            targetMillis -= walkOffsetMillis

            if (targetMillis <= now) {
                targetMillis += 24 * 60 * 60 * 1000L
            }

            val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
                action = ACTION_MOSQUE_WALKING_ALERT
                putExtra(PrayerNotificationHelper.EXTRA_PRAYER_NAME, timing.displayName)
                putExtra(EXTRA_MOSQUE_NAME, mosqueName)
                putExtra(PrayerNotificationHelper.EXTRA_PRAYER_TIME, timing.manualMosqueTime)
                putExtra(EXTRA_MINUTES_BEFORE, timing.preAlarmMinutes)
                putExtra(PrayerNotificationHelper.EXTRA_SOUND_MODE, timing.alarmSoundMode.name)
                putExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, reqCode)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reqCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
                    } else {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
                }
                Log.d(TAG, "Scheduled walk alarm for ${timing.displayName} in ${timing.preAlarmMinutes}m at $targetMillis")
            } catch (e: Throwable) {
                try {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
                } catch (_: Throwable) {}
                Log.e(TAG, "Failed to schedule walk alarm: ${e.message}")
            }
        }
    }

    private fun createWalkingChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                CHANNEL_WALK_ID,
                "Mosque Congregation Walking Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Gentle alerts 5-15 mins before Iqamah to prepare Wudu and walk to the masjid"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendMosqueWalkingNotification(
        context: Context,
        prayerName: String,
        mosqueName: String,
        iqamahTime: String,
        minutesBefore: Int,
        notificationId: Int
    ) {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_WALK_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🚶 Time to Walk to the Masjid ($prayerName)")
            .setContentText("Iqamah in $minutesBefore mins at $mosqueName ($iqamahTime). Make Wudu and earn congregation reward.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Iqamah begins in $minutesBefore minutes at $mosqueName ($iqamahTime).\n\n" +
                    "\"Whoever purifies himself in his house, then walks to one of the houses of Allah to fulfill an obligation... one step wipes out a sin and the other raises him a degree.\" (Sahih Muslim)"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    fun parseTimeToMillis(timeStr: String): Long {
        return try {
            val formats = listOf(
                SimpleDateFormat("hh:mm a", Locale.US),
                SimpleDateFormat("h:mm a", Locale.US),
                SimpleDateFormat("HH:mm", Locale.US)
            )
            val nowCal = Calendar.getInstance()
            for (f in formats) {
                try {
                    val parsed = f.parse(timeStr.trim())
                    if (parsed != null) {
                        val parsedCal = Calendar.getInstance().apply { time = parsed }
                        return Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, parsedCal.get(Calendar.HOUR_OF_DAY))
                            set(Calendar.MINUTE, parsedCal.get(Calendar.MINUTE))
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                    }
                } catch (_: Exception) {}
            }
            nowCal.timeInMillis
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }

    /**
     * Dispatches an immediate test notification for a configured mosque prayer alarm.
     */
    fun triggerInstantTestMosqueAlarm(
        context: Context,
        prayerId: String = "fajr"
    ) {
        val schedule = PrayerTimeCalculator.calculateSchedule(PrayerTimeCalculator.POPULAR_CITIES[0])
        val timings = getMosqueTimings(context, schedule)
        val timing = timings.find { it.prayerId.equals(prayerId, ignoreCase = true) } ?: timings.first()
        val mosqueName = getMosqueName(context)

        PrayerNotificationHelper.sendPrayerNotification(
            context = context,
            prayerName = "${timing.displayName} (Masjid Iqamah)",
            cityName = mosqueName,
            prayerTime = timing.manualMosqueTime,
            offsetMinutes = 0,
            soundMode = timing.alarmSoundMode,
            notificationId = 9888 + timing.prayerId.hashCode() % 100
        )
    }
}
