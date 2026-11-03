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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.domain.PrayerSchedule
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AlertSoundMode(val title: String, val description: String) {
    FULL_ADHAN("Adhan Call", "Plays the melodic Adhan sound"),
    SOFT_CHIME("Soft Chime", "Gentle bell and tone chime"),
    SILENT_VIBRATION("Silent & Vibrate", "Vibrate device only, no sound")
}

data class PrayerNotificationSettings(
    val alertsEnabled: Boolean = true,
    val notifyFajr: Boolean = true,
    val notifySunrise: Boolean = false,
    val notifyDhuhr: Boolean = true,
    val notifyAsr: Boolean = true,
    val notifyMaghrib: Boolean = true,
    val notifyIsha: Boolean = true,
    val notifySunsetFastingReminder: Boolean = true,
    val reminderOffsetMinutes: Int = 0, // 0 = at prayer time, 5 = 5m before, 10 = 10m before, 15 = 15m before
    val soundMode: AlertSoundMode = AlertSoundMode.FULL_ADHAN
)

data class NextAlarmInfo(
    val prayerName: String,
    val formattedTime: String,
    val targetTimestamp: Long,
    val remainingFormatted: String
)

object PrayerNotificationHelper {
    const val CHANNEL_ID = "prayer_alerts_channel"
    const val CHANNEL_CONFIRMATION_ID = "prayer_confirmation_channel"
    const val CHANNEL_FASTING_ID = "fasting_completion_channel"

    const val ACTION_PRAYER_ALERT = "com.example.deen365.ACTION_PRAYER_ALERT"
    const val ACTION_MARK_PRAYED = "com.example.deen365.ACTION_MARK_PRAYED"
    const val ACTION_SNOOZE_PRAYER = "com.example.deen365.ACTION_SNOOZE_PRAYER"
    const val ACTION_FASTING_REMINDER = "com.example.deen365.ACTION_FASTING_REMINDER"
    const val ACTION_LOG_FAST_COMPLETED = "com.example.deen365.ACTION_LOG_FAST_COMPLETED"

    const val EXTRA_PRAYER_NAME = "extra_prayer_name"
    const val EXTRA_CITY_NAME = "extra_city_name"
    const val EXTRA_PRAYER_TIME = "extra_prayer_time"
    const val EXTRA_OFFSET_MINUTES = "extra_offset_minutes"
    const val EXTRA_SOUND_MODE = "extra_sound_mode"
    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    private const val PREFS_NAME = "deen365_prayer_notifs"
    private const val KEY_ALERTS_ENABLED = "alerts_enabled"
    private const val KEY_NOTIFY_FAJR = "notify_fajr"
    private const val KEY_NOTIFY_SUNRISE = "notify_sunrise"
    private const val KEY_NOTIFY_DHUHR = "notify_dhuhr"
    private const val KEY_NOTIFY_ASR = "notify_asr"
    private const val KEY_NOTIFY_MAGHRIB = "notify_maghrib"
    private const val KEY_NOTIFY_ISHA = "notify_isha"
    private const val KEY_NOTIFY_SUNSET_FASTING = "notify_sunset_fasting"
    private const val KEY_OFFSET_MINUTES = "offset_minutes"
    private const val KEY_SOUND_MODE = "sound_mode"

    private const val PRAYER_REQ_BASE = 2000
    const val FASTING_REQ_CODE = 2500

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSettings(context: Context): PrayerNotificationSettings {
        val prefs = getPrefs(context)
        val soundModeStr = prefs.getString(KEY_SOUND_MODE, AlertSoundMode.FULL_ADHAN.name) ?: AlertSoundMode.FULL_ADHAN.name
        val soundMode = try {
            AlertSoundMode.valueOf(soundModeStr)
        } catch (_: Exception) {
            AlertSoundMode.FULL_ADHAN
        }

        return PrayerNotificationSettings(
            alertsEnabled = prefs.getBoolean(KEY_ALERTS_ENABLED, true),
            notifyFajr = prefs.getBoolean(KEY_NOTIFY_FAJR, true),
            notifySunrise = prefs.getBoolean(KEY_NOTIFY_SUNRISE, false),
            notifyDhuhr = prefs.getBoolean(KEY_NOTIFY_DHUHR, true),
            notifyAsr = prefs.getBoolean(KEY_NOTIFY_ASR, true),
            notifyMaghrib = prefs.getBoolean(KEY_NOTIFY_MAGHRIB, true),
            notifyIsha = prefs.getBoolean(KEY_NOTIFY_ISHA, true),
            notifySunsetFastingReminder = prefs.getBoolean(KEY_NOTIFY_SUNSET_FASTING, true),
            reminderOffsetMinutes = prefs.getInt(KEY_OFFSET_MINUTES, 0),
            soundMode = soundMode
        )
    }

