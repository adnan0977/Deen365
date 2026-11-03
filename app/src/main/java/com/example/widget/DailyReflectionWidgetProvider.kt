package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.DeenDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyReflectionWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        CoroutineScope(Dispatchers.IO).launch {
            val db = DeenDatabase.getDatabase(context)
            val reflection = db.dailyReflectionDao().getLatestReflectionSync()

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_daily_reflection)

                if (reflection != null) {
                    views.setTextViewText(R.id.widget_reference, "${reflection.surahName} (${reflection.surahNumber}:${reflection.ayahNumber})")
                    views.setTextViewText(R.id.widget_arabic_text, reflection.arabicText)
                    views.setTextViewText(R.id.widget_translation, "\"${reflection.translation}\"")
                } else {
                    views.setTextViewText(R.id.widget_reference, "Surah Ar-Ra'd (13:28)")
                    views.setTextViewText(R.id.widget_arabic_text, "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ")
                    views.setTextViewText(R.id.widget_translation, "\"Unquestionably, by the remembrance of Allah hearts are assured.\"")
                }

                views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
