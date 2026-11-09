package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.example.data.local.DeenDatabase
import com.example.data.local.FastingRecordEntity
import com.example.data.local.PrayerRecordEntity
import com.example.domain.PrayerTimeCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrayerNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        when (action) {
            PrayerNotificationHelper.ACTION_PRAYER_ALERT -> {
                val prayerName = intent.getStringExtra(PrayerNotificationHelper.EXTRA_PRAYER_NAME) ?: "Prayer"
                val cityName = intent.getStringExtra(PrayerNotificationHelper.EXTRA_CITY_NAME) ?: "your location"
                val prayerTime = intent.getStringExtra(PrayerNotificationHelper.EXTRA_PRAYER_TIME) ?: ""
                val offsetMinutes = intent.getIntExtra(PrayerNotificationHelper.EXTRA_OFFSET_MINUTES, 0)
                val soundModeStr = intent.getStringExtra(PrayerNotificationHelper.EXTRA_SOUND_MODE) ?: AlertSoundMode.FULL_ADHAN.name
                val notificationId = intent.getIntExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, prayerName.hashCode())

                val soundMode = try {
                    AlertSoundMode.valueOf(soundModeStr)
                } catch (_: Exception) {
                    AlertSoundMode.FULL_ADHAN
                }

                PrayerNotificationHelper.sendPrayerNotification(
                    context = context,
                    prayerName = prayerName,
                    cityName = cityName,
                    prayerTime = prayerTime,
                    offsetMinutes = offsetMinutes,
                    soundMode = soundMode,
                    notificationId = notificationId
                )

                // Reschedule for next day (24 hours later) so the schedule never expires
                rescheduleNextOccurrence(context, prayerName, cityName, prayerTime, offsetMinutes, soundMode, notificationId)
            }

            MosqueTimingHelper.ACTION_MOSQUE_WALKING_ALERT -> {
                val prayerName = intent.getStringExtra(PrayerNotificationHelper.EXTRA_PRAYER_NAME) ?: "Prayer"
                val mosqueName = intent.getStringExtra(MosqueTimingHelper.EXTRA_MOSQUE_NAME) ?: "My Local Masjid"
                val prayerTime = intent.getStringExtra(PrayerNotificationHelper.EXTRA_PRAYER_TIME) ?: ""
                val minutesBefore = intent.getIntExtra(MosqueTimingHelper.EXTRA_MINUTES_BEFORE, 15)
                val notificationId = intent.getIntExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, 3600)

                MosqueTimingHelper.sendMosqueWalkingNotification(
                    context = context,
                    prayerName = prayerName,
                    mosqueName = mosqueName,
                    iqamahTime = prayerTime,
                    minutesBefore = minutesBefore,
                    notificationId = notificationId
                )
            }

            PrayerNotificationHelper.ACTION_MARK_PRAYED -> {
                val prayerName = intent.getStringExtra(PrayerNotificationHelper.EXTRA_PRAYER_NAME) ?: return
                val notificationId = intent.getIntExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, prayerName.hashCode())

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = DeenDatabase.getDatabase(context)
                        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                        val existing = db.trackerDao().getPrayerRecordSync(todayStr)
                            ?: PrayerRecordEntity(dateStr = todayStr)

                        val updated = when (prayerName.lowercase().trim()) {
                            "fajr" -> existing.copy(fajr = true, updatedAt = System.currentTimeMillis())
                            "dhuhr" -> existing.copy(dhuhr = true, updatedAt = System.currentTimeMillis())
                            "asr" -> existing.copy(asr = true, updatedAt = System.currentTimeMillis())
                            "maghrib" -> existing.copy(maghrib = true, updatedAt = System.currentTimeMillis())
                            "isha" -> existing.copy(isha = true, updatedAt = System.currentTimeMillis())
                            else -> existing
                        }

                        db.trackerDao().insertOrUpdatePrayerRecord(updated)

                        // Cancel prayer alert notification
                        val manager = NotificationManagerCompat.from(context)
                        manager.cancel(notificationId)

                        // Post confirmation update
                        PrayerNotificationHelper.sendPrayerMarkedConfirmation(
                            context = context,
                            prayerName = prayerName,
                            completedCount = updated.completedCount,
                            notificationId = notificationId
                        )
                    } catch (_: Exception) {
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            PrayerNotificationHelper.ACTION_SNOOZE_PRAYER -> {
                val prayerName = intent.getStringExtra(PrayerNotificationHelper.EXTRA_PRAYER_NAME) ?: "Prayer"
                val cityName = intent.getStringExtra(PrayerNotificationHelper.EXTRA_CITY_NAME) ?: "your location"
                val prayerTime = intent.getStringExtra(PrayerNotificationHelper.EXTRA_PRAYER_TIME) ?: ""
                val notificationId = intent.getIntExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, prayerName.hashCode())

                // Cancel current notification
                val manager = NotificationManagerCompat.from(context)
                manager.cancel(notificationId)

                // Schedule alarm for 10 minutes from now
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
                val snoozeMillis = System.currentTimeMillis() + 10 * 60 * 1000L

                val alertIntent = Intent(context, PrayerNotificationReceiver::class.java).apply {
                    this.action = PrayerNotificationHelper.ACTION_PRAYER_ALERT
                    putExtra(PrayerNotificationHelper.EXTRA_PRAYER_NAME, prayerName)
                    putExtra(PrayerNotificationHelper.EXTRA_CITY_NAME, cityName)
                    putExtra(PrayerNotificationHelper.EXTRA_PRAYER_TIME, prayerTime)
                    putExtra(PrayerNotificationHelper.EXTRA_OFFSET_MINUTES, 0)
                    putExtra(PrayerNotificationHelper.EXTRA_SOUND_MODE, AlertSoundMode.SOFT_CHIME.name)
                    putExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, notificationId)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId + 300,
                    alertIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
                )

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeMillis, pendingIntent)
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeMillis, pendingIntent)
                    }
                } catch (_: Exception) {}
            }

            PrayerNotificationHelper.ACTION_FASTING_REMINDER -> {
                val cityName = intent.getStringExtra(PrayerNotificationHelper.EXTRA_CITY_NAME) ?: "your location"
                val sunsetTime = intent.getStringExtra(PrayerNotificationHelper.EXTRA_PRAYER_TIME) ?: ""
                val notificationId = intent.getIntExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, PrayerNotificationHelper.FASTING_REQ_CODE)

                PrayerNotificationHelper.sendFastingCompletionNotification(
                    context = context,
                    cityName = cityName,
                    sunsetTime = sunsetTime,
                    notificationId = notificationId
                )

                // Reschedule for next day (24 hours later)
                rescheduleNextFastingOccurrence(context, cityName, sunsetTime, notificationId)
            }

            PrayerNotificationHelper.ACTION_LOG_FAST_COMPLETED -> {
                val notificationId = intent.getIntExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, PrayerNotificationHelper.FASTING_REQ_CODE)

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = DeenDatabase.getDatabase(context)
                        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                        val existing = db.trackerDao().getFastingRecordSync(todayStr)

                        val updated = FastingRecordEntity(
                            dateStr = todayStr,
                            isFasted = true,
                            fastingType = existing?.fastingType ?: "Ramadan",
                            isQadha = existing?.isQadha ?: false
                        )
                        db.trackerDao().insertOrUpdateFastingRecord(updated)

                        val totalFasted = db.trackerDao().getTotalFastedDaysCountSync()

                        // Cancel the reminder notification
                        val manager = NotificationManagerCompat.from(context)
                        manager.cancel(notificationId)

                        // Post confirmation update
                        PrayerNotificationHelper.sendFastingLoggedConfirmation(
                            context = context,
                            totalCompletedDays = totalFasted,
                            notificationId = notificationId
                        )
                    } catch (_: Exception) {
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            PrayerNotificationHelper.ACTION_QURAN_REMINDER -> {
                val pagesTarget = intent.getIntExtra(PrayerNotificationHelper.EXTRA_PAGES_TARGET, 4)
                val notificationId = intent.getIntExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, PrayerNotificationHelper.QURAN_REMINDER_REQ_CODE)

                PrayerNotificationHelper.sendQuranReminderNotification(
                    context = context,
                    pagesTarget = pagesTarget,
                    notificationId = notificationId
                )

                // Reschedule for next day (24 hours later)
                rescheduleNextQuranOccurrence(context, pagesTarget, notificationId)
            }

            PrayerNotificationHelper.ACTION_FRIDAY_KAHF_REMINDER -> {
                val notificationId = intent.getIntExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, PrayerNotificationHelper.QURAN_KAHF_REQ_CODE)

                PrayerNotificationHelper.sendFridayKahfNotification(
                    context = context,
                    notificationId = notificationId
                )

                // Reschedule for next Friday (7 days later)
                rescheduleNextFridayKahfOccurrence(context, notificationId)
            }

            PrayerNotificationHelper.ACTION_MARK_QURAN_READ -> {
                val pagesTarget = intent.getIntExtra(PrayerNotificationHelper.EXTRA_PAGES_TARGET, 4)
                val notificationId = intent.getIntExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, PrayerNotificationHelper.QURAN_REMINDER_REQ_CODE)

                // Cancel the notification
                val manager = NotificationManagerCompat.from(context)
                manager.cancel(notificationId)

                // Post confirmation
                PrayerNotificationHelper.sendQuranReadConfirmation(
                    context = context,
                    pagesAdded = pagesTarget,
                    notificationId = notificationId
                )
            }

            PrayerNotificationHelper.ACTION_SNOOZE_QURAN -> {
                val pagesTarget = intent.getIntExtra(PrayerNotificationHelper.EXTRA_PAGES_TARGET, 4)
                val notificationId = intent.getIntExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, PrayerNotificationHelper.QURAN_REMINDER_REQ_CODE)

                val manager = NotificationManagerCompat.from(context)
                manager.cancel(notificationId)

                // Snooze for 15 minutes
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                if (alarmManager != null) {
                    val snoozeMillis = System.currentTimeMillis() + 15 * 60 * 1000L
                    val alertIntent = Intent(context, PrayerNotificationReceiver::class.java).apply {
                        this.action = PrayerNotificationHelper.ACTION_QURAN_REMINDER
                        putExtra(PrayerNotificationHelper.EXTRA_PAGES_TARGET, pagesTarget)
                        putExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, notificationId)
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        notificationId + 50,
                        alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
                    )

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeMillis, pendingIntent)
                        } else {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeMillis, pendingIntent)
                        }
                    } catch (_: Exception) {}
                }
            }

            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            "com.example.deen365.ACTION_RESCHEDULE_ALL" -> {
                // Device rebooted or time zone shifted: recalculate schedule from location and restore alarms
                PrayerNotificationSchedulerService.startScheduling(context, forceGps = true)
                PrayerNotificationHelper.scheduleDailyQuranReminder(context)
            }
        }
    }

    private fun rescheduleNextOccurrence(
        context: Context,
        prayerName: String,
        cityName: String,
        prayerTime: String,
        offsetMinutes: Int,
        soundMode: AlertSoundMode,
        notificationId: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val nextDayMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000L

        val nextIntent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = PrayerNotificationHelper.ACTION_PRAYER_ALERT
            putExtra(PrayerNotificationHelper.EXTRA_PRAYER_NAME, prayerName)
            putExtra(PrayerNotificationHelper.EXTRA_CITY_NAME, cityName)
            putExtra(PrayerNotificationHelper.EXTRA_PRAYER_TIME, prayerTime)
            putExtra(PrayerNotificationHelper.EXTRA_OFFSET_MINUTES, offsetMinutes)
            putExtra(PrayerNotificationHelper.EXTRA_SOUND_MODE, soundMode.name)
            putExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, notificationId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextDayMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextDayMillis, pendingIntent)
            }
        } catch (_: Exception) {}
    }

    private fun rescheduleNextFastingOccurrence(
        context: Context,
        cityName: String,
        sunsetTime: String,
        notificationId: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val nextDayMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000L

        val nextIntent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = PrayerNotificationHelper.ACTION_FASTING_REMINDER
            putExtra(PrayerNotificationHelper.EXTRA_CITY_NAME, cityName)
            putExtra(PrayerNotificationHelper.EXTRA_PRAYER_TIME, sunsetTime)
            putExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, notificationId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextDayMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextDayMillis, pendingIntent)
            }
        } catch (_: Exception) {}
    }

    private fun rescheduleNextQuranOccurrence(
        context: Context,
        pagesTarget: Int,
        notificationId: Int
    ) {
        val settings = PrayerNotificationHelper.getQuranReminderSettings(context)
        if (!settings.enabled) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val nextDayMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000L

        val nextIntent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = PrayerNotificationHelper.ACTION_QURAN_REMINDER
            putExtra(PrayerNotificationHelper.EXTRA_PAGES_TARGET, pagesTarget)
            putExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, notificationId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextDayMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextDayMillis, pendingIntent)
            }
        } catch (_: Exception) {}
    }

    private fun rescheduleNextFridayKahfOccurrence(
        context: Context,
        notificationId: Int
    ) {
        val settings = PrayerNotificationHelper.getQuranReminderSettings(context)
        if (!settings.enabled || !settings.notifyFridayKahf) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val nextWeekMillis = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000L

        val nextIntent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = PrayerNotificationHelper.ACTION_FRIDAY_KAHF_REMINDER
            putExtra(PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, notificationId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextWeekMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextWeekMillis, pendingIntent)
            }
        } catch (_: Exception) {}
    }
}
