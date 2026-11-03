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
}