    fun saveSettings(context: Context, settings: PrayerNotificationSettings) {
        getPrefs(context).edit()
            .putBoolean(KEY_ALERTS_ENABLED, settings.alertsEnabled)
            .putBoolean(KEY_NOTIFY_FAJR, settings.notifyFajr)
            .putBoolean(KEY_NOTIFY_SUNRISE, settings.notifySunrise)
            .putBoolean(KEY_NOTIFY_DHUHR, settings.notifyDhuhr)
            .putBoolean(KEY_NOTIFY_ASR, settings.notifyAsr)
            .putBoolean(KEY_NOTIFY_MAGHRIB, settings.notifyMaghrib)
            .putBoolean(KEY_NOTIFY_ISHA, settings.notifyIsha)
            .putBoolean(KEY_NOTIFY_SUNSET_FASTING, settings.notifySunsetFastingReminder)
            .putInt(KEY_OFFSET_MINUTES, settings.reminderOffsetMinutes)
            .putString(KEY_SOUND_MODE, settings.soundMode.name)
            .apply()
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Main Prayer Alerts Channel
            val name = "Prayer Time Alerts (Adhan)"
            val descriptionText = "Notifications for the daily obligatory prayers based on your schedule"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build()

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                setSound(defaultSoundUri, audioAttributes)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)

