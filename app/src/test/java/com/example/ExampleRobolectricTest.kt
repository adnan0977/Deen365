package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DeenDatabase
import com.example.data.repository.DailyReflectionRepository
import com.example.domain.IslamicCalendarHelper
import com.example.domain.PrayerTimeCalculator
import com.example.domain.QiblaCalculator
import com.example.sensor.QiblaSensorManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @org.junit.Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
            val options = com.google.firebase.FirebaseOptions.Builder()
                .setApplicationId("1:123456789:android:abcdef")
                .setApiKey("fakeApiKey12345")
                .setProjectId("deen365-test")
                .build()
            com.google.firebase.FirebaseApp.initializeApp(context, options)
        }
    }

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Deen365", appName)
    }

    @Test
    fun testPrayerCalculation() {
        val schedule = PrayerTimeCalculator.calculateSchedule(PrayerTimeCalculator.POPULAR_CITIES[0])
        assertNotNull(schedule.fajr)
        assertNotNull(schedule.dhuhr)
        assertNotNull(schedule.asr)
        assertNotNull(schedule.maghrib)
        assertNotNull(schedule.isha)
        assertNotNull(schedule.suhoorEnd)
        assertNotNull(schedule.iftar)
    }

    @Test
    fun testQiblaCalculation() {
        // From Karachi to Makkah
        val bearing = QiblaCalculator.calculateQiblaBearing(24.8607, 67.0011)
        assertTrue(bearing in 0f..360f)
    }

    @Test
    fun testIslamicCalendar() {
        val hijri = IslamicCalendarHelper.getTodayHijriDate()
        assertTrue(hijri.day in 1..30)
        assertTrue(hijri.year >= 1440)
        assertNotNull(hijri.monthName)
    }

    @Test
    fun testQiblaSensorManagerLifecycle() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sensorManager = QiblaSensorManager(context)
        sensorManager.startListening()
        assertNotNull(sensorManager.sensorData.value)
        sensorManager.stopListening()
    }

    @Test
    fun testDailyQuranicReflectionRoomCaching() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = DeenDatabase.getDatabase(context)
        val repo = DailyReflectionRepository(db.dailyReflectionDao(), db.ayahDao())

        val reflection = repo.fetchDailyReflection(forceNew = true)
        assertNotNull(reflection)
        assertTrue(reflection.arabicText.isNotBlank())
        assertTrue(reflection.translation.isNotBlank())
        assertTrue(reflection.surahNumber in 1..114)
        assertTrue(reflection.ayahNumber > 0)

        // Verify it was stored in Room
        val cached = db.dailyReflectionDao().getLatestReflectionSync()
        assertNotNull(cached)
        assertEquals(reflection.arabicText, cached?.arabicText)
    }

    @Test
    fun testPrayerAttendanceHistoryAndStats() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = DeenDatabase.getDatabase(context)
        val trackerDao = db.trackerDao()
        val repo = com.example.data.repository.TrackerRepository(trackerDao)

        val testDate = "2025-05-15"
        trackerDao.insertOrUpdatePrayerRecord(com.example.data.local.PrayerRecordEntity(dateStr = testDate))

        // Seed or toggle prayers
        repo.togglePrayer("Fajr", testDate)
        repo.togglePrayer("Dhuhr", testDate)
        repo.togglePrayer("Asr", testDate)

        val record = trackerDao.getPrayerRecordSync(testDate)
        assertNotNull(record)
        assertTrue(record!!.fajr)
        assertTrue(record.dhuhr)
        assertTrue(record.asr)
        assertFalse(record.maghrib)
        assertEquals(3, record.completedCount)

        val allRecords = trackerDao.getAllPrayerRecordsSync()
        val stats = repo.calculateHistoricalStats(allRecords)
        assertTrue(stats.totalPrayedCount >= 3)
    }

    @Test
    fun testPrayerNotificationChannelAndSettings() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        com.example.notification.PrayerNotificationHelper.createNotificationChannel(context)

        val customSettings = com.example.notification.PrayerNotificationSettings(
            alertsEnabled = true,
            notifyFajr = true,
            notifySunrise = false,
            notifyDhuhr = true,
            notifyAsr = true,
            notifyMaghrib = true,
            notifyIsha = true,
            reminderOffsetMinutes = 10,
            soundMode = com.example.notification.AlertSoundMode.SOFT_CHIME
        )
        com.example.notification.PrayerNotificationHelper.saveSettings(context, customSettings)

        val retrieved = com.example.notification.PrayerNotificationHelper.getSettings(context)
        assertTrue(retrieved.alertsEnabled)
        assertTrue(retrieved.notifyFajr)
        assertFalse(retrieved.notifySunrise)
        assertEquals(10, retrieved.reminderOffsetMinutes)
        assertEquals(com.example.notification.AlertSoundMode.SOFT_CHIME, retrieved.soundMode)
    }

    @Test
    fun testPrayerNotificationSchedulingAndNextAlarm() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val schedule = PrayerTimeCalculator.calculateSchedule(PrayerTimeCalculator.POPULAR_CITIES[0])

        com.example.notification.PrayerNotificationHelper.scheduleDailyPrayerAlarms(context, schedule)

        val nextInfo = com.example.notification.PrayerNotificationHelper.getNextScheduledAlarmInfo(context, schedule)
        assertNotNull(nextInfo)
        assertTrue(nextInfo!!.prayerName.isNotBlank())
        assertTrue(nextInfo.remainingFormatted.isNotBlank())
        assertTrue(nextInfo.targetTimestamp > 0L)
    }

    @Test
    fun testNamazOfflineRoomPersistence() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = DeenDatabase.getDatabase(context)
        val audioPlayer = com.example.data.audio.SalahAudioPlayer.getInstance(context)
        val repo = com.example.data.repository.NamazOfflineRepository(db.namazDao(), audioPlayer, context)

        // Test caching Fajr prayer into Room DB
        val success = repo.cachePrayerForOffline("fajr")
        assertTrue(success)

        val isDownloaded = repo.isPrayerDownloaded("fajr")
        assertTrue(isDownloaded)

        // Verify prayer entity
        val prayerFlow = db.namazDao().getPrayer("fajr")
        val prayer = prayerFlow.first()
        assertNotNull(prayer)
        assertEquals("fajr", prayer?.id)
        assertEquals("صَلَاةُ الفَجْرِ", prayer?.arabicTitle)
        assertEquals(4, prayer?.totalRakats)
        assertTrue(prayer?.isDownloaded == true)

        // Verify steps in Room
        val stepsFlow = db.namazDao().getPrayerSteps("fajr")
        val steps = stepsFlow.first()
        assertTrue(steps.isNotEmpty())
        assertEquals(1, steps[0].stepIndex)
        assertEquals("Niyyah (Heart's Intention)", steps[0].title)

        // Verify Azan in Room
        val azanFlow = db.namazDao().getPrayerAzan("fajr")
        val azan = azanFlow.first()
        assertNotNull(azan)
        assertTrue(azan?.isFajr == true)
        val phrases = azan!!.parsePhrases()
        assertTrue(phrases.isNotEmpty())
        assertTrue(phrases.any { it.arabic.contains("الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ") })
    }

    @Test
    fun testHadithCollectionAndLearningProgress() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = DeenDatabase.getDatabase(context)
        val repo = com.example.data.repository.HadithRepository(db.hadithDao())

        // Verify collections are available
        val collections = repo.getCollections()
        assertTrue(collections.size >= 6)
        assertTrue(collections.any { it.id == "bukhari" })
        assertTrue(collections.any { it.id == "muslim" })
        assertTrue(collections.any { it.id == "nawawi" })

        // Check Hadiths for Bukhari
        val hadiths = repo.getHadithsForCollection("bukhari")
        assertTrue(hadiths.isNotEmpty())

        val firstHadith = hadiths[0]
        // Test Favorite toggle
        repo.toggleFavorite("bukhari", firstHadith.hadithNumber, false)
        val favCount = repo.getFavoriteHadithsCount().first()
        assertTrue(favCount >= 1)

        // Test Studied toggle
        repo.toggleStudied("bukhari", firstHadith.hadithNumber, false)
        val studiedCount = repo.getStudiedCount().first()
        assertTrue(studiedCount >= 1)

        // Test Memorized toggle
        repo.toggleMemorized("bukhari", firstHadith.hadithNumber, false)
        val memorizedCount = repo.getMemorizedCount().first()
        assertTrue(memorizedCount >= 1)

        // Test Search
        val searchResults = repo.searchHadiths("intention").first()
        assertTrue(searchResults.isNotEmpty())

        val all = repo.allHadiths.first()
        assertTrue(all.isNotEmpty())

        // Test filter by narrator
        val abuHurairahHadiths = all.filter { it.narrator.contains("Abu Hurairah", ignoreCase = true) }
        assertTrue(abuHurairahHadiths.isNotEmpty())

        // Test filter by category
        val faithHadiths = all.filter { it.chapterName.contains("Faith", ignoreCase = true) }
        assertTrue(faithHadiths.isNotEmpty())
    }

    @Test
    fun testDuaModuleAndLearningPersistence() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = DeenDatabase.getDatabase(context)
        val repo = com.example.data.repository.DuaRepository(db.duaDao(), kotlinx.coroutines.GlobalScope)

        // Verify initial seeding or items
        repo.seedDuasIfEmpty()
        val allDuas = repo.getAllDuas().first()
        assertTrue(allDuas.isNotEmpty())

        val dua = allDuas[0]
        // Test Favorite toggle
        repo.toggleFavorite(dua.id, false)
        val favCount = repo.getFavoriteCount().first()
        assertTrue(favCount >= 1)

        // Test Memorized toggle
        repo.toggleMemorized(dua.id, false)
        val memCount = repo.getMemorizedCount().first()
        assertTrue(memCount >= 1)

        // Test Practice Counter increment and reset
        repo.incrementPractice(dua.id, 0)
        val updatedDua = db.duaDao().getDuaById(dua.id)
        assertEquals(1, updatedDua?.practiceCount)

        repo.resetPractice(dua.id)
        val resetDua = db.duaDao().getDuaById(dua.id)
        assertEquals(0, resetDua?.practiceCount)

        // Test search
        val morningDuas = repo.searchDuas("morning").first()
        assertTrue(morningDuas.isNotEmpty())
    }

    @Test
    fun testQuranAudioReciterUrls() {
        val mishary = com.example.data.model.QuranAudioReciter.MISHARY
        val abdulBasit = com.example.data.model.QuranAudioReciter.ABDUL_BASIT
        val sudais = com.example.data.model.QuranAudioReciter.SUDAIS

        val misharyUrl = mishary.getAyahAudioUrl(surahNumber = 1, ayahNumberInSurah = 1)
        assertEquals("https://everyayah.com/data/Alafasy_128kbps/001001.mp3", misharyUrl)

        val abdulBasitUrl = abdulBasit.getAyahAudioUrl(surahNumber = 112, ayahNumberInSurah = 4)
        assertEquals("https://everyayah.com/data/Abdul_Basit_Mujawwad_128kbps/112004.mp3", abdulBasitUrl)

        val sudaisUrl = sudais.getAyahAudioUrl(surahNumber = 2, ayahNumberInSurah = 255)
        assertEquals("https://everyayah.com/data/Abdurrahmaan_As-Sudais_192kbps/002255.mp3", sudaisUrl)
    }

    @Test
    fun testTafsirAndWordByWordAnalysis() {
        val tafsir1_1 = com.example.data.repository.TafsirRepository.getTafsirForAyah(1, 1)
        assertNotNull(tafsir1_1)
        assertEquals("Tafsir Ibn Kathir", tafsir1_1.author)
        assertTrue(tafsir1_1.summary.isNotBlank())
        assertTrue(tafsir1_1.keyLessons.isNotEmpty())

        val words = com.example.data.repository.TafsirRepository.getWordByWordForAyah(
            "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            "In the name of Allah the Entirely Merciful the Especially Merciful"
        )
        assertTrue(words.isNotEmpty())
        assertEquals("بِسْمِ", words[0].arabic)
    }

    @Test
    fun testQiblaAndTasbihThemes() {
        assertEquals(4, com.example.domain.QiblaTheme.entries.size)
        assertTrue(com.example.domain.QiblaTheme.entries.any { it == com.example.domain.QiblaTheme.EMERALD_GOLD })
        assertTrue(com.example.domain.QiblaTheme.entries.any { it == com.example.domain.QiblaTheme.EARTH_SATELLITE })

        assertEquals(4, com.example.domain.TasbihTheme.entries.size)
        assertTrue(com.example.domain.TasbihTheme.entries.any { it == com.example.domain.TasbihTheme.EMERALD_GOLD })
        assertTrue(com.example.domain.TasbihTheme.entries.any { it == com.example.domain.TasbihTheme.CELESTIAL_MIDNIGHT })
    }

    @Test
    fun testAdminProModuleGating() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.viewmodel.DeenViewModel(context)

        val module = com.example.ui.model.DefaultHomeModules.ALL_MODULES.first()
        // Admin toggle status
        assertNotNull(module)
        vm.toggleModuleProStatus(module.id, isPro = true)
        assertTrue(vm.uiState.value.proModuleIds.contains(module.id))

        // Toggle back off
        vm.toggleModuleProStatus(module.id, isPro = false)
        assertFalse(vm.uiState.value.proModuleIds.contains(module.id))
    }

    @Test
    fun testPrayerNotificationSettingsAndAlarms() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val defaultSettings = com.example.notification.PrayerNotificationHelper.getSettings(context)
        assertNotNull(defaultSettings)

        val customSettings = defaultSettings.copy(
            alertsEnabled = true,
            notifyFajr = true,
            notifyDhuhr = true,
            notifyAsr = true,
            notifyMaghrib = true,
            notifyIsha = true,
            reminderOffsetMinutes = 10,
            soundMode = com.example.notification.AlertSoundMode.SOFT_CHIME
        )
        com.example.notification.PrayerNotificationHelper.saveSettings(context, customSettings)

        val retrieved = com.example.notification.PrayerNotificationHelper.getSettings(context)
        assertEquals(10, retrieved.reminderOffsetMinutes)
        assertEquals(com.example.notification.AlertSoundMode.SOFT_CHIME, retrieved.soundMode)
        assertTrue(retrieved.alertsEnabled)

        val city = PrayerTimeCalculator.POPULAR_CITIES[0]
        val schedule = PrayerTimeCalculator.calculateSchedule(city)
        assertNotNull(schedule.fajr)
        assertNotNull(schedule.dhuhr)
        assertNotNull(schedule.asr)
        assertNotNull(schedule.maghrib)
        assertNotNull(schedule.isha)

        // Test next alarm info
        val nextInfo = com.example.notification.PrayerNotificationHelper.getNextScheduledAlarmInfo(context, schedule, retrieved)
        assertNotNull(nextInfo)
        assertTrue(nextInfo?.prayerName?.isNotBlank() == true)
        assertTrue(nextInfo?.formattedTime?.isNotBlank() == true)
    }

    @Test
    fun testPrayerNotificationSchedulerServiceHelpers() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Test schedule summary helper
        val summary = com.example.notification.PrayerNotificationSchedulerService.getLatestScheduleSummary(context)
        assertTrue(summary.isNotBlank())

        // Test coordinate calculation for five daily prayers based on custom coordinates
        val makkahSchedule = PrayerTimeCalculator.calculateScheduleForCoordinates(
            latitude = 21.4225,
            longitude = 39.8262,
            timezone = 3.0,
            cityName = "Makkah al-Mukarramah",
            isUsingGpsLocation = true
        )
        assertEquals("Makkah al-Mukarramah", makkahSchedule.cityName)
        assertTrue(makkahSchedule.isUsingGpsLocation)
        assertTrue(makkahSchedule.fajr.isNotBlank())
        assertTrue(makkahSchedule.dhuhr.isNotBlank())
        assertTrue(makkahSchedule.asr.isNotBlank())
        assertTrue(makkahSchedule.maghrib.isNotBlank())
        assertTrue(makkahSchedule.isha.isNotBlank())
    }

    @Test
    fun testDarkModeConfigEnumAndDefaults() {
        assertEquals(com.example.domain.DarkModeConfig.SYSTEM, com.example.domain.DarkModeConfig.fromId("system"))
        assertEquals(com.example.domain.DarkModeConfig.LIGHT, com.example.domain.DarkModeConfig.fromId("light"))
        assertEquals(com.example.domain.DarkModeConfig.DARK, com.example.domain.DarkModeConfig.fromId("dark"))
        assertEquals(com.example.domain.DarkModeConfig.SYSTEM, com.example.domain.DarkModeConfig.fromId(null))
        assertEquals(com.example.domain.DarkModeConfig.SYSTEM, com.example.domain.DarkModeConfig.fromId("unknown"))
    }

    @Test
    fun testDarkModeViewModelThreeWayToggleAndAutoSync() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.viewmodel.DeenViewModel(app)

        // 1. Initial State should be SYSTEM default
        val initialConfig = vm.uiState.value.darkModeConfig
        assertEquals(com.example.domain.DarkModeConfig.SYSTEM, initialConfig)

        // 2. Select Always Dark mode -> Verify effective isDarkMode=true, and Qibla/Tasbih auto sync to Celestial Midnight
        vm.setDarkModeConfig(com.example.domain.DarkModeConfig.DARK, isSystemInDark = false)
        assertTrue(vm.uiState.value.isDarkMode)
        assertEquals(com.example.domain.DarkModeConfig.DARK, vm.uiState.value.darkModeConfig)
        assertEquals(com.example.domain.QiblaTheme.CELESTIAL_MIDNIGHT, vm.uiState.value.selectedQiblaTheme)
        assertEquals(com.example.domain.TasbihTheme.CELESTIAL_MIDNIGHT, vm.uiState.value.selectedTasbihTheme)

        // 3. Select Always Light mode -> Verify effective isDarkMode=false, and Qibla/Tasbih revert to Emerald Gold
        vm.setDarkModeConfig(com.example.domain.DarkModeConfig.LIGHT, isSystemInDark = true)
        assertFalse(vm.uiState.value.isDarkMode)
        assertEquals(com.example.domain.DarkModeConfig.LIGHT, vm.uiState.value.darkModeConfig)
        assertEquals(com.example.domain.QiblaTheme.EMERALD_GOLD, vm.uiState.value.selectedQiblaTheme)
        assertEquals(com.example.domain.TasbihTheme.EMERALD_GOLD, vm.uiState.value.selectedTasbihTheme)

        // 4. Set to SYSTEM and simulate system dark mode toggle
        vm.setDarkModeConfig(com.example.domain.DarkModeConfig.SYSTEM, isSystemInDark = false)
        assertFalse(vm.uiState.value.isDarkMode)

        vm.onSystemDarkModeChanged(isSystemInDark = true)
        assertTrue(vm.uiState.value.isDarkMode)
        assertEquals(com.example.domain.QiblaTheme.CELESTIAL_MIDNIGHT, vm.uiState.value.selectedQiblaTheme)
        assertEquals(com.example.domain.TasbihTheme.CELESTIAL_MIDNIGHT, vm.uiState.value.selectedTasbihTheme)

        vm.onSystemDarkModeChanged(isSystemInDark = false)
        assertFalse(vm.uiState.value.isDarkMode)
        assertEquals(com.example.domain.QiblaTheme.EMERALD_GOLD, vm.uiState.value.selectedQiblaTheme)
        assertEquals(com.example.domain.TasbihTheme.EMERALD_GOLD, vm.uiState.value.selectedTasbihTheme)
    }

    @Test
    fun testFastingMonthProgressTrackerAndPieChartCalculations() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.viewmodel.DeenViewModel(app)

        // 1. Initial State: Total month days default 30, completed days default 14
        val initialTotal = vm.uiState.value.fastingMonthTotalDays
        val initialCompleted = vm.uiState.value.fastingTrackerDaysCount
        assertEquals(30, initialTotal)
        assertEquals(14, initialCompleted)

        val remainingDays = initialTotal - initialCompleted
        assertEquals(16, remainingDays)

        val progressPercentage = (initialCompleted.toFloat() / initialTotal.toFloat() * 100f)
        assertTrue(progressPercentage > 46f && progressPercentage < 47f)

        // 2. Test changing month length (e.g. lunar 29 days moon sighting)
        vm.setFastingMonthTotalDays(29)
        assertEquals(29, vm.uiState.value.fastingMonthTotalDays)
        val remainingIn29 = vm.uiState.value.fastingMonthTotalDays - vm.uiState.value.fastingTrackerDaysCount
        assertEquals(15, remainingIn29)

        // 3. Test increment and decrement stepper
        vm.incrementFastingTrackerDays()
        assertEquals(15, vm.uiState.value.fastingTrackerDaysCount)

        vm.decrementFastingTrackerDays()
        assertEquals(14, vm.uiState.value.fastingTrackerDaysCount)

        // 4. Test setting arbitrary completed days clamped to bounds
        vm.setFastingTrackerDays(28)
        assertEquals(28, vm.uiState.value.fastingTrackerDaysCount)

        // 5. Test today's fasting toggle
        val wasFasting = vm.uiState.value.isFastingToday
        vm.toggleFastingToday()
        assertEquals(!wasFasting, vm.uiState.value.isFastingToday)
    }

    @Test
    fun testDailySunsetFastingNotificationSchedulingAndLogAction() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.viewmodel.DeenViewModel(app)

        // 1. Verify default settings have notifySunsetFastingReminder = true
        val settings = com.example.notification.PrayerNotificationHelper.getSettings(context)
        assertTrue(settings.notifySunsetFastingReminder)

        // 2. Toggle via ViewModel
        vm.toggleSunsetFastingReminder(false, context)
        assertFalse(vm.uiState.value.notifySunsetFasting)
        val savedDisabled = com.example.notification.PrayerNotificationHelper.getSettings(context)
        assertFalse(savedDisabled.notifySunsetFastingReminder)

        vm.toggleSunsetFastingReminder(true, context)
        assertTrue(vm.uiState.value.notifySunsetFasting)
        val savedEnabled = com.example.notification.PrayerNotificationHelper.getSettings(context)
        assertTrue(savedEnabled.notifySunsetFastingReminder)

        // 3. Test dynamic scheduling with PrayerSchedule (sunset / iftar)
        val city = PrayerTimeCalculator.POPULAR_CITIES[0]
        val schedule = PrayerTimeCalculator.calculateSchedule(city)
        assertNotNull(schedule.iftar)
        assertNotNull(schedule.maghrib)
        assertTrue(schedule.maghribTimestamp > 0L)

        com.example.notification.PrayerNotificationHelper.scheduleDailyPrayerAlarms(context, schedule, savedEnabled)

        // 4. Test instant test notification dispatch
        com.example.notification.PrayerNotificationHelper.triggerInstantTestFastingNotification(
            context = context,
            cityName = schedule.cityName,
            sunsetTime = schedule.iftar
        )

        // 5. Test BroadcastReceiver action ACTION_LOG_FAST_COMPLETED
        val receiver = com.example.notification.PrayerNotificationReceiver()
        val logIntent = android.content.Intent(com.example.notification.PrayerNotificationHelper.ACTION_LOG_FAST_COMPLETED).apply {
            putExtra(com.example.notification.PrayerNotificationHelper.EXTRA_CITY_NAME, schedule.cityName)
            putExtra(com.example.notification.PrayerNotificationHelper.EXTRA_PRAYER_TIME, schedule.iftar)
            putExtra(com.example.notification.PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, com.example.notification.PrayerNotificationHelper.FASTING_REQ_CODE)
        }

        receiver.onReceive(context, logIntent)

        // Wait brief delay for async Room DB write
        kotlinx.coroutines.delay(200)

        val db = DeenDatabase.getDatabase(context)
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val record = db.trackerDao().getFastingRecordSync(todayStr)
        assertNotNull(record)
        assertTrue(record!!.isFasted)
    }

    @Test
    fun testFastingHistoryLogAndStatsCalculation() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = DeenDatabase.getDatabase(context)
        val trackerDao = db.trackerDao()
        val repo = com.example.data.repository.TrackerRepository(trackerDao)

        // 1. Log various past fasts across dates
        val date1 = "2026-09-10"
        val date2 = "2026-09-11"
        val date3 = "2026-09-15"
        val date4 = "2026-10-01"
        val date5 = "2026-10-05"

        repo.logFastRecord(date1, isFasted = true, fastingType = "Ramadan", note = "Day 1")
        repo.logFastRecord(date2, isFasted = true, fastingType = "Ramadan", note = "Day 2")
        repo.logFastRecord(date3, isFasted = true, fastingType = "Sunnah (Mon/Thu)", note = "Thursday Sunnah")
        repo.logFastRecord(date4, isFasted = true, fastingType = "White Days", note = "Ayyam al-Beed")
        repo.logFastRecord(date5, isFasted = true, fastingType = "Qadha", isQadha = true, note = "Makeup fast")

        // 2. Verify stored in Room database
        val allRecords = trackerDao.getAllFastingRecordsSync()
        assertTrue(allRecords.size >= 5)

        val r1 = trackerDao.getFastingRecordSync(date1)
        assertNotNull(r1)
        assertTrue(r1!!.isFasted)
        assertEquals("Ramadan", r1.fastingType)
        assertEquals("Day 1", r1.note)

        val r5 = trackerDao.getFastingRecordSync(date5)
        assertNotNull(r5)
        assertTrue(r5!!.isQadha)
        assertEquals("Makeup fast", r5.note)

        // 3. Test stats calculation
        val testRecords = listOf(
            com.example.data.local.FastingRecordEntity(dateStr = date1, isFasted = true, fastingType = "Ramadan"),
            com.example.data.local.FastingRecordEntity(dateStr = date2, isFasted = true, fastingType = "Ramadan"),
            com.example.data.local.FastingRecordEntity(dateStr = date3, isFasted = true, fastingType = "Sunnah (Mon/Thu)"),
            com.example.data.local.FastingRecordEntity(dateStr = date4, isFasted = true, fastingType = "White Days"),
            com.example.data.local.FastingRecordEntity(dateStr = date5, isFasted = true, fastingType = "Qadha", isQadha = true)
        )
        val stats = repo.calculateFastingStats(testRecords)
        assertEquals(5, stats.totalFastedDays)
        assertEquals(2, stats.ramadanFastsCount)
        assertEquals(1, stats.sunnahFastsCount)
        assertEquals(1, stats.whiteDaysCount)
        assertEquals(1, stats.qadhaFastsCount)
        assertTrue(stats.bestStreak >= 2)

        // 4. Test deleting a fast
        repo.deleteFastRecord(date1)
        val deleted = trackerDao.getFastingRecordSync(date1)
        org.junit.Assert.assertNull(deleted)

        // 5. Test ViewModel actions
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.viewmodel.DeenViewModel(app)
        val vmTestDate = "2026-08-20"
        vm.logPastFast(vmTestDate, fastingType = "Sunnah (Mon/Thu)", isQadha = false, note = "Test fast")

        kotlinx.coroutines.delay(200)
        val recordFromVm = trackerDao.getFastingRecordSync(vmTestDate)
        assertNotNull(recordFromVm)
        assertTrue(recordFromVm!!.isFasted)
        assertEquals("Sunnah (Mon/Thu)", recordFromVm.fastingType)
    }

    @Test
    fun testEnhancedQiblaSensorManagerAndDeclination() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sensorManager = com.example.sensor.QiblaSensorManager(context)

        // 1. Initial State
        val initialData = sensorManager.sensorData.value
        assertNotNull(initialData)
        assertTrue(initialData.magneticFieldStrength > 0f)
        assertNotNull(initialData.sensorTypeUsed)

        // 2. Location & GeomagneticField declination calculation
        sensorManager.setLocation(51.5074, -0.1278, 10.0)
        sensorManager.setLocation(40.7128, -74.0060, 10.0)

        // 3. Lifecycle start & stop
        sensorManager.startListening()
        sensorManager.stopListening()

        // 4. Test QiblaCalculator Kaaba distance and direction vectors
        val londonQibla = QiblaCalculator.calculateQibla(51.5074, -0.1278)
        assertTrue(londonQibla.bearingDegrees in 115f..125f)
        assertTrue(londonQibla.distanceKm in 4000.0..6000.0)
        assertEquals("ESE", londonQibla.compassDirection)

        // 5. Test ViewModel Qibla integration
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.viewmodel.DeenViewModel(app)
        vm.startQiblaCompass()
        assertNotNull(vm.uiState.value.qiblaSensorData)
        assertTrue(vm.uiState.value.qiblaBearing in 0f..360f)
        vm.stopQiblaCompass()
    }

    @Test
    fun testQuranSunnahAlimParitySuite() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.viewmodel.DeenViewModel(app)

        // 1. Tajweed Engine Rule Detection
        val testVerse = "إِنَّا أَنزَلْنَاهُ فِي لَيْلَةِ الْقَدْرِ"
        val occurrences = com.example.data.util.TajweedEngine.detectRules(testVerse)
        assertNotNull(occurrences)
        val annotated = com.example.data.util.TajweedEngine.annotateTajweedText(testVerse, isTajweedActive = true)
        assertEquals(testVerse, annotated.text)

        // 2. Hizb Quarter Data
        val markerFatihah = com.example.data.seed.HizbQuarterData.getMarkerForAyah(1, 1)
        assertNotNull(markerFatihah)
        assertEquals(1, markerFatihah!!.hizbNumber)
        val markerKursi = com.example.data.seed.HizbQuarterData.getMarkerForAyah(2, 255)
        assertNotNull(markerKursi)

        // 3. Alim Thematic Subject Index
        assertTrue(com.example.data.seed.AlimSubjectIndexData.TOPICS.isNotEmpty())
        val tawhidTopic = com.example.data.seed.AlimSubjectIndexData.TOPICS.firstOrNull { it.id == "tawhid_faith" }
        assertNotNull(tawhidTopic)
        assertTrue(tawhidTopic!!.references.isNotEmpty())

        // 4. Comparative Fiqh 4-Madhhab Data
        assertTrue(com.example.data.seed.ComparativeFiqhData.ISSUES.isNotEmpty())
        val salahTopic = com.example.data.seed.ComparativeFiqhData.ISSUES.first { it.id == "salah_hand_placement" }
        assertEquals(4, salahTopic.schoolPositions.size)
        assertTrue(salahTopic.schoolPositions.any { it.madhhab == "Hanafi" })
        assertTrue(salahTopic.schoolPositions.any { it.madhhab == "Shafi'i" })
        assertTrue(salahTopic.schoolPositions.any { it.madhhab == "Maliki" })
        assertTrue(salahTopic.schoolPositions.any { it.madhhab == "Hanbali" })

        // 5. Sunnah Kitab and Bab Progress Data
        val bukhariBooks = com.example.data.seed.HadithKitabProgressData.getBooksForCollection("bukhari")
        assertTrue(bukhariBooks.isNotEmpty())
        assertEquals("bukhari", bukhariBooks.first().collectionId)

        // 6. ViewModel Reading Goal and Tajweed Mode
        vm.setDailyQuranGoal(20)
        assertEquals(20, vm.uiState.value.dailyQuranGoalMinutes)

        vm.incrementReadingTime(60)
        assertTrue(vm.uiState.value.todayReadingSeconds >= 60)

        assertFalse(vm.uiState.value.isTajweedModeEnabled)
        vm.toggleTajweedMode()
        assertTrue(vm.uiState.value.isTajweedModeEnabled)

        // 7. Hadith Book Completion Toggle with Room Repository
        runBlocking {
            val book = bukhariBooks.first()
            val initialCompleted = vm.kitabProgressRepo.completedBookIds.first().contains(book.id)
            vm.kitabProgressRepo.toggleBookCompletion(book)
            val toggledCompleted = vm.kitabProgressRepo.completedBookIds.first().contains(book.id)
            assertEquals(!initialCompleted, toggledCompleted)
        }
    }

    @Test
    fun testRoomKitabCompletionTrackerAndCollectionProgress() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = DeenDatabase.getDatabase(context)
        val dao = db.kitabCompletionDao()
        val repo = com.example.data.repository.KitabProgressRepository(dao)

        // 1. Initial State: Clear all test records
        repo.clearAllProgress()
        val initialCompleted = repo.completedBookIds.first()
        assertTrue(initialCompleted.isEmpty())

        // 2. Mark individual chapters as read
        val bukhariBooks = com.example.data.seed.HadithKitabProgressData.getBooksForCollection("bukhari")
        val revelationBook = bukhariBooks.first { it.id == "bukhari_1" }
        val beliefBook = bukhariBooks.first { it.id == "bukhari_2" }

        val marked1 = repo.toggleBookCompletion(revelationBook)
        assertTrue(marked1)
        val marked2 = repo.toggleBookCompletion(beliefBook)
        assertTrue(marked2)

        // 3. Verify reactive Room Flow
        val completedIds = repo.completedBookIds.first()
        assertEquals(2, completedIds.size)
        assertTrue(completedIds.contains("bukhari_1"))
        assertTrue(completedIds.contains("bukhari_2"))

        // 4. Verify progress across collections computation
        val progressList = repo.collectionProgressList.first()
        assertEquals(3, progressList.size)

        val bukhariProgress = progressList.first { it.collectionId == "bukhari" }
        assertEquals(2, bukhariProgress.completedBooksCount)
        assertEquals(7, bukhariProgress.totalBooksCount)
        assertEquals(7 + 53, bukhariProgress.completedHadithCount)
        assertTrue(bukhariProgress.progressFraction > 0.28f && bukhariProgress.progressFraction < 0.29f)

        val muslimProgress = progressList.first { it.collectionId == "muslim" }
        assertEquals(0, muslimProgress.completedBooksCount)
        assertEquals(0f, muslimProgress.progressFraction)

        // 5. Mark entire Muslim collection as read
        repo.setCollectionCompleted("muslim", true)
        val muslimUpdatedList = repo.collectionProgressList.first()
        val muslimUpdated = muslimUpdatedList.first { it.collectionId == "muslim" }
        assertEquals(4, muslimUpdated.completedBooksCount)
        assertEquals(4, muslimUpdated.totalBooksCount)
        assertEquals(1.0f, muslimUpdated.progressFraction)

        // 6. Toggle book off
        val unmark = repo.toggleBookCompletion(revelationBook)
        assertFalse(unmark)
        assertFalse(repo.completedBookIds.first().contains("bukhari_1"))

        // 7. Cleanup
        repo.clearAllProgress()
        assertTrue(repo.completedBookIds.first().isEmpty())
    }

    @Test
    fun testGharibHadithGlossarySearchAndFiltering() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.viewmodel.DeenViewModel(app)

        // 1. Verify Dataset
        assertTrue(com.example.data.seed.GharibHadithData.ENTRIES.size >= 10)
        assertTrue(com.example.data.seed.GharibHadithData.CATEGORIES.contains("Commerce & Fiqh"))

        // 2. Search by English Transliteration
        val searchMufarrid = com.example.data.seed.GharibHadithData.searchGlossary("Mufarridoon")
        assertEquals(1, searchMufarrid.size)
        assertEquals("mufarridoon", searchMufarrid.first().id)
        assertEquals("ف - ر - د", searchMufarrid.first().rootArabic)

        // 3. Search by Arabic Root Letters
        val searchRootGharara = com.example.data.seed.GharibHadithData.searchGlossary("غ-ر-ر")
        assertTrue(searchRootGharara.any { it.id == "ghurr_muhajjaloon" })

        // 4. Search by Category Filter
        val commerceEntries = com.example.data.seed.GharibHadithData.searchGlossary("", category = "Commerce & Fiqh")
        assertTrue(commerceEntries.size >= 3)
        assertTrue(commerceEntries.any { it.id == "musarrah" })
        assertTrue(commerceEntries.any { it.id == "najash" })
        assertTrue(commerceEntries.any { it.id == "inah" })

        // 5. Search with contextual term from Hadith reference
        val searchHima = com.example.data.seed.GharibHadithData.searchGlossary("sanctuary")
        assertTrue(searchHima.any { it.id == "hima" })

        // 6. Verify ViewModel interactions
        assertFalse(vm.uiState.value.isGharibGlossaryOpen)
        vm.openGharibGlossary()
        assertTrue(vm.uiState.value.isGharibGlossaryOpen)

        vm.setGharibSearchQuery("Najash")
        assertEquals("Najash", vm.uiState.value.gharibSearchQuery)

        vm.setGharibCategory("Commerce & Fiqh")
        assertEquals("Commerce & Fiqh", vm.uiState.value.selectedGharibCategory)

        vm.selectGharibEntry(searchMufarrid.first())
        assertEquals("mufarridoon", vm.uiState.value.selectedGharibEntry?.id)

        vm.closeGharibGlossary()
        assertFalse(vm.uiState.value.isGharibGlossaryOpen)
    }

    @Test
    fun testHadithSchemaAndKitabTrackerDao() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = DeenDatabase.getDatabase(context)
        val trackerDao = db.hadithKitabTrackerDao()

        // 1. Insert Hadith Books
        val books = listOf(
            com.example.data.local.HadithBookEntity(
                id = "bukhari_test",
                titleEnglish = "Sahih al-Bukhari",
                titleArabic = "صحيح البخاري",
                authorName = "Imam al-Bukhari",
                deathYearHijri = 256,
                totalHadithCount = 7563,
                totalChaptersCount = 97,
                badgeLetter = "B",
                badgeColorHex = "#E25A53",
                description = "Authentic collection"
            ),
            com.example.data.local.HadithBookEntity(
                id = "muslim_test",
                titleEnglish = "Sahih Muslim",
                titleArabic = "صحيح مسلم",
                authorName = "Imam Muslim",
                deathYearHijri = 261,
                totalHadithCount = 3033,
                totalChaptersCount = 56,
                badgeLetter = "M",
                badgeColorHex = "#E05688",
                description = "Authentic collection"
            )
        )
        trackerDao.insertBooks(books)

        val retrievedBooks = trackerDao.getAllBooks().first()
        assertTrue(retrievedBooks.any { it.id == "bukhari_test" })
        assertTrue(retrievedBooks.any { it.id == "muslim_test" })

        // 2. Insert Hadith Chapters
        val chapters = listOf(
            com.example.data.local.HadithChapterEntity(
                id = "bukhari_test_1",
                collectionId = "bukhari_test",
                bookNumber = 1,
                titleEnglish = "1. Revelation",
                titleArabic = "بدء الوحي",
                hadithRange = "Hadith No: 1-7",
                hadithCount = 7,
                overview = "Beginning of revelation"
            ),
            com.example.data.local.HadithChapterEntity(
                id = "bukhari_test_2",
                collectionId = "bukhari_test",
                bookNumber = 2,
                titleEnglish = "2. Belief",
                titleArabic = "كتاب الإيمان",
                hadithRange = "Hadith No: 8-58",
                hadithCount = 51,
                overview = "Branches of faith"
            )
        )
        trackerDao.insertChapters(chapters)

        val bukhariChapters = trackerDao.getChaptersForCollection("bukhari_test").first()
        assertEquals(2, bukhariChapters.size)

        // 3. Test Kitab Completion Tracking
        assertEquals(0, trackerDao.getCompletedChaptersCountForCollection("bukhari_test").first())

        // Mark chapter 1 as completed
        trackerDao.updateChapterCompletion("bukhari_test_1", isCompleted = true, completedAt = System.currentTimeMillis())
        assertEquals(1, trackerDao.getCompletedChaptersCountForCollection("bukhari_test").first())

        val completedIds = trackerDao.getCompletedChapterIds().first()
        assertTrue(completedIds.contains("bukhari_test_1"))
        assertFalse(completedIds.contains("bukhari_test_2"))

        // Toggle chapter 2 as completed
        trackerDao.toggleChapterCompletion("bukhari_test_2")
        assertEquals(2, trackerDao.getCompletedChaptersCountForCollection("bukhari_test").first())

        // 4. Batch complete collection
        trackerDao.setCollectionCompleted("bukhari_test", isCompleted = false, completedAt = null)
        assertEquals(0, trackerDao.getCompletedChaptersCountForCollection("bukhari_test").first())

        // 5. Aggregate progress tuples
        trackerDao.updateChapterCompletion("bukhari_test_1", isCompleted = true, completedAt = System.currentTimeMillis())
        val progressSummaries = trackerDao.getBookProgressSummaries().first()
        val bukhariSummary = progressSummaries.find { it.collectionId == "bukhari_test" }
        assertNotNull(bukhariSummary)
        assertEquals(1, bukhariSummary?.completedChapters)
        assertEquals(2, bukhariSummary?.totalChapters)

        // 6. Verify HadithRecordEntity alias compatibility
        val record: com.example.data.local.HadithRecordEntity = com.example.data.local.HadithEntity(
            collection = "bukhari",
            bookNumber = 1,
            chapterName = "Revelation",
            hadithNumber = 1,
            arabicText = "إنما الأعمال بالنيات",
            translation = "Actions are according to intentions",
            narrator = "Umar bin Al-Khattab",
            grade = "Sahih"
        )
        assertEquals(1, record.id)
        assertEquals("bukhari", record.collection)
    }

    @Test
    fun testQuranMultiTranslations() {
        val clearQuran = com.example.data.repository.QuranMultiTranslationProvider.getTranslation(
            1, 1, "en.khattab", "In the Name of Allah"
        )
        assertTrue(clearQuran.contains("Compassionate") || clearQuran.contains("Merciful"))

        val urduMaududi = com.example.data.repository.QuranMultiTranslationProvider.getTranslation(
            1, 1, "ur.maududi", "In the Name of Allah"
        )
        assertTrue(urduMaududi.contains("اللہ کے نام سے"))

        val frenchHamidullah = com.example.data.repository.QuranMultiTranslationProvider.getTranslation(
            1, 1, "fr.hamidullah", "In the Name of Allah"
        )
        assertTrue(frenchHamidullah.contains("Au nom d'Allah"))

        val indonesianKemenag = com.example.data.repository.QuranMultiTranslationProvider.getTranslation(
            1, 1, "id.kemenag", "In the Name of Allah"
        )
        assertTrue(indonesianKemenag.contains("Dengan nama Allah"))

        val comparisonList = com.example.data.repository.QuranMultiTranslationProvider.getTranslationsForAyah(
            1, 1, setOf("en.khattab", "en.sahih", "ur.maududi"), "In the Name of Allah"
        )
        assertEquals(3, comparisonList.size)
    }

    @Test
    fun testQuranHighlightAndGoalBookmarksDao() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = DeenDatabase.getDatabase(context)
        val studyDao = db.quranStudyDao()

        // 1. Highlighting Test
        val highlight = com.example.data.local.QuranHighlightEntity(
            id = "1_1",
            surahNumber = 1,
            ayahNumber = 1,
            surahName = "Al-Fatihah",
            colorHex = "#10B981",
            colorName = "Emerald",
            categoryLabel = "Reflection & Tadabbur",
            note = "Contemplate the meaning of Bismillah"
        )
        studyDao.upsertHighlight(highlight)

        val retrievedHighlight = studyDao.getHighlight(1, 1)
        assertNotNull(retrievedHighlight)
        assertEquals("#10B981", retrievedHighlight?.colorHex)
        assertEquals("Contemplate the meaning of Bismillah", retrievedHighlight?.note)

        val allHighlights = studyDao.getAllHighlights().first()
        assertTrue(allHighlights.any { it.id == "1_1" })

        // 2. Goal Checkpoints Test
        val goal = com.example.data.local.QuranGoalBookmarkEntity(
            id = "goal_test_1",
            surahNumber = 1,
            ayahNumber = 7,
            surahName = "Al-Fatihah",
            goalLabel = "Morning Fajr Target",
            targetMinutes = 15,
            note = "Complete Al-Fatihah recitation",
            isCompleted = false,
            dateString = "2026-10-30"
        )
        studyDao.upsertGoalBookmark(goal)

        val retrievedGoal = studyDao.getGoalBookmark(1, 7)
        assertNotNull(retrievedGoal)
        assertEquals(false, retrievedGoal?.isCompleted)

        studyDao.updateGoalBookmarkCompletion("goal_test_1", true)
        val updatedGoal = studyDao.getGoalBookmark(1, 7)
        assertEquals(true, updatedGoal?.isCompleted)

        val allGoals = studyDao.getAllGoalBookmarks().first()
        assertTrue(allGoals.any { it.id == "goal_test_1" })

        // Clean up
        studyDao.deleteHighlight(1, 1)
        assertEquals(null, studyDao.getHighlight(1, 1))

        studyDao.deleteGoalBookmark("goal_test_1")
        assertEquals(null, studyDao.getGoalBookmark(1, 7))
    }

    @Test
    fun testQuranReaderSearchFunctionality() {
        val testAyahs = listOf(
            com.example.data.local.AyahEntity(
                surahNumber = 1,
                ayahNumber = 1,
                arabicText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                translation = "In the Name of Allah—the Most Compassionate, Most Merciful.",
                transliteration = "Bismi Allahi arrahmani arraheem"
            ),
            com.example.data.local.AyahEntity(
                surahNumber = 1,
                ayahNumber = 2,
                arabicText = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                translation = "All praise is for Allah—Lord of all worlds,",
                transliteration = "Alhamdu lillahi rabbi alAAalameen"
            ),
            com.example.data.local.AyahEntity(
                surahNumber = 1,
                ayahNumber = 6,
                arabicText = "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ",
                translation = "Guide us along the Straight Path,",
                transliteration = "Ihdina assirata almustaqeem"
            )
        )

        // 1. Keyword search in English translation
        val mercyMatches = testAyahs.filter {
            it.translation.contains("Merciful", ignoreCase = true)
        }
        assertEquals(1, mercyMatches.size)
        assertEquals(1, mercyMatches.first().ayahNumber)

        // 2. Keyword search in transliteration
        val pathMatches = testAyahs.filter {
            it.transliteration.contains("mustaqeem", ignoreCase = true)
        }
        assertEquals(1, pathMatches.size)
        assertEquals(6, pathMatches.first().ayahNumber)

        // 3. Keyword search in Arabic text
        val arabicMatches = testAyahs.filter {
            it.arabicText.contains("الْعَالَمِينَ")
        }
        assertEquals(1, arabicMatches.size)
        assertEquals(2, arabicMatches.first().ayahNumber)

        // 4. Exact verse number search
        val verseNumMatches = testAyahs.filter {
            it.ayahNumber.toString() == "6"
        }
        assertEquals(1, verseNumMatches.size)
        assertEquals(6, verseNumMatches.first().ayahNumber)

        // 5. Surah matching by name or number
        val surahs = com.example.data.seed.QuranSeedData.SURAH_LIST
        val kahfMatches = surahs.filter {
            it.englishName.contains("Kahf", ignoreCase = true) || it.number.toString() == "Kahf"
        }
        assertTrue(kahfMatches.any { it.number == 18 })

        val numberMatches = surahs.filter {
            it.number.toString() == "36"
        }
        assertEquals("Ya-Sin", numberMatches.first().englishName)
    }

    @Test
    fun testLocationBasedPrayerNotificationService() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. Channel creation & settings initialization
        com.example.notification.PrayerNotificationHelper.createNotificationChannel(context)
        val initialSettings = com.example.notification.PrayerNotificationSettings(
            alertsEnabled = true,
            notifyFajr = true,
            notifySunrise = false,
            notifyDhuhr = true,
            notifyAsr = true,
            notifyMaghrib = true,
            notifyIsha = true,
            notifySunsetFastingReminder = true,
            reminderOffsetMinutes = 5,
            soundMode = com.example.notification.AlertSoundMode.FULL_ADHAN
        )
        com.example.notification.PrayerNotificationHelper.saveSettings(context, initialSettings)

        // 2. Solar timings calculation using GPS coordinates
        val gpsSchedule = com.example.domain.PrayerTimeCalculator.calculateScheduleForCoordinates(
            latitude = 24.8607,
            longitude = 67.0011,
            timezone = 5.0,
            cityName = "Karachi GPS",
            method = com.example.domain.CalculationMethod.KARACHI,
            isUsingGpsLocation = true
        )
        assertNotNull(gpsSchedule.fajr)
        assertNotNull(gpsSchedule.dhuhr)
        assertNotNull(gpsSchedule.asr)
        assertNotNull(gpsSchedule.maghrib)
        assertNotNull(gpsSchedule.isha)
        assertTrue(gpsSchedule.isUsingGpsLocation)

        // 3. Execution of PrayerNotificationSchedulerService logic
        val summary = com.example.notification.PrayerNotificationSchedulerService.scheduleNotificationsForLocation(context, forceGps = false)
        assertTrue(summary.contains("scheduled"))
        assertTrue(com.example.notification.PrayerNotificationSchedulerService.isAlarmsScheduled(context))

        val retrievedSummary = com.example.notification.PrayerNotificationSchedulerService.getLatestScheduleSummary(context)
        assertEquals(summary, retrievedSummary)

        // 4. Verify Next Alarm Info
        val nextInfo = com.example.notification.PrayerNotificationHelper.getNextScheduledAlarmInfo(context, gpsSchedule, initialSettings)
        assertNotNull(nextInfo)
        assertTrue(nextInfo!!.prayerName in listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"))
        assertTrue(nextInfo.targetTimestamp > 0L)

        // 5. Test turning off alerts
        com.example.notification.PrayerNotificationHelper.saveSettings(context, initialSettings.copy(alertsEnabled = false))
        val disabledSummary = com.example.notification.PrayerNotificationSchedulerService.scheduleNotificationsForLocation(context)
        assertTrue(disabledSummary.contains("turned off") || disabledSummary.contains("disabled"))
        assertFalse(com.example.notification.PrayerNotificationSchedulerService.isAlarmsScheduled(context))

        // Restore active alerts
        com.example.notification.PrayerNotificationHelper.saveSettings(context, initialSettings)

        // 6. Test PrayerNotificationReceiver ACTION_MARK_PRAYED updates Room DB
        val db = DeenDatabase.getDatabase(context)
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        db.trackerDao().insertOrUpdatePrayerRecord(com.example.data.local.PrayerRecordEntity(dateStr = todayStr))

        val receiver = com.example.notification.PrayerNotificationReceiver()
        val markAsrIntent = android.content.Intent(com.example.notification.PrayerNotificationHelper.ACTION_MARK_PRAYED).apply {
            putExtra(com.example.notification.PrayerNotificationHelper.EXTRA_PRAYER_NAME, "Asr")
            putExtra(com.example.notification.PrayerNotificationHelper.EXTRA_NOTIFICATION_ID, 2003)
        }
        receiver.onReceive(context, markAsrIntent)

        // Allow async coroutine in receiver to finish
        kotlinx.coroutines.delay(200)

        val record = db.trackerDao().getPrayerRecordSync(todayStr)
        assertNotNull(record)
        assertTrue(record!!.asr)
    }

    @Test
    fun testCustomMosqueTimingAlarmsAndReminders() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val schedule = com.example.domain.PrayerTimeCalculator.calculateSchedule(com.example.domain.PrayerTimeCalculator.POPULAR_CITIES[0])

        // 1. Configure custom mosque name
        com.example.notification.MosqueTimingHelper.setMosqueName(context, "Masjid Al-Farooq")
        assertEquals("Masjid Al-Farooq", com.example.notification.MosqueTimingHelper.getMosqueName(context))

        // 2. Set custom Iqamah timings for Fajr and Asr
        com.example.notification.MosqueTimingHelper.savePrayerTiming(
            context = context,
            prayerId = "fajr",
            manualTime = "05:45 AM",
            isActive = true,
            preAlarmMinutes = 15,
            alarmSoundMode = com.example.notification.AlertSoundMode.FULL_ADHAN
        )
        com.example.notification.MosqueTimingHelper.savePrayerTiming(
            context = context,
            prayerId = "asr",
            manualTime = "05:15 PM",
            isActive = true,
            preAlarmMinutes = 10,
            alarmSoundMode = com.example.notification.AlertSoundMode.SOFT_CHIME
        )

        // 3. Retrieve timings and verify persistence
        val timings = com.example.notification.MosqueTimingHelper.getMosqueTimings(context, schedule)
        val fajrTiming = timings.find { it.prayerId == "fajr" }
        assertNotNull(fajrTiming)
        assertEquals("05:45 AM", fajrTiming!!.manualMosqueTime)
        assertTrue(fajrTiming.isMosqueTimingActive)
        assertEquals(15, fajrTiming.preAlarmMinutes)
        assertEquals(com.example.notification.AlertSoundMode.FULL_ADHAN, fajrTiming.alarmSoundMode)

        val asrTiming = timings.find { it.prayerId == "asr" }
        assertNotNull(asrTiming)
        assertEquals("05:15 PM", asrTiming!!.manualMosqueTime)
        assertTrue(asrTiming.isMosqueTimingActive)
        assertEquals(10, asrTiming.preAlarmMinutes)
        assertEquals(com.example.notification.AlertSoundMode.SOFT_CHIME, asrTiming.alarmSoundMode)

        // 4. Test scheduleDailyPrayerAlarms with mosque timings active
        com.example.notification.PrayerNotificationHelper.scheduleDailyPrayerAlarms(context, schedule)

        // 5. Test parseTimeToMillis helper
        val millis = com.example.notification.MosqueTimingHelper.parseTimeToMillis("05:45 AM")
        assertTrue(millis > 0L)

        // 6. Test instant test mosque alarm trigger
        com.example.notification.MosqueTimingHelper.triggerInstantTestMosqueAlarm(context, "fajr")

        // 7. Test global mosque mode toggle
        com.example.notification.MosqueTimingHelper.setGlobalMosqueActive(context, true)
        assertTrue(com.example.notification.MosqueTimingHelper.isGlobalMosqueActive(context))
        val allActiveTimings = com.example.notification.MosqueTimingHelper.getMosqueTimings(context, schedule)
        assertTrue(allActiveTimings.all { it.isMosqueTimingActive })
    }

    @Test
    fun testQuranWordGrammarSeed() {
        val detailRahman = com.example.data.seed.QuranWordGrammarSeed.getGrammarDetail("ٱلرَّحْمَٰنِ")
        assertNotNull(detailRahman)
        assertEquals("ر - ح - م", detailRahman.root)
        assertEquals("رحم", detailRahman.rootArabic)
        assertTrue(detailRahman.occurrenceCount > 50)
        assertTrue(detailRahman.partOfSpeech.contains("Noun") || detailRahman.partOfSpeech.contains("اسم"))

        val detailHamd = com.example.data.seed.QuranWordGrammarSeed.getGrammarDetail("ٱلْحَمْدُ")
        assertNotNull(detailHamd)
        assertEquals("ح - م - د", detailHamd.root)
        assertTrue(detailHamd.derivedWords.isNotEmpty())
    }

    @Test
    fun testSanadChainTransmissionSeed() {
        val chainBukhari1 = com.example.data.seed.SanadChainSeedData.getSanadForHadith("bukhari", 1)
        assertNotNull(chainBukhari1)
        assertEquals(1, chainBukhari1.hadithNumber)
        assertEquals("bukhari", chainBukhari1.collectionId)
        assertTrue(chainBukhari1.nodes.size >= 5)

        val compiler = chainBukhari1.nodes.first()
        assertEquals(com.example.data.model.NarratorRole.COMPILER, compiler.role)
        assertTrue(compiler.nameEnglish.contains("Bukhari"))

        val prophet = chainBukhari1.nodes.last()
        assertEquals(com.example.data.model.NarratorRole.PROPHET, prophet.role)
        assertTrue(prophet.nameEnglish.contains("Prophet Muhammad"))
    }

    @Test
    fun testComparativeFiqhAndAsbabAlNuzul() {
        val wuduTopic = com.example.data.seed.ComparativeFiqhSeedData.getTopic("wudu_intentions_order")
        assertNotNull(wuduTopic)
        assertEquals(4, wuduTopic!!.rulings.size)
        assertTrue(wuduTopic.rulings.any { it.madhab == com.example.data.model.Madhab.HANAFI })
        assertTrue(wuduTopic.rulings.any { it.madhab == com.example.data.model.Madhab.MALIKI })
        assertTrue(wuduTopic.rulings.any { it.madhab == com.example.data.model.Madhab.SHAFII })
        assertTrue(wuduTopic.rulings.any { it.madhab == com.example.data.model.Madhab.HANBALI })

        val asbabFatihah = com.example.data.seed.AsbabAlNuzulSeedData.getForSurah(1)
        assertNotNull(asbabFatihah)
        assertEquals(1, asbabFatihah!!.surahNumber)
        assertTrue(asbabFatihah.historicalIncident.isNotEmpty())
    }

    @Test
    fun testQuranExplorerStructureAndMorphology() {
        // 1. Test Preset Verse 1:1 (Bismillah)
        val verse1_1 = com.example.data.seed.QuranExplorerSeedData.getVerse("1:1")
        assertNotNull(verse1_1)
        assertEquals("1:1", verse1_1.verseKey)
        assertEquals(4, verse1_1.words.size)
        assertEquals(4, verse1_1.totalWordsCount)
        assertEquals("بِسْمِ", verse1_1.words[0].textUthmani)
        assertEquals("س - م - و", verse1_1.words[0].root)
        assertEquals("P+N", verse1_1.words[0].partOfSpeechCode)
        assertTrue(verse1_1.words[0].morphologySegments.any { it.segmentType == com.example.data.model.MorphologySegmentType.PREFIX })
        assertTrue(verse1_1.words[0].morphologySegments.any { it.segmentType == com.example.data.model.MorphologySegmentType.STEM })

        // 2. Test Verse 2:255 (Ayat al-Kursi)
        val verseKursi = com.example.data.seed.QuranExplorerSeedData.getVerse("2:255")
        assertNotNull(verseKursi)
        assertEquals(2, verseKursi.surahNumber)
        assertEquals(255, verseKursi.ayahNumber)
        assertTrue(verseKursi.uniqueRootsCount >= 3)
        val wordHayy = verseKursi.words.find { it.textUthmani.contains("ٱلْحَىُّ") }
        assertNotNull(wordHayy)
        assertEquals("ح - ي - ي", wordHayy!!.root)

        // 3. Test Dynamic Verse Synthesis
        val dynamicVerse = com.example.data.seed.QuranExplorerSeedData.getVerse("3:18", "شَهِدَ اللَّهُ", "Allah witnesses")
        assertNotNull(dynamicVerse)
        assertEquals("3:18", dynamicVerse.verseKey)
        assertTrue(dynamicVerse.words.isNotEmpty())

        // 4. Test Popular Roots Directory
        assertTrue(com.example.data.seed.QuranExplorerSeedData.POPULAR_ROOTS.isNotEmpty())
        val rahmanRoot = com.example.data.seed.QuranExplorerSeedData.POPULAR_ROOTS.find { it.rootArabic == "رحم" }
        assertNotNull(rahmanRoot)
        assertEquals(560, rahmanRoot!!.totalOccurrencesInQuran)
    }
}



