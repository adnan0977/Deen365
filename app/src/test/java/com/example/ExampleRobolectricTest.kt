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
    fun testPrayerCalculationForGpsCoordinates() {
        val schedule = PrayerTimeCalculator.calculateScheduleForCoordinates(
            latitude = 24.8607,
            longitude = 67.0011,
            timezone = 5.0,
            cityName = "Karachi GPS",
            isUsingGpsLocation = true
        )
        assertNotNull(schedule.fajr)
        assertNotNull(schedule.dhuhr)
        assertNotNull(schedule.asr)
        assertNotNull(schedule.maghrib)
        assertNotNull(schedule.isha)
        assertTrue(schedule.fajrTimestamp > 0L)
        assertTrue(schedule.dhuhrTimestamp > 0L)
        assertTrue(schedule.asrTimestamp > 0L)
        assertTrue(schedule.maghribTimestamp > 0L)
        assertTrue(schedule.ishaTimestamp > 0L)
        assertTrue(schedule.isUsingGpsLocation)
        assertEquals("Karachi GPS", schedule.cityName)
    }

    @Test
    fun testFormatCoordinates() {
        val formatted = PrayerTimeCalculator.formatCoordinates(24.8607, 67.0011)
        assertTrue(formatted.contains("24.8607° N"))
        assertTrue(formatted.contains("67.0011° E"))

        val formattedSouthWest = PrayerTimeCalculator.formatCoordinates(-33.8688, -71.2093)
        assertTrue(formattedSouthWest.contains("33.8688° S"))
        assertTrue(formattedSouthWest.contains("71.2093° W"))
    }

    @Test
    fun testLivePrayerCountdownCalculation() {
        val schedule = PrayerTimeCalculator.calculateScheduleForCoordinates(
            latitude = 21.4225,
            longitude = 39.8262,
            timezone = 3.0,
            cityName = "Makkah"
        )
        // Simulate a fixed time: 30 minutes and 15 seconds before Dhuhr
        val mockNow = schedule.dhuhrTimestamp - (30 * 60 * 1000L + 15 * 1000L)
        val countdown = PrayerTimeCalculator.getLiveCountdown(schedule, mockNow)

        assertEquals("Dhuhr", countdown.nextPrayerName)
        assertEquals(0L, countdown.hours)
        assertEquals(30L, countdown.minutes)
        assertEquals(15L, countdown.seconds)
        assertEquals("00:30:15", countdown.formattedHMS)
        assertTrue(countdown.progressFraction in 0f..1f)
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
        assertEquals(8, chainBukhari1.nodes.size)
        assertEquals(7, chainBukhari1.links.size)
        assertTrue(chainBukhari1.isContinuityVerified)
        assertEquals(245, chainBukhari1.spanYears)

        val compiler = chainBukhari1.nodes.first()
        assertEquals(com.example.data.model.NarratorRole.COMPILER, compiler.role)
        assertTrue(compiler.nameEnglish.contains("Bukhari"))
        assertEquals(1, compiler.sunnahNarratorId)
        assertEquals(com.example.data.model.ScholarlyGradeTier.PURPLE, compiler.gradeTier)
        assertTrue(compiler.evaluations.any { it.scholarName.contains("Ibn Hajar") })

        val prophet = chainBukhari1.nodes.last()
        assertEquals(com.example.data.model.NarratorRole.PROPHET, prophet.role)
        assertTrue(prophet.nameEnglish.contains("Prophet Muhammad"))
        assertEquals(com.example.data.model.ScholarlyGradeTier.GOLD, prophet.gradeTier)

        // Verify first transmission link: Bukhari -> Al-Humaydi
        val firstLink = chainBukhari1.links[0]
        assertEquals("bukhari", firstLink.fromNodeId)
        assertEquals("al_humaydi", firstLink.toNodeId)
        assertEquals("حَدَّثَنَا", firstLink.formulaArabic)
        assertEquals(com.example.data.model.TransmissionMethod.SAMA, firstLink.method)
        assertTrue(firstLink.isDirectHearingProven)

        // Verify last transmission link: Umar (RA) -> Prophet
        val lastLink = chainBukhari1.links.last()
        assertEquals("umar", lastLink.fromNodeId)
        assertEquals("prophet", lastLink.toNodeId)
        assertTrue(lastLink.isDirectHearingProven)

        // Test Bukhari Hadith #2 (Golden Madinan Chain)
        val chainBukhari2 = com.example.data.seed.SanadChainSeedData.getSanadForHadith("bukhari", 2)
        assertNotNull(chainBukhari2)
        assertEquals(2, chainBukhari2.hadithNumber)
        assertEquals(7, chainBukhari2.nodes.size)
        assertEquals(6, chainBukhari2.links.size)
        assertTrue(chainBukhari2.nodes.any { it.nodeId == "malik" })
        assertTrue(chainBukhari2.nodes.any { it.nodeId == "aisha" })
    }

    @Test
    fun testSunnahComTransmissionMethodsAndGrading() {
        val methods = com.example.data.model.TransmissionMethod.entries
        assertTrue(methods.size >= 5)
        assertTrue(methods.contains(com.example.data.model.TransmissionMethod.SAMA))
        assertTrue(methods.contains(com.example.data.model.TransmissionMethod.QIRAAH))
        assertTrue(methods.contains(com.example.data.model.TransmissionMethod.ANANAH))

        for (method in methods) {
            assertTrue(method.arabicTerm.isNotBlank())
            assertTrue(method.englishTerm.isNotBlank())
            assertTrue(method.description.isNotBlank())
        }

        // Test dynamic synthesized chain for other collections (e.g. Muslim)
        val muslimChain = com.example.data.seed.SanadChainSeedData.getSanadForHadith("muslim", 8)
        assertNotNull(muslimChain)
        assertEquals(8, muslimChain.hadithNumber)
        assertTrue(muslimChain.links.isNotEmpty())
        assertTrue(muslimChain.nodes.any { it.role == com.example.data.model.NarratorRole.COMPILER })
        assertTrue(muslimChain.nodes.any { it.role == com.example.data.model.NarratorRole.COMPANION })
    }


    @Test
    fun testComparativeFiqhAndAsbabAlNuzul() {
        val allTopics = com.example.data.seed.ComparativeFiqhSeedData.TOPICS
        assertTrue(allTopics.size >= 8)

        val wuduTopic = com.example.data.seed.ComparativeFiqhSeedData.getTopic("wudu_intentions_order")
        assertNotNull(wuduTopic)
        assertEquals(4, wuduTopic!!.rulings.size)
        assertTrue(wuduTopic.rulings.any { it.madhab == com.example.data.model.Madhab.HANAFI })
        assertTrue(wuduTopic.rulings.any { it.madhab == com.example.data.model.Madhab.MALIKI })
        assertTrue(wuduTopic.rulings.any { it.madhab == com.example.data.model.Madhab.SHAFII })
        assertTrue(wuduTopic.rulings.any { it.madhab == com.example.data.model.Madhab.HANBALI })
        assertTrue(wuduTopic.quickSummaryTable.containsKey(com.example.data.model.Madhab.HANAFI))
        assertTrue(wuduTopic.practicalTakeaway.isNotBlank())

        val touchingSkinTopic = com.example.data.seed.ComparativeFiqhSeedData.getTopic("wudu_touching_skin")
        assertNotNull(touchingSkinTopic)
        assertEquals(4, touchingSkinTopic!!.rulings.size)

        val jewelryTopic = com.example.data.seed.ComparativeFiqhSeedData.getTopic("zakat_women_jewelry")
        assertNotNull(jewelryTopic)
        assertEquals(4, jewelryTopic!!.rulings.size)

        val asbabFatihah = com.example.data.seed.AsbabAlNuzulSeedData.getForSurah(1)
        assertNotNull(asbabFatihah)
        assertEquals(1, asbabFatihah!!.surahNumber)
        assertTrue(asbabFatihah.historicalIncident.isNotEmpty())
    }

    @Test
    fun testMadhabEnumFoundationsAndToggles() {
        val madhabs = com.example.data.model.Madhab.entries
        assertEquals(4, madhabs.size)

        val hanafi = com.example.data.model.Madhab.HANAFI
        assertEquals("Hanafi", hanafi.displayName)
        assertEquals("الحنفي", hanafi.arabicName)
        assertTrue(hanafi.founder.contains("Abu Hanifa"))
        assertTrue(hanafi.brandColorHex > 0L)
        assertTrue(hanafi.coreMethodology.isNotBlank())

        val maliki = com.example.data.model.Madhab.MALIKI
        assertEquals("Maliki", maliki.displayName)
        assertTrue(maliki.founder.contains("Malik"))

        val shafii = com.example.data.model.Madhab.SHAFII
        assertEquals("Shafi'i", shafii.displayName)
        assertTrue(shafii.founder.contains("Shafi'i"))

        val hanbali = com.example.data.model.Madhab.HANBALI
        assertEquals("Hanbali", hanbali.displayName)
        assertTrue(hanbali.founder.contains("Ahmad"))
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

    @Test
    fun testSpiritualChatMessageModel() {
        val userMsg = com.example.data.model.SpiritualChatMessage(
            sender = com.example.data.model.MessageSender.USER,
            text = "How can I maintain focus in Namaz?"
        )
        assertEquals(com.example.data.model.MessageSender.USER, userMsg.sender)
        assertEquals("How can I maintain focus in Namaz?", userMsg.text)
        assertFalse(userMsg.isStreaming)
        assertFalse(userMsg.isError)

        val aiMsg = com.example.data.model.SpiritualChatMessage(
            sender = com.example.data.model.MessageSender.AI,
            text = "To attain Khushoo, remember you stand directly before your Creator.",
            arabicSnippet = "قَدْ أَفْلَحَ الْمُؤْمِنُونَ الَّذِينَ هُمْ فِي صَلَاتِهِمْ خَاشِعُونَ",
            reference = "Surah Al-Mu'minun 23:1-2",
            translation = "Certainly will the believers have succeeded: They who are during their prayer humbly submissive."
        )
        assertEquals(com.example.data.model.MessageSender.AI, aiMsg.sender)
        assertNotNull(aiMsg.arabicSnippet)
        assertEquals("Surah Al-Mu'minun 23:1-2", aiMsg.reference)
    }

    @Test
    fun testDailyDuaRoomDatabaseRotation() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val todayDua = com.example.data.repository.DuaOfTheDayRepository.getTodayDua(context, dayOffset = 0)
        assertNotNull(todayDua)
        assertTrue(todayDua.title.isNotBlank())
        assertTrue(todayDua.arabic.isNotBlank())
        assertTrue(todayDua.reference.isNotBlank())

        val nextDayDua = com.example.data.repository.DuaOfTheDayRepository.getTodayDua(context, dayOffset = 1)
        assertNotNull(nextDayDua)
        assertTrue(nextDayDua.title.isNotBlank())
    }

    @Test
    fun testSpiritualGoalsModelDefaultsAndCustomization() {
        val defaultGoals = com.example.domain.model.SpiritualGoals()
        assertEquals(10, defaultGoals.dailyQuranAyahsTarget)
        assertEquals(0.5f, defaultGoals.weeklyQuranJuzTarget, 0.01f)
        assertEquals("Surah Al-Baqarah", defaultGoals.targetSurahKhatamGoal)
        assertEquals(5, defaultGoals.weeklyHadithsToStudy)
        assertEquals("40 Hadith Nawawi", defaultGoals.targetHadithCollection)
        assertEquals(4, defaultGoals.monthlyDuasToMemorize)
        assertEquals("Morning & Evening", defaultGoals.targetDuaCategory)
        assertEquals(5, defaultGoals.dailySalahTarget)
        assertEquals(4, defaultGoals.monthlyFastingTargetDays)
        assertEquals(3, defaultGoals.salahMeaningUnderstandingCompleted)

        val customGoals = defaultGoals.copy(
            dailyQuranAyahsTarget = 25,
            weeklyHadithsToStudy = 10,
            monthlyDuasToMemorize = 8,
            monthlyFastingTargetDays = 6,
            salahMeaningUnderstandingCompleted = 6
        )
        assertEquals(25, customGoals.dailyQuranAyahsTarget)
        assertEquals(10, customGoals.weeklyHadithsToStudy)
        assertEquals(8, customGoals.monthlyDuasToMemorize)
        assertEquals(6, customGoals.monthlyFastingTargetDays)
        assertEquals(6, customGoals.salahMeaningUnderstandingCompleted)
    }

    @Test
    fun testSpiritualAchievementsEngineProgressCalculation() {
        val goals = com.example.domain.model.SpiritualGoals(
            dailyQuranAyahsTarget = 20,
            weeklyHadithsToStudy = 5,
            monthlyDuasToMemorize = 4,
            dailySalahTarget = 5,
            monthlyFastingTargetDays = 2,
            salahMeaningUnderstandingCompleted = 2
        )

        val baseState = com.example.ui.viewmodel.DeenUiState()
        val emptyProgress = com.example.domain.SpiritualAchievementsEngine.computeProgressSummary(baseState, goals)

        assertNotNull(emptyProgress)
        assertTrue(emptyProgress.overallSpiritualVitalityScore in 5..100)
        assertNotNull(emptyProgress.spiritualRankTitle)
        assertEquals(20, emptyProgress.quranAyahsGoal)
        assertEquals(5, emptyProgress.hadithsStudiedGoal)
        assertEquals(4, emptyProgress.duasMemorizedGoal)
        assertEquals(2, emptyProgress.fastingGoalDays)
        assertEquals(2, emptyProgress.salahStepsUnderstood)

        // Test with full progress state
        val fullState = baseState.copy(
            prayerRecord = com.example.data.local.PrayerRecordEntity(
                dateStr = "2026-11-04",
                fajr = true, dhuhr = true, asr = true, maghrib = true, isha = true
            ),
            hadithStudiedCount = 10,
            duaMemorizedCount = 6,
            totalFastedDaysCount = 4,
            completedActionSteps = setOf(1, 2, 3, 4)
        )
        val fullProgress = com.example.domain.SpiritualAchievementsEngine.computeProgressSummary(fullState, goals)
        assertEquals(1.0f, fullProgress.salahProgressPercentage, 0.01f)
        assertEquals(1.0f, fullProgress.hadithProgressPercentage, 0.01f)
        assertEquals(1.0f, fullProgress.duaProgressPercentage, 0.01f)
        assertEquals(1.0f, fullProgress.fastingProgressPercentage, 0.01f)
        assertEquals(1.0f, fullProgress.quranProgressPercentage, 0.01f)
        assertTrue(fullProgress.overallSpiritualVitalityScore >= 95)
        assertEquals("Muttaqi (Steadfast)", fullProgress.spiritualRankTitle)
    }

    @Test
    fun testSpiritualBadgesEvaluation() {
        val goals = com.example.domain.model.SpiritualGoals(
            salahMeaningUnderstandingCompleted = 5
        )
        val state = com.example.ui.viewmodel.DeenUiState(
            prayerStats = com.example.sync.HistoricalPrayerStats(currentStreakDays = 8),
            prayerRecord = com.example.data.local.PrayerRecordEntity(
                dateStr = "2026-11-04",
                fajr = true, dhuhr = true, asr = true, maghrib = true, isha = true
            ),
            hadithStudiedCount = 15,
            hadithMemorizedCount = 3,
            duaMemorizedCount = 7,
            totalFastedDaysCount = 3,
            totalTasbihCount = 550,
            isQiblaAligned = true,
            completedActionSteps = setOf(1, 2, 3)
        )

        val badges = com.example.domain.SpiritualAchievementsEngine.computeBadges(state, goals)
        assertEquals(10, badges.size)

        // 8 days streak -> badge_steadfast_musalli should be unlocked
        val streakBadge = badges.firstOrNull { it.id == "badge_steadfast_musalli" }
        assertNotNull(streakBadge)
        assertTrue(streakBadge!!.isUnlocked)

        // All 5 prayers today -> badge_salah_guardian should be unlocked
        val guardianBadge = badges.firstOrNull { it.id == "badge_salah_guardian" }
        assertNotNull(guardianBadge)
        assertTrue(guardianBadge!!.isUnlocked)

        // 15 hadiths -> badge_hadith_seeker should be unlocked (threshold 5)
        val hadithBadge = badges.firstOrNull { it.id == "badge_hadith_seeker" }
        assertNotNull(hadithBadge)
        assertTrue(hadithBadge!!.isUnlocked)

        // 7 duas -> badge_dua_hisn should be unlocked (threshold 4)
        val duaBadge = badges.firstOrNull { it.id == "badge_dua_hisn" }
        assertNotNull(duaBadge)
        assertTrue(duaBadge!!.isUnlocked)

        // 5 salah steps understood -> badge_khushu_mastery should be unlocked (threshold 5)
        val mindfulNamazBadge = badges.firstOrNull { it.id == "badge_khushu_mastery" }
        assertNotNull(mindfulNamazBadge)
        assertTrue(mindfulNamazBadge!!.isUnlocked)

        // 550 tasbih -> badge_zakir_tasbih should be unlocked (threshold 500)
        val zakirBadge = badges.firstOrNull { it.id == "badge_zakir_tasbih" }
        assertNotNull(zakirBadge)
        assertTrue(zakirBadge!!.isUnlocked)
    }

    @Test
    fun testUserAvatarDirectory() {
        val allAvatars = com.example.domain.model.UserAvatar.entries
        assertTrue(allAvatars.size >= 8)

        val mosque = com.example.domain.model.UserAvatar.fromId("avatar_mosque")
        assertEquals("avatar_mosque", mosque.id)
        assertEquals("🕌", mosque.emoji)

        val quran = com.example.domain.model.UserAvatar.fromId("avatar_quran")
        assertEquals("avatar_quran", quran.id)
        assertEquals("📖", quran.emoji)

        // Fallback for non-existent ID
        val fallback = com.example.domain.model.UserAvatar.fromId("unknown_id_xyz")
        assertEquals(com.example.domain.model.UserAvatar.MOSQUE, fallback)
    }

    @Test
    fun testZakatGoldAndSilverHadithNisabCalculations() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val zakatRepo = com.example.data.repository.ZakatPriceRepository(context)

        val usdCurrency = com.example.data.model.CurrencyInfo.findByCode("USD")
        assertEquals("USD", usdCurrency.code)

        val rates = com.example.data.model.GoldSilverRates(
            goldPricePerGramUSD = 100.0,
            silverPricePerGramUSD = 1.0
        )

        // 1. Hadith-grounded Nisab Thresholds:
        // Gold Nisab = 20 Dinars / Mithqals = 85.0 grams pure gold (Sunan Abi Dawud #1573)
        val expectedGoldNisab = 100.0 * 85.0 // 8,500 USD
        assertEquals(expectedGoldNisab, rates.calculateGoldNisab(usdCurrency), 0.001)

        // Silver Nisab = 200 Dirhams = 595.0 grams pure silver (Sahih al-Bukhari #1405)
        val expectedSilverNisab = 1.0 * 595.0 // 595 USD
        assertEquals(expectedSilverNisab, rates.calculateSilverNisab(usdCurrency), 0.001)

        // 2. Calculation with Silver Nisab when Net Wealth >= Nisab and Hawl is met
        val inputEligible = com.example.data.model.ZakatAssetInput(
            cashInHandAndBank = 1000.0,
            immediateDebtsDueNow = 200.0
        )
        val resultEligible = zakatRepo.calculateZakat(
            input = inputEligible,
            currency = usdCurrency,
            rates = rates,
            standard = com.example.data.model.NisabStandard.SILVER,
            isHawlMet = true
        )

        assertEquals(800.0, resultEligible.netZakatableWealth, 0.001)
        assertTrue(resultEligible.isNetWealthAboveNisab)
        assertTrue(resultEligible.isZakatPayable)
        // Rate: exactly 2.5% (1/40th / Rub' al-'Ushr) -> 800 * 0.025 = 20.0
        assertEquals(20.0, resultEligible.zakatPayableAmount, 0.001)

        // 3. Below Nisab test: Net Wealth (400 USD) < Silver Nisab (595 USD)
        val inputBelowNisab = com.example.data.model.ZakatAssetInput(
            cashInHandAndBank = 500.0,
            immediateDebtsDueNow = 100.0
        )
        val resultBelow = zakatRepo.calculateZakat(
            input = inputBelowNisab,
            currency = usdCurrency,
            rates = rates,
            standard = com.example.data.model.NisabStandard.SILVER,
            isHawlMet = true
        )
        assertEquals(400.0, resultBelow.netZakatableWealth, 0.001)
        assertFalse(resultBelow.isNetWealthAboveNisab)
        assertFalse(resultBelow.isZakatPayable)
        assertEquals(0.0, resultBelow.zakatPayableAmount, 0.001)

        // 4. Hawl (1 lunar year) not met test: wealth above Nisab, but Hawl is false -> 0 Zakat
        val resultNoHawl = zakatRepo.calculateZakat(
            input = inputEligible,
            currency = usdCurrency,
            rates = rates,
            standard = com.example.data.model.NisabStandard.SILVER,
            isHawlMet = false
        )
        assertTrue(resultNoHawl.isNetWealthAboveNisab)
        assertFalse(resultNoHawl.isHawlMet)
        assertFalse(resultNoHawl.isZakatPayable)
        assertEquals(0.0, resultNoHawl.zakatPayableAmount, 0.001)
    }

    @Test
    fun testZakatMultiCurrencyConversionAndPersistence() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val zakatRepo = com.example.data.repository.ZakatPriceRepository(context)

        // Verify currency catalogue
        val currencies = com.example.data.model.CurrencyInfo.ALL_CURRENCIES
        assertTrue(currencies.size >= 15)

        val sarCurrency = com.example.data.model.CurrencyInfo.findByCode("SAR")
        assertEquals("SAR", sarCurrency.code)
        assertEquals("﷼", sarCurrency.symbol)
        assertEquals(3.75, sarCurrency.usdExchangeRate, 0.001)

        val pkrCurrency = com.example.data.model.CurrencyInfo.findByCode("PKR")
        assertEquals("PKR", pkrCurrency.code)
        assertEquals("Rs", pkrCurrency.symbol)

        // Test SharedPreferences Currency Persistence
        zakatRepo.setCurrency("SAR")
        assertEquals("SAR", zakatRepo.selectedCurrency.value.code)

        // Recreate repository from context to verify persistent load across app restart
        val reloadedRepo = com.example.data.repository.ZakatPriceRepository(context)
        assertEquals("SAR", reloadedRepo.selectedCurrency.value.code)

        // Purity scaling checks: 24K (1.0), 22K (22/24), 21K (21/24), 18K (18/24)
        val rates = com.example.data.model.GoldSilverRates(goldPricePerGramUSD = 80.0)
        val gold24k = rates.getGoldPricePerGram(sarCurrency, com.example.data.model.GoldPurity.K24)
        val gold22k = rates.getGoldPricePerGram(sarCurrency, com.example.data.model.GoldPurity.K22)
        val gold18k = rates.getGoldPricePerGram(sarCurrency, com.example.data.model.GoldPurity.K18)

        assertEquals(80.0 * 3.75, gold24k, 0.001) // 300 SAR
        assertEquals(300.0 * (22.0 / 24.0), gold22k, 0.001) // 275 SAR
        assertEquals(300.0 * (18.0 / 24.0), gold18k, 0.001) // 225 SAR
    }

    @Test
    fun testZakatAssetLiabilitiesAndSavedRecords() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val zakatRepo = com.example.data.repository.ZakatPriceRepository(context)

        val usd = com.example.data.model.CurrencyInfo.findByCode("USD")
        val rates = com.example.data.model.GoldSilverRates(
            goldPricePerGramUSD = 80.0,
            silverPricePerGramUSD = 1.0
        )

        // Input combining cash, gold carats, silver, trade goods, liabilities
        val assetInput = com.example.data.model.ZakatAssetInput(
            cashInHandAndBank = 5000.0,
            goldGrams24k = 10.0, // 10 * 80 = 800
            goldGrams22k = 24.0, // 24 * (80 * 22/24) = 1760
            silverGrams = 100.0, // 100 * 1 = 100
            businessInventoryGoods = 4000.0,
            sharesAndMutualFunds = 2000.0,
            immediateDebtsDueNow = 1500.0,
            currentMonthBillsAndExpenses = 500.0
        )

        val result = zakatRepo.calculateZakat(
            input = assetInput,
            currency = usd,
            rates = rates,
            standard = com.example.data.model.NisabStandard.GOLD,
            isHawlMet = true
        )

        // Gross = 5000 + 800 + 1760 + 100 + 4000 + 2000 = 13660
        assertEquals(13660.0, result.grossAssets, 0.01)
        // Liabilities = 1500 + 500 = 2000
        assertEquals(2000.0, result.totalLiabilities, 0.01)
        // Net = 13660 - 2000 = 11660
        assertEquals(11660.0, result.netZakatableWealth, 0.01)
        // Gold Nisab = 85 * 80 = 6800. Net (11660) >= 6800 -> eligible!
        assertTrue(result.isZakatPayable)
        // Zakat = 11660 * 0.025 = 291.50
        assertEquals(291.50, result.zakatPayableAmount, 0.01)

        // Test saving record
        val savedId = zakatRepo.saveCalculation(
            title = "Ramadan 1446 Zakat",
            result = result,
            currency = usd,
            notes = "Paid to local widows & students"
        )
        assertNotNull(savedId)

        val records = zakatRepo.savedRecords.value
        val saved = records.find { it.id == savedId }
        assertNotNull(saved)
        assertEquals("Ramadan 1446 Zakat", saved!!.title)
        assertEquals(291.50, saved.zakatAmount, 0.01)
        assertEquals("USD", saved.currencyCode)

        // Test delete record
        zakatRepo.deleteRecord(savedId)
        val recordsAfterDelete = zakatRepo.savedRecords.value
        assertFalse(recordsAfterDelete.any { it.id == savedId })

        // Verify the 8 Quranic Beneficiaries list
        val beneficiaries = com.example.data.model.ZakatBeneficiary.MASARIF_LIST
        assertEquals(8, beneficiaries.size)
        assertEquals("fuqara", beneficiaries[0].id)
        assertEquals("الْفُقَرَاء", beneficiaries[0].arabicTitle)
        assertEquals("ibn_sabil", beneficiaries[7].id)
        assertEquals("ابْنِ السَّبِيل", beneficiaries[7].arabicTitle)
    }

    @Test
    fun test99NamesOfAllahCanonAndMemorization() {
        val allNames = com.example.data.model.AsmaUlHusnaData.ALL_99_NAMES
        assertEquals(99, allNames.size)

        // Verify first and last names per authentic Sunnah
        assertEquals(1, allNames.first().number)
        assertEquals("Ar-Rahman", allNames.first().transliteration)
        assertEquals("الرَّحْمَٰنُ", allNames.first().arabic)
        assertTrue(allNames.first().quranicReference.isNotBlank())

        assertEquals(99, allNames.last().number)
        assertEquals("As-Sabur", allNames.last().transliteration)
        assertEquals("الصَّبُورُ", allNames.last().arabic)

        // Verify uniqueness of numbers 1..99
        val numbers = allNames.map { it.number }.toSet()
        assertEquals(99, numbers.size)

        // Verify search matching
        val matchingRahman = allNames.filter { it.transliteration.contains("Rahman", ignoreCase = true) }
        assertTrue(matchingRahman.isNotEmpty())

        // Verify state model
        var state = com.example.data.model.AsmaUlHusnaState()
        assertEquals(0, state.memorizedIds.size)
        state = state.copy(memorizedIds = setOf(1, 2, 3))
        assertEquals(3, state.memorizedIds.size)
        assertTrue(state.memorizedIds.contains(1))

        // Verify helper functions for home dashboard preview
        val featured = com.example.data.model.AsmaUlHusnaData.getFeaturedName(1)
        assertEquals(1, featured.number)
        assertEquals("Ar-Rahman", featured.transliteration)

        val previewNames = com.example.data.model.AsmaUlHusnaData.getPreviewNames(8)
        assertEquals(8, previewNames.size)
        assertEquals(1, previewNames[0].number)
        assertEquals(8, previewNames[7].number)

        assertEquals(0, com.example.data.model.AsmaUlHusnaData.getMemorizedPercentage(0))
        assertEquals(50, com.example.data.model.AsmaUlHusnaData.getMemorizedPercentage(50))
        assertEquals(100, com.example.data.model.AsmaUlHusnaData.getMemorizedPercentage(99))
    }

    @Test
    fun testHajjAndUmrahRitualsAndCounters() {
        val umrahSteps = com.example.data.model.HajjUmrahData.UMRAH_STEPS
        assertEquals(4, umrahSteps.size)
        assertEquals("umrah_ihram", umrahSteps[0].id)
        assertEquals("umrah_tawaf", umrahSteps[1].id)
        assertEquals("umrah_sai", umrahSteps[2].id)
        assertEquals("umrah_halq", umrahSteps[3].id)

        val hajjDays = com.example.data.model.HajjUmrahData.HAJJ_DAYS
        assertEquals(4, hajjDays.size)
        assertEquals("hajj_day_8", hajjDays[0].id)
        assertEquals("hajj_day_9", hajjDays[1].id)

        // Test Interactive Tawaf Tracker (7 laps)
        var tawaf = com.example.data.model.TawafTrackerState()
        assertEquals(1, tawaf.currentLap)
        assertEquals(0, tawaf.completedLaps)
        assertFalse(tawaf.isFinished)

        // Simulate 7 laps
        for (i in 1..7) {
            val isDone = i == 7
            tawaf = tawaf.copy(
                completedLaps = i,
                currentLap = if (isDone) 7 else i + 1,
                isFinished = isDone
            )
        }
        assertEquals(7, tawaf.completedLaps)
        assertTrue(tawaf.isFinished)

        // Test Interactive Sa'i Tracker (7 rounds Safa-Marwah)
        var sai = com.example.data.model.SaiTrackerState()
        assertEquals(1, sai.currentRound)
        assertEquals("Safa", sai.currentStation)

        // Round 1 Safa->Marwah, Round 2 Marwah->Safa
        sai = sai.copy(completedRounds = 1, currentRound = 2, currentStation = "Marwah")
        assertEquals(1, sai.completedRounds)
        assertEquals("Marwah", sai.currentStation)

        // Checklist items
        val checklist = com.example.data.model.HajjUmrahData.DEFAULT_CHECKLIST
        assertTrue(checklist.size >= 10)
        assertFalse(checklist[0].isCompleted)
    }

    @Test
    fun testQadaSalahCalculatorAndPersistence() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val qadaRepo = com.example.data.repository.QadaSalahRepository(context)

        // Set deficit of 365 days (1 full year)
        qadaRepo.setTotalDeficit(365)
        var counts = qadaRepo.countsState.value
        assertEquals(365, counts.fajrRemaining)
        assertEquals(365, counts.dhuhrRemaining)
        assertEquals(365, counts.witrRemaining)
        assertEquals(0, counts.fajrCompleted)

        // Mark Fajr completed
        qadaRepo.markPrayerCompleted(com.example.data.model.QadaPrayerType.FAJR, delta = 1)
        counts = qadaRepo.countsState.value
        assertEquals(364, counts.fajrRemaining)
        assertEquals(1, counts.fajrCompleted)

        // Mark batch 5 Dhuhr completed
        qadaRepo.markPrayerCompleted(com.example.data.model.QadaPrayerType.DHUHR, delta = 5)
        counts = qadaRepo.countsState.value
        assertEquals(360, counts.dhuhrRemaining)
        assertEquals(5, counts.dhuhrCompleted)

        // Undo 1 Dhuhr
        qadaRepo.undoPrayerCompleted(com.example.data.model.QadaPrayerType.DHUHR, delta = 1)
        counts = qadaRepo.countsState.value
        assertEquals(361, counts.dhuhrRemaining)
        assertEquals(4, counts.dhuhrCompleted)

        // Verify reload from SharedPreferences
        val reloadedRepo = com.example.data.repository.QadaSalahRepository(context)
        val reloadedCounts = reloadedRepo.countsState.value
        assertEquals(364, reloadedCounts.fajrRemaining)
        assertEquals(1, reloadedCounts.fajrCompleted)
        assertEquals(361, reloadedCounts.dhuhrRemaining)
        assertEquals(4, reloadedCounts.dhuhrCompleted)
    }
}