            // Confirmation Channel
            val confirmChannel = NotificationChannel(
                CHANNEL_CONFIRMATION_ID,
                "Prayer Attendance Confirmations",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Updates when you log or confirm prayers"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(confirmChannel)

            // Sunset Fasting Completion Channel
            val fastingChannel = NotificationChannel(
                CHANNEL_FASTING_ID,
                "Sunset Fast Completion Reminders (Iftar)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily push reminders at sunset to recite Iftar Dua and record completed fast"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400)
                setSound(defaultSoundUri, audioAttributes)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(fastingChannel)
        }
    }

    /**
     * Schedules alarms for each enabled prayer based on the daily calculated schedule.
     */
    fun scheduleDailyPrayerAlarms(
        context: Context,
        schedule: PrayerSchedule,
        settings: PrayerNotificationSettings = getSettings(context)
    ) {
        createNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        if (!settings.alertsEnabled) {
            cancelAllPrayerAlarms(context)
            return
        }

        val prayerEntries = listOf(
            PrayerEntry("Fajr", schedule.fajr, schedule.fajrTimestamp, settings.notifyFajr, 0),
            PrayerEntry("Sunrise", schedule.sunrise, schedule.sunriseTimestamp, settings.notifySunrise, 1),
            PrayerEntry("Dhuhr", schedule.dhuhr, schedule.dhuhrTimestamp, settings.notifyDhuhr, 2),
            PrayerEntry("Asr", schedule.asr, schedule.asrTimestamp, settings.notifyAsr, 3),
            PrayerEntry("Maghrib", schedule.maghrib, schedule.maghribTimestamp, settings.notifyMaghrib, 4),
            PrayerEntry("Isha", schedule.isha, schedule.ishaTimestamp, settings.notifyIsha, 5)
        )

        val mosqueTimings = MosqueTimingHelper.getMosqueTimings(context, schedule)

        val now = System.currentTimeMillis()
        val offsetMillis = settings.reminderOffsetMinutes * 60 * 1000L

        for (entry in prayerEntries) {
            val requestCode = PRAYER_REQ_BASE + entry.index

            if (!entry.isEnabled) {
                // Cancel if disabled
                cancelPrayerAlarm(context, alarmManager, requestCode, entry.name)
                continue
            }

            val mosqueTiming = mosqueTimings.find { it.prayerId.equals(entry.name, ignoreCase = true) }
            val isUsingMosque = mosqueTiming != null && mosqueTiming.isMosqueTimingActive
            val prayerTimeStr = if (isUsingMosque) mosqueTiming!!.manualMosqueTime else entry.timeStr
            val effectiveSoundMode = if (isUsingMosque) mosqueTiming!!.alarmSoundMode else settings.soundMode

            var targetMillis = if (isUsingMosque) {
                MosqueTimingHelper.parseTimeToMillis(prayerTimeStr) - offsetMillis
            } else if (entry.timestamp > 0L) {
                entry.timestamp - offsetMillis
            } else {
                parsePrayerTimeToMillis(entry.timeStr) - offsetMillis
            }

            // If time has already passed today, schedule for tomorrow
            if (targetMillis <= now) {
                targetMillis += 24 * 60 * 60 * 1000L
            }

            val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
                action = ACTION_PRAYER_ALERT
                putExtra(EXTRA_PRAYER_NAME, if (isUsingMosque) "${entry.name} (Masjid Iqamah)" else entry.name)
                putExtra(EXTRA_CITY_NAME, if (isUsingMosque) MosqueTimingHelper.getMosqueName(context) else schedule.cityName)
                putExtra(EXTRA_PRAYER_TIME, prayerTimeStr)
                putExtra(EXTRA_OFFSET_MINUTES, settings.reminderOffsetMinutes)
                putExtra(EXTRA_SOUND_MODE, effectiveSoundMode.name)
                putExtra(EXTRA_NOTIFICATION_ID, requestCode)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )

            scheduleAlarmExact(alarmManager, targetMillis, pendingIntent)
        }

        // Schedule pre-alarm walking reminders for enabled mosque timings
        MosqueTimingHelper.scheduleMosqueWalkingAlarms(context, mosqueTimings)

        // Schedule Sunset Fasting Completion Reminder dynamically using prayer time data (Maghrib/Iftar)
        if (settings.notifySunsetFastingReminder) {
            val sunsetTimeStr = schedule.iftar.ifEmpty { schedule.maghrib }
            var targetSunsetMillis = if (schedule.maghribTimestamp > 0L) {
                schedule.maghribTimestamp
            } else {
                parsePrayerTimeToMillis(sunsetTimeStr)
            }

            if (targetSunsetMillis <= now) {
                targetSunsetMillis += 24 * 60 * 60 * 1000L
            }

            val fastingIntent = Intent(context, PrayerNotificationReceiver::class.java).apply {
                action = ACTION_FASTING_REMINDER
                putExtra(EXTRA_CITY_NAME, schedule.cityName)
                putExtra(EXTRA_PRAYER_TIME, sunsetTimeStr)
                putExtra(EXTRA_NOTIFICATION_ID, FASTING_REQ_CODE)
            }

            val fastingPendingIntent = PendingIntent.getBroadcast(
                context,
                FASTING_REQ_CODE,
                fastingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )

            scheduleAlarmExact(alarmManager, targetSunsetMillis, fastingPendingIntent)
        } else {
            cancelFastingAlarm(context, alarmManager)
        }
    }

    fun cancelAllPrayerAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val names = listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha")
        for ((index, name) in names.withIndex()) {
            cancelPrayerAlarm(context, alarmManager, PRAYER_REQ_BASE + index, name)
        }
        cancelFastingAlarm(context, alarmManager)
    }

    fun cancelFastingAlarm(context: Context, alarmManager: AlarmManager? = null) {
        val am = alarmManager ?: (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager) ?: return
        val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = ACTION_FASTING_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            FASTING_REQ_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        if (pendingIntent != null) {
            am.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun scheduleAlarmExact(alarmManager: AlarmManager, targetMillis: Long, pendingIntent: PendingIntent) {
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
        } catch (_: SecurityException) {
            try {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
            } catch (_: Exception) {}
        } catch (_: Exception) {}
    }

    private fun cancelPrayerAlarm(context: Context, alarmManager: AlarmManager, requestCode: Int, prayerName: String) {
        val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = ACTION_PRAYER_ALERT
            putExtra(EXTRA_PRAYER_NAME, prayerName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun getNextScheduledAlarmInfo(
        context: Context,
        schedule: PrayerSchedule,
        settings: PrayerNotificationSettings = getSettings(context)
    ): NextAlarmInfo? {
        if (!settings.alertsEnabled) return null

        val now = System.currentTimeMillis()
        val offsetMillis = settings.reminderOffsetMinutes * 60 * 1000L

        val prayerEntries = listOf(
            PrayerEntry("Fajr", schedule.fajr, schedule.fajrTimestamp, settings.notifyFajr, 0),
            PrayerEntry("Sunrise", schedule.sunrise, schedule.sunriseTimestamp, settings.notifySunrise, 1),
            PrayerEntry("Dhuhr", schedule.dhuhr, schedule.dhuhrTimestamp, settings.notifyDhuhr, 2),
            PrayerEntry("Asr", schedule.asr, schedule.asrTimestamp, settings.notifyAsr, 3),
            PrayerEntry("Maghrib", schedule.maghrib, schedule.maghribTimestamp, settings.notifyMaghrib, 4),
            PrayerEntry("Isha", schedule.isha, schedule.ishaTimestamp, settings.notifyIsha, 5)
        )

        var closestEntry: PrayerEntry? = null
        var closestTarget = Long.MAX_VALUE

        for (entry in prayerEntries) {
            if (!entry.isEnabled) continue
            var target = if (entry.timestamp > 0L) {
                entry.timestamp - offsetMillis
            } else {
                parsePrayerTimeToMillis(entry.timeStr) - offsetMillis
            }
            if (target <= now) {
                target += 24 * 60 * 60 * 1000L
            }
            if (target < closestTarget) {
                closestTarget = target
                closestEntry = entry
            }
        }

        if (closestEntry == null || closestTarget == Long.MAX_VALUE) return null

        val diffMinutes = ((closestTarget - now) / 60000L).coerceAtLeast(0)
        val hours = diffMinutes / 60
        val mins = diffMinutes % 60
        val remainingStr = if (hours > 0) "in ${hours}h ${mins}m" else "in ${mins}m"

        return NextAlarmInfo(
            prayerName = closestEntry.name,
            formattedTime = closestEntry.timeStr,
            targetTimestamp = closestTarget,
            remainingFormatted = remainingStr
        )
    }

    fun sendPrayerNotification(
        context: Context,
        prayerName: String,
        cityName: String,
        prayerTime: String,
        offsetMinutes: Int = 0,
        soundMode: AlertSoundMode = AlertSoundMode.FULL_ADHAN,
        notificationId: Int = prayerName.hashCode()
    ) {
        createNotificationChannel(context)

        // Intent to open App on Namaz tab
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_TAB", "NAMAZ")
            putExtra("EXTRA_PRAYER", prayerName)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        // Action 1: "Mark Prayed"
        val markPrayedIntent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = ACTION_MARK_PRAYED
            putExtra(EXTRA_PRAYER_NAME, prayerName)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            setPackage(context.packageName)
        }
        val markPrayedPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 100,
            markPrayedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        // Action 2: "Snooze 10m"
        val snoozeIntent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = ACTION_SNOOZE_PRAYER
            putExtra(EXTRA_PRAYER_NAME, prayerName)
            putExtra(EXTRA_CITY_NAME, cityName)
            putExtra(EXTRA_PRAYER_TIME, prayerTime)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            setPackage(context.packageName)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 200,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val hadithQuote = when (prayerName.lowercase()) {
            "fajr" -> "\"Whoever prays the dawn prayer is under the protection of Allah.\" (Sahih Muslim)"
            "sunrise" -> "\"Whoever prays Fajr in congregation then sits remembering Allah until sunrise, then prays two rakats, has the reward of a complete Hajj and Umrah.\" (At-Tirmidhi)"
            "dhuhr" -> "\"This is an hour when the gates of heaven are opened, and I wish that a good deed of mine ascends.\" (At-Tirmidhi)"
            "asr" -> "\"Whoever misses the Asr prayer, it is as if his family and wealth were lost.\" (Sahih al-Bukhari)"
            "maghrib" -> "\"Recite your Maghrib prayer while the twilight is clear; supplications are answered at dusk.\" (Ahmad)"
            "isha" -> "\"Whoever prays Isha in congregation, it is as if he spent half the night in prayer.\" (Sahih Muslim)"
            else -> "\"Verily, Salah has been enjoined on the believers at fixed times.\" (Surah An-Nisa 4:103)"
        }

        val titleText = if (offsetMinutes > 0) {
            "⏳ $offsetMinutes min until $prayerName Prayer ($prayerTime)"
        } else {
            "🕌 Time for $prayerName Prayer ($prayerTime)"
        }

        val bodyText = if (offsetMinutes > 0) {
            "Adhan for $prayerName in $cityName will be in $offsetMinutes minutes ($prayerTime)."
        } else {
            "It is now time for the $prayerName prayer in $cityName ($prayerTime)."
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titleText)
            .setContentText("$bodyText Tap to open Salah Guide.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$bodyText\n\n$hadithQuote\n\nMay Allah accept your prayer and devotion.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher, "✅ Mark Prayed", markPrayedPendingIntent)
            .addAction(R.mipmap.ic_launcher, "⏰ Snooze 10m", snoozePendingIntent)

        if (soundMode == AlertSoundMode.SILENT_VIBRATION) {
            builder.setSilent(true)
            builder.setVibrate(longArrayOf(0, 500, 200, 500))
        }

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Notifications permission not granted yet
        }
    }

    fun sendPrayerMarkedConfirmation(
        context: Context,
        prayerName: String,
        completedCount: Int,
        notificationId: Int
    ) {
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_CONFIRMATION_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("✅ $prayerName Recorded")
            .setContentText("Marked as offered. You've completed $completedCount of 5 prayers today. May Allah accept!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationId, builder.build())
        } catch (_: Exception) {}
    }

    /**
     * Dispatches daily push notification at sunset reminding user to break fast & log fast completion.
     */
    fun sendFastingCompletionNotification(
        context: Context,
        cityName: String,
        sunsetTime: String,
        notificationId: Int = FASTING_REQ_CODE
    ) {
        createNotificationChannel(context)

        // Intent to open App on Fasting screen
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_TAB", "FASTING")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        // Action: "Log Fast Completed" (Marks fast directly from push notification)
        val logFastIntent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = ACTION_LOG_FAST_COMPLETED
            putExtra(EXTRA_CITY_NAME, cityName)
            putExtra(EXTRA_PRAYER_TIME, sunsetTime)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            setPackage(context.packageName)
        }
        val logFastPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 10,
            logFastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val iftarDuaArabic = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ"
        val iftarDuaTrans = "Dhahaba adh-dhama'u wabtallat al-'uruq wa thabata al-ajru in sha' Allah"
        val iftarDuaMeaning = "The thirst has gone, the veins are moistened, and the reward is confirmed, if Allah wills."

        val title = "🌙 Sunset (Iftar) • Log Your Fast"
        val shortBody = "Sunset has arrived in $cityName ($sunsetTime). Break your fast & tap to log Sawm!"
        val expandedBody = "Sunset time in $cityName is $sunsetTime (Iftar).\n\n" +
                "Iftar Dua:\n$iftarDuaArabic\n\"$iftarDuaTrans\"\n($iftarDuaMeaning)\n\n" +
                "May Allah accept your fasting, prayers, and good deeds today."

        val builder = NotificationCompat.Builder(context, CHANNEL_FASTING_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(shortBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedBody))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher, "✅ Log Fast Completed", logFastPendingIntent)
            .addAction(R.mipmap.ic_launcher, "📖 Iftar Dua & Guide", pendingIntent)

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Notification permission not granted
        }
    }

    /**
     * Confirmation notification sent after the user logs their fast via the push notification action.
     */
    fun sendFastingLoggedConfirmation(
        context: Context,
        totalCompletedDays: Int,
        notificationId: Int = FASTING_REQ_CODE
    ) {
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_TAB", "FASTING")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId + 50,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_CONFIRMATION_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("🎉 Alhamdulillah! Fast Logged")
            .setContentText("Today's fast recorded successfully. Total: $totalCompletedDays days completed.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Alhamdulillah! Your fast for today has been logged.\n" +
                            "Completed: $totalCompletedDays days in the month.\n" +
                            "May Allah accept your fasting and grant you abundant reward."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationId, builder.build())
        } catch (_: Exception) {}
    }

    fun triggerInstantTestFastingNotification(
        context: Context,
        cityName: String = "Your Location",
        sunsetTime: String? = null
    ) {
        val currentTimeFormatted = sunsetTime ?: SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        sendFastingCompletionNotification(
            context = context,
            cityName = cityName,
            sunsetTime = currentTimeFormatted,
            notificationId = FASTING_REQ_CODE
        )
    }

    fun triggerInstantTestPrayerNotification(
        context: Context,
        prayerName: String = "Asr",
        cityName: String = "Your Location"
    ) {
        val currentTimeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val settings = getSettings(context)
        sendPrayerNotification(
            context = context,
            prayerName = prayerName,
            cityName = cityName,
            prayerTime = currentTimeFormatted,
            offsetMinutes = settings.reminderOffsetMinutes,
            soundMode = settings.soundMode,
            notificationId = 9999
        )
    }

    private fun parsePrayerTimeToMillis(timeStr: String): Long {
        return try {
            val parser = SimpleDateFormat("hh:mm a", Locale.US)
            val parsedDate = parser.parse(timeStr.trim()) ?: return System.currentTimeMillis()

            val calendar = Calendar.getInstance().apply {
                val parsedCal = Calendar.getInstance().apply { time = parsedDate }
                set(Calendar.HOUR_OF_DAY, parsedCal.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, parsedCal.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            calendar.timeInMillis
        } catch (_: Exception) {
            System.currentTimeMillis() + 60000L
        }
    }

    private data class PrayerEntry(
        val name: String,
        val timeStr: String,
        val timestamp: Long,
        val isEnabled: Boolean,
        val index: Int
    )
}
