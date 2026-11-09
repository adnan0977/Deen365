package com.example

import com.example.data.seed.DuaSeedData
import com.example.data.seed.NamazGuideData
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testNamazGuideStepsDataIntegrity() {
        val steps = NamazGuideData.STEPS
        assertEquals(15, steps.size)

        for (step in steps) {
            assertTrue("Step ${step.id} has empty title", step.title.isNotBlank())
            assertTrue("Step ${step.id} has empty postureName", step.postureName.isNotBlank())
            assertTrue("Step ${step.id} has empty arabicText", step.arabicText.isNotBlank())
            assertTrue("Step ${step.id} has empty transliteration", step.transliteration.isNotBlank())
            assertTrue("Step ${step.id} has empty translation", step.translation.isNotBlank())
            assertTrue("Step ${step.id} has empty explanation", step.explanation.isNotBlank())
            assertTrue("Step ${step.id} has empty spiritualBenefit", step.spiritualBenefit.isNotBlank())
            assertTrue("Step ${step.id} has empty commonMistakes", step.commonMistakes.isNotBlank())
        }

        val prayers = NamazGuideData.PRAYERS
        assertTrue(prayers.size >= 5)
        for (prayer in prayers) {
            assertTrue(prayer.totalRakats > 0)
            assertTrue(prayer.overview.isNotBlank())
            assertTrue(prayer.stepIds.isNotEmpty())
        }
    }

    @Test
    fun testDuaSeedDataIntegrity() {
        val duas = DuaSeedData.DUAS
        assertTrue(duas.isNotEmpty())

        for (dua in duas) {
            assertTrue("Dua ${dua.id} has empty title", dua.title.isNotBlank())
            assertTrue("Dua ${dua.id} has empty category", dua.category.isNotBlank())
            assertTrue("Dua ${dua.id} has empty arabicText", dua.arabicText.isNotBlank())
            assertTrue("Dua ${dua.id} has empty transliteration", dua.transliteration.isNotBlank())
            assertTrue("Dua ${dua.id} has empty translation", dua.translation.isNotBlank())
            assertTrue("Dua ${dua.id} has empty explanation", dua.explanation.isNotBlank())
            assertTrue("Dua ${dua.id} has empty reference", dua.reference.isNotBlank())
            assertTrue("Dua ${dua.id} recommendedCount must be >= 1", dua.recommendedCount >= 1)
        }
    }

    @Test
    fun testDhikrSeedDataIntegrity() {
        val items = com.example.data.seed.DhikrSeedData.DEFAULT_ITEMS
        assertTrue("Dhikr items list should not be empty", items.isNotEmpty())
        for (item in items) {
            assertTrue("Item ${item.id} has empty arabicText", item.arabicText.isNotBlank())
            assertTrue("Item ${item.id} has empty transliteration", item.transliteration.isNotBlank())
            assertTrue("Item ${item.id} has empty meaning", item.meaning.isNotBlank())
            assertTrue("Item ${item.id} dailyGoal must be positive", item.dailyGoal > 0)
        }
    }

    @Test
    fun testHistoricalPrayerStatsCalculation() {
        val sampleRecords = listOf(
            com.example.data.local.PrayerRecordEntity(
                dateStr = "2026-10-18",
                fajr = true, dhuhr = true, asr = true, maghrib = true, isha = true
            ),
            com.example.data.local.PrayerRecordEntity(
                dateStr = "2026-10-19",
                fajr = true, dhuhr = true, asr = true, maghrib = true, isha = false
            ),
            com.example.data.local.PrayerRecordEntity(
                dateStr = "2026-10-20",
                fajr = true, dhuhr = false, asr = true, maghrib = true, isha = true
            )
        )

        assertEquals(5, sampleRecords[0].completedCount)
        assertEquals(4, sampleRecords[1].completedCount)
        assertEquals(4, sampleRecords[2].completedCount)
        assertEquals(100f, sampleRecords[0].completionPercentage, 0.01f)
        assertEquals(80f, sampleRecords[1].completionPercentage, 0.01f)
    }

    @Test
    fun testInteractiveLearningAcademyDataIntegrity() {
        val tracks = com.example.data.seed.InteractiveLearningModulesData.ALL_TRACKS
        assertEquals("Should have 5 core learning tracks", 5, tracks.size)

        for (track in tracks) {
            assertTrue("Track ${track.id} has empty title", track.title.isNotBlank())
            assertTrue("Track ${track.id} has empty categoryIcon", track.categoryIcon.isNotBlank())
            assertTrue("Track ${track.id} has empty description", track.description.isNotBlank())
            assertTrue("Track ${track.id} must have lessons", track.lessons.isNotEmpty())

            for (lesson in track.lessons) {
                assertTrue("Lesson ${lesson.id} has empty title", lesson.title.isNotBlank())
                assertTrue("Lesson ${lesson.id} has empty summary", lesson.summary.isNotBlank())
                assertTrue("Lesson ${lesson.id} has empty contentMarkdown", lesson.contentMarkdown.isNotBlank())
                assertTrue("Lesson ${lesson.id} has empty practicalExample", lesson.practicalExample.isNotBlank())
                assertTrue("Lesson ${lesson.id} has empty keyAyahOrHadithQuote", lesson.keyAyahOrHadithQuote.isNotBlank())
                assertTrue("Lesson ${lesson.id} must have quiz questions", lesson.quizQuestions.isNotEmpty())

                for (q in lesson.quizQuestions) {
                    assertTrue("Question ${q.id} has empty questionText", q.questionText.isNotBlank())
                    assertTrue("Question ${q.id} must have at least 2 options", q.options.size >= 2)
                    assertTrue("Question ${q.id} correctOptionIndex out of bounds", q.correctOptionIndex in q.options.indices)
                    assertTrue("Question ${q.id} has empty explanation", q.explanation.isNotBlank())
                }
            }
        }
    }

    @Test
    fun testSahabaBiographiesWithLinkedHadithsIntegrity() {
        val sahaba = com.example.data.seed.SahabaBiographiesSeedData.ALL_SAHABA
        assertTrue("Should have at least 8 prominent companions", sahaba.size >= 8)

        for (s in sahaba) {
            assertTrue("Sahabi ${s.id} has empty nameEnglish", s.nameEnglish.isNotBlank())
            assertTrue("Sahabi ${s.id} has empty nameArabic", s.nameArabic.isNotBlank())
            assertTrue("Sahabi ${s.id} has empty honorificTitle", s.honorificTitle.isNotBlank())
            assertTrue("Sahabi ${s.id} has empty category", s.category.isNotBlank())
            assertTrue("Sahabi ${s.id} has empty famousQuote", s.famousQuote.isNotBlank())
            assertTrue("Sahabi ${s.id} must have majorTrials", s.majorTrials.isNotEmpty())
            assertTrue("Sahabi ${s.id} must have milestonesAndService", s.milestonesAndService.isNotEmpty())
            assertTrue("Sahabi ${s.id} must have linkedHadiths", s.linkedHadiths.isNotEmpty())

            for (ref in s.linkedHadiths) {
                assertTrue("Hadith ref collection is empty", ref.collection.isNotBlank())
                assertTrue("Hadith ref number must be positive", ref.hadithNumber > 0)
                assertTrue("Hadith ref narrator is empty", ref.narrator.isNotBlank())
                assertTrue("Hadith ref arabicText is empty", ref.arabicText.isNotBlank())
                assertTrue("Hadith ref translation is empty", ref.translation.isNotBlank())
                assertTrue("Hadith ref grade is empty", ref.grade.isNotBlank())
            }
        }
    }

    @Test
    fun testCommunitySeedDataIntegrity() {
        val posts = com.example.data.seed.CommunitySeedData.INITIAL_POSTS
        assertTrue("Initial community posts should not be empty", posts.isNotEmpty())

        for (post in posts) {
            assertTrue("Post ${post.id} title is empty", post.title.isNotBlank())
            assertTrue("Post ${post.id} content is empty", post.content.isNotBlank())
            assertTrue("Post ${post.id} authorName is empty", post.authorName.isNotBlank())
        }

        val duaRequests = com.example.data.seed.CommunitySeedData.INITIAL_DUA_REQUESTS
        assertTrue("Initial dua requests should not be empty", duaRequests.isNotEmpty())
        for (req in duaRequests) {
            assertTrue("Dua request ${req.id} title is empty", req.requestTitle.isNotBlank())
            assertTrue("Dua request ${req.id} details is empty", req.requestDetails.isNotBlank())
            assertTrue("Dua request ameenCount must be >= 1", req.ameenCount >= 1)
        }
    }

    @Test
    fun testAiSpiritualGuideEngineOffline() = kotlinx.coroutines.runBlocking {
        val engine = com.example.data.remote.AiSpiritualGuideEngine()

        val salahGuidance = engine.generatePersonalizedGuidance(
            focusArea = "salah_consistency",
            prayersCompletedToday = 5,
            fastingStreakDays = 7,
            userDisplayName = "Abdullah"
        )
        assertNotNull(salahGuidance)
        assertTrue(salahGuidance.title.isNotBlank())
        assertTrue(salahGuidance.quranAyahArabic.isNotBlank())
        assertTrue(salahGuidance.hadithArabic.isNotBlank())
        assertTrue(salahGuidance.recommendedDuaArabic.isNotBlank())
        assertTrue(salahGuidance.practicalDeeds.size >= 3)

        val fastingGuidance = engine.generatePersonalizedGuidance(
            focusArea = "fasting_vitality",
            prayersCompletedToday = 5,
            fastingStreakDays = 15,
            userDisplayName = "Fatima"
        )
        assertNotNull(fastingGuidance)
        assertTrue(fastingGuidance.fastingGuidance.isNotBlank())
    }

    @Test
    fun testDuaCategoryLibraryCoverage() {
        val categories = DuaSeedData.CATEGORY_METAS
        assertEquals("Should have 12 curated Dua categories", 12, categories.size)

        val duas = DuaSeedData.DUAS
        assertTrue("Should have extensive library with at least 30 Duas", duas.size >= 30)

        // Verify every category has at least one authentic Dua
        for (category in categories) {
            val matching = duas.filter { it.category.equals(category.name, ignoreCase = true) }
            assertTrue("Category ${category.name} must contain at least 1 Dua", matching.isNotEmpty())
            assertTrue("Category ${category.name} has empty arabicName", category.arabicName.isNotBlank())
            assertTrue("Category ${category.name} has empty description", category.description.isNotBlank())
        }
    }

    @Test
    fun testDuaSearchAndOrganization() {
        val duas = DuaSeedData.DUAS

        // Search by keyword in title/translation
        val forgivenessMatches = duas.filter {
            it.title.contains("Forgiveness", ignoreCase = true) ||
            it.translation.contains("forgive", ignoreCase = true) ||
            it.category.contains("Forgiveness", ignoreCase = true)
        }
        assertTrue("Should find forgiveness Duas", forgivenessMatches.isNotEmpty())

        // Search by Arabic text
        val rabMatches = duas.filter { it.arabicText.contains("رَبَّنَا") }
        assertTrue("Should find Duas containing Rabbana", rabMatches.isNotEmpty())

        // Verify bookmarking and folder assignment on DuaEntity
        val sampleDua = com.example.data.local.DuaEntity(
            id = "test_1",
            title = "Morning Shield",
            category = "Morning & Evening",
            arabicText = "بِسْمِ اللَّهِ",
            transliteration = "Bismillah",
            translation = "In the Name of Allah",
            explanation = "Shield from harm",
            reference = "Tirmidhi 3388",
            isFavorite = true,
            customFolder = "Daily Routine",
            userNote = "Recite every morning after Fajr"
        )

        assertTrue(sampleDua.isFavorite)
        assertEquals("Daily Routine", sampleDua.customFolder)
        assertEquals("Recite every morning after Fajr", sampleDua.userNote)
    }

    @Test
    fun testMirathCalculatorEstateAndShares() {
        val liabilities = com.example.data.model.EstateLiabilities(
            grossEstate = 100_000.0,
            funeralExpenses = 2_000.0,
            debtsOwed = 8_000.0,
            wasiyyahBequest = 10_000.0
        )
        // Net after debts = 100,000 - 2,000 - 8,000 = 90,000; wasiyyah (10,000 <= 30,000) -> Net = 80,000
        assertEquals(80_000.0, liabilities.netDistributableEstate, 0.01)

        // Case: Deceased Male, leaving Wife (1), Sons (1), Daughters (1), Parents deceased
        val heirs = com.example.data.model.HeirCounts(
            wifeCount = 1,
            sonsCount = 1,
            daughtersCount = 1,
            fatherAlive = false,
            motherAlive = false
        )
        val result = com.example.data.model.MirathCalculator.calculate(
            gender = com.example.data.model.DeceasedGender.MALE,
            liabilities = liabilities,
            heirs = heirs,
            madhab = com.example.data.model.Madhab.HANAFI
        )

        assertTrue(result.shares.isNotEmpty())
        // Wife should receive 1/8 of 80,000 = 10,000
        val wifeShare = result.shares.find { it.heirId == "wives" }
        assertNotNull(wifeShare)
        assertEquals(10_000.0, wifeShare!!.totalAmount, 0.5)

        // Remaining 70,000 is split 2:1 between son (2 parts) and daughter (1 part) -> 3 parts total
        val sonShare = result.shares.find { it.heirId == "sons" }
        val daughterShare = result.shares.find { it.heirId == "daughters_asabah" }
        assertNotNull(sonShare)
        assertNotNull(daughterShare)
        assertEquals(70_000.0 * 2.0 / 3.0, sonShare!!.totalAmount, 0.5)
        assertEquals(70_000.0 * 1.0 / 3.0, daughterShare!!.totalAmount, 0.5)

        // Total distributed should equal net estate
        assertEquals(liabilities.netDistributableEstate, result.totalDistributedAmount, 1.0)
    }

    @Test
    fun testHijriCalendarDataIntegrity() {
        val months = com.example.data.model.HijriCalendarData.HIJRI_MONTHS
        assertEquals(12, months.size)
        assertEquals("Muharram", months[0].englishName)
        assertEquals("Dhul Hijjah", months[11].englishName)

        val occasions = com.example.data.model.HijriCalendarData.SACRED_OCCASIONS
        assertTrue("Should have sacred Islamic occasions", occasions.size >= 8)
        for (occ in occasions) {
            assertTrue(occ.title.isNotBlank())
            assertTrue(occ.arabicTitle.isNotBlank())
            assertTrue(occ.virtues.isNotBlank())
        }

        val sunnahDays = com.example.data.model.HijriCalendarData.getSunnahFastsForMonth(9)
        assertTrue("Should have recommended Sunnah fast days for Ramadan/Month", sunnahDays.isNotEmpty())
        for (fast in sunnahDays) {
            assertTrue(fast.title.isNotBlank())
            assertTrue(fast.propheticVirtue.isNotBlank())
        }
    }

    @Test
    fun testHalalFoodGuideDataAndSearch() {
        val additives = com.example.data.model.HalalFoodData.ALL_E_NUMBERS
        assertTrue("Should have curated list of E-numbers", additives.isNotEmpty())

        // Verify status distribution
        val halalCount = additives.count { it.status == com.example.data.model.HalalStatus.HALAL }
        val haramCount = additives.count { it.status == com.example.data.model.HalalStatus.HARAM }
        val mushboohCount = additives.count { it.status == com.example.data.model.HalalStatus.MUSHBOOH }
        assertTrue("Should contain Halal additives", halalCount > 0)
        assertTrue("Should contain Haram additives", haramCount > 0)
        assertTrue("Should contain Mushbooh additives", mushboohCount > 0)

        // Test search query matching
        val gelatinMatch = additives.filter {
            it.code.contains("E441", ignoreCase = true) || it.name.contains("Gelatin", ignoreCase = true)
        }
        assertTrue("Should find Gelatin E441", gelatinMatch.isNotEmpty())
        assertEquals(com.example.data.model.HalalStatus.MUSHBOOH, gelatinMatch[0].status)
    }

    @Test
    fun testHaramainLiveChannelsAndData() {
        val channels = com.example.data.model.HaramainLiveData.CHANNELS
        assertEquals(2, channels.size)

        val makkahChannels = channels.filter { it.mosque == com.example.data.model.HolyMosque.MAKKAH_HARAM }
        val madinahChannels = channels.filter { it.mosque == com.example.data.model.HolyMosque.MADINAH_NABAWI }
        assertEquals(1, makkahChannels.size)
        assertEquals(1, madinahChannels.size)

        for (channel in channels) {
            assertTrue(channel.title.isNotBlank())
            assertTrue(channel.streamWebUrl.isNotBlank())
        }
    }

    @Test
    fun testKhatamPlannerPacingCalculations() {
        val presets = com.example.data.model.KhatamPlannerData.PRESETS
        assertEquals(4, presets.size)

        val preset30 = presets.find { it.targetDays == 30 }
        assertNotNull(preset30)
        assertEquals(30, preset30!!.targetDays)
        assertEquals(20.1, preset30.pagesPerDay, 0.1)
        assertEquals(4.0, preset30.pagesPerPrayer, 0.1)

        // Dua Khatm Quran
        val duaAr = com.example.data.model.KhatamPlannerData.KHATM_AL_QURAN_DUA_ARABIC
        val duaEn = com.example.data.model.KhatamPlannerData.KHATM_AL_QURAN_DUA_TRANSLATION
        assertTrue(duaAr.isNotBlank())
        assertTrue(duaEn.isNotBlank())
    }

    @Test
    fun testRuqyahSpiritualHealingVersesAndPropheticSteps() {
        val verses = com.example.data.model.RuqyahData.VERSES
        assertTrue("Should contain Ruqyah verses", verses.size >= 5)

        // Must include Al-Fatihah, Ayat al-Kursi, Al-Mu'awwidhat
        assertTrue(verses.any { it.title.contains("Fatihah", ignoreCase = true) })
        assertTrue(verses.any { it.title.contains("Kursi", ignoreCase = true) })
        assertTrue(verses.any { it.title.contains("Mu'awwidhat", ignoreCase = true) })

        val steps = com.example.data.model.RuqyahData.PROPHETIC_STEPS
        assertEquals(4, steps.size)
        for (step in steps) {
            assertTrue(step.title.isNotBlank())
            assertTrue(step.hadithProof.isNotBlank())
        }
    }

    @Test
    fun testBabyNamesDataAndMeaningIntegrity() {
        val names = com.example.data.model.BabyNamesData.ALL_NAMES
        assertTrue("Should have extensive curated baby names", names.isNotEmpty())

        val boysNames = names.filter { it.gender == com.example.data.model.BabyGender.BOY }
        val girlsNames = names.filter { it.gender == com.example.data.model.BabyGender.GIRL }
        assertTrue("Should have boy names", boysNames.isNotEmpty())
        assertTrue("Should have girl names", girlsNames.isNotEmpty())

        for (name in names) {
            assertTrue(name.name.isNotBlank())
            assertTrue(name.arabic.isNotBlank())
            assertTrue(name.meaning.isNotBlank())
            assertTrue(name.origin.isNotBlank())
        }
    }

    @Test
    fun testPrayerNotificationSettingsAndChannels() {
        val settings = com.example.notification.PrayerNotificationSettings(
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

        assertTrue(settings.alertsEnabled)
        assertTrue(settings.notifyFajr)
        assertFalse(settings.notifySunrise)
        assertEquals(5, settings.reminderOffsetMinutes)
        assertEquals(com.example.notification.AlertSoundMode.FULL_ADHAN, settings.soundMode)

        // Verify channel IDs
        assertEquals("prayer_alerts_channel", com.example.notification.PrayerNotificationHelper.CHANNEL_ID)
        assertEquals("prayer_confirmation_channel", com.example.notification.PrayerNotificationHelper.CHANNEL_CONFIRMATION_ID)
        assertEquals("fasting_completion_channel", com.example.notification.PrayerNotificationHelper.CHANNEL_FASTING_ID)
        assertEquals("quran_daily_reminders_channel", com.example.notification.PrayerNotificationHelper.CHANNEL_QURAN_ID)
    }

    @Test
    fun testQuranReminderSettingsAndPresets() {
        val defaultSettings = com.example.notification.QuranReminderSettings()
        assertTrue(defaultSettings.enabled)
        assertEquals(com.example.notification.QuranReminderTimePreset.NIGHT, defaultSettings.preset)
        assertEquals(21, defaultSettings.hour)
        assertEquals(30, defaultSettings.minute)
        assertEquals(4, defaultSettings.dailyPagesTarget)
        assertTrue(defaultSettings.notifyFridayKahf)
        assertTrue(defaultSettings.formattedTime.isNotBlank())

        // Test custom preset
        val fajrPreset = com.example.notification.QuranReminderTimePreset.AFTER_FAJR
        assertEquals(6, fajrPreset.hour)
        assertEquals(0, fajrPreset.minute)
        assertTrue(fajrPreset.title.contains("Fajr", ignoreCase = true))

        val customSettings = defaultSettings.copy(
            preset = com.example.notification.QuranReminderTimePreset.CUSTOM,
            hour = 8,
            minute = 15,
            dailyPagesTarget = 10
        )
        assertEquals(8, customSettings.hour)
        assertEquals(15, customSettings.minute)
        assertEquals(10, customSettings.dailyPagesTarget)
        assertTrue(customSettings.formattedTime.contains("08:15") || customSettings.formattedTime.contains("8:15"))
    }

    @Test
    fun testNotificationIntentActionsAndConstants() {
        assertEquals("com.example.deen365.ACTION_PRAYER_ALERT", com.example.notification.PrayerNotificationHelper.ACTION_PRAYER_ALERT)
        assertEquals("com.example.deen365.ACTION_MARK_PRAYED", com.example.notification.PrayerNotificationHelper.ACTION_MARK_PRAYED)
        assertEquals("com.example.deen365.ACTION_SNOOZE_PRAYER", com.example.notification.PrayerNotificationHelper.ACTION_SNOOZE_PRAYER)
        assertEquals("com.example.deen365.ACTION_FASTING_REMINDER", com.example.notification.PrayerNotificationHelper.ACTION_FASTING_REMINDER)
        assertEquals("com.example.deen365.ACTION_LOG_FAST_COMPLETED", com.example.notification.PrayerNotificationHelper.ACTION_LOG_FAST_COMPLETED)
        assertEquals("com.example.deen365.ACTION_QURAN_REMINDER", com.example.notification.PrayerNotificationHelper.ACTION_QURAN_REMINDER)
        assertEquals("com.example.deen365.ACTION_MARK_QURAN_READ", com.example.notification.PrayerNotificationHelper.ACTION_MARK_QURAN_READ)
        assertEquals("com.example.deen365.ACTION_SNOOZE_QURAN", com.example.notification.PrayerNotificationHelper.ACTION_SNOOZE_QURAN)
        assertEquals("com.example.deen365.ACTION_FRIDAY_KAHF_REMINDER", com.example.notification.PrayerNotificationHelper.ACTION_FRIDAY_KAHF_REMINDER)
    }

    @Test
    fun testHadithOfTheDayRepositoryAndItemMapping() {
        // Test deterministic selection
        val todayHadith = com.example.data.repository.HadithOfTheDayRepository.getTodayHadithSync(0)
        assertNotNull(todayHadith)
        assertTrue("Arabic text must not be empty", todayHadith.arabicText.isNotBlank())
        assertTrue("Translation must not be empty", todayHadith.translation.isNotBlank())
        assertTrue("Collection name must not be empty", todayHadith.collectionName.isNotBlank())
        assertTrue("Reference text must contain collection and number", todayHadith.referenceText.contains("#${todayHadith.hadithNumber}"))
        assertTrue("Practical lesson must not be empty", todayHadith.practicalLesson.isNotBlank())

        // Test offset navigation gives valid items
        val nextDayHadith = com.example.data.repository.HadithOfTheDayRepository.getTodayHadithSync(1)
        assertNotNull(nextDayHadith)
        assertTrue(nextDayHadith.translation.isNotBlank())

        val prevDayHadith = com.example.data.repository.HadithOfTheDayRepository.getTodayHadithSync(-1)
        assertNotNull(prevDayHadith)
        assertTrue(prevDayHadith.translation.isNotBlank())

        // Test practical lesson derivation for key themes
        val testEntity = com.example.data.local.HadithEntity(
            collection = "nawawi",
            bookNumber = 1,
            chapterName = "Actions by Intentions",
            hadithNumber = 1,
            arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ",
            translation = "Actions are judged by intentions",
            narrator = "Umar ibn Al-Khattab (RA)",
            grade = "Sahih"
        )
        val lesson = com.example.data.repository.HadithOfTheDayRepository.derivePracticalLesson(testEntity)
        assertTrue("Lesson should mention intentions", lesson.contains("intention", ignoreCase = true))
    }
}


