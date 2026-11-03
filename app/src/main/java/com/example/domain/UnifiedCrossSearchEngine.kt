package com.example.domain

import com.example.data.seed.CaliphProfile
import com.example.data.seed.CaliphsAndSahabaSeedData
import com.example.data.seed.ProphetStory
import com.example.data.seed.ProphetsSeedData
import com.example.data.seed.SahabiProfile
import com.example.data.seed.IslamicHistoryEra
import com.example.data.seed.IslamicBookSeedData
import com.example.data.model.IslamicBook
import com.example.data.seed.HadithSeedData
import com.example.data.local.HadithEntity

enum class SearchCorpusType(val label: String) {
    ALL("All Resources"),
    QURAN("Quran"),
    HADITH("Hadith"),
    HERITAGE("Heritage & History"),
    BOOKS("Islamic Books")
}

data class UnifiedSearchResult(
    val corpusType: SearchCorpusType,
    val title: String,
    val subtitle: String,
    val snippet: String,
    val arabicSnippet: String? = null,
    val referenceId: String,
    val metadataBadge: String
)

object UnifiedCrossSearchEngine {

    fun search(query: String, corpusFilter: SearchCorpusType = SearchCorpusType.ALL): List<UnifiedSearchResult> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return emptyList()

        val results = mutableListOf<UnifiedSearchResult>()

        // 1. Search Islamic Heritage: Prophets
        if (corpusFilter == SearchCorpusType.ALL || corpusFilter == SearchCorpusType.HERITAGE) {
            ProphetsSeedData.ALL_PROPHETS.forEach { prophet ->
                val match = prophet.nameEnglish.lowercase().contains(q) ||
                        prophet.nameArabic.contains(q) ||
                        prophet.title.lowercase().contains(q) ||
                        prophet.summary.lowercase().contains(q) ||
                        prophet.keyAyahTranslation.lowercase().contains(q)

                if (match) {
                    results.add(
                        UnifiedSearchResult(
                            corpusType = SearchCorpusType.HERITAGE,
                            title = "${prophet.nameEnglish} (${prophet.nameArabic})",
                            subtitle = prophet.title,
                            snippet = prophet.summary,
                            arabicSnippet = prophet.keyAyahArabic,
                            referenceId = "prophet_${prophet.id}",
                            metadataBadge = "${prophet.quranicMentionsCount} Verses in Quran"
                        )
                    )
                }
            }

            // 2. Search Caliphs
            CaliphsAndSahabaSeedData.RIGHTLY_GUIDED_CALIPHS.forEach { caliph ->
                val match = caliph.nameEnglish.lowercase().contains(q) ||
                        caliph.title.lowercase().contains(q) ||
                        caliph.famousSpeechQuote.lowercase().contains(q) ||
                        caliph.virtues.any { it.lowercase().contains(q) } ||
                        caliph.majorAchievements.any { it.lowercase().contains(q) }

                if (match) {
                    results.add(
                        UnifiedSearchResult(
                            corpusType = SearchCorpusType.HERITAGE,
                            title = caliph.nameEnglish,
                            subtitle = caliph.title,
                            snippet = caliph.famousSpeechQuote,
                            arabicSnippet = caliph.nameArabic,
                            referenceId = "caliph_${caliph.id}",
                            metadataBadge = caliph.reignYears
                        )
                    )
                }
            }

            // 3. Search Sahaba
            CaliphsAndSahabaSeedData.ASHARA_AND_SAHABA.forEach { sahabi ->
                val match = sahabi.nameEnglish.lowercase().contains(q) ||
                        sahabi.honorific.lowercase().contains(q) ||
                        sahabi.virtuesAndRole.lowercase().contains(q) ||
                        sahabi.famousQuote.lowercase().contains(q)

                if (match) {
                    results.add(
                        UnifiedSearchResult(
                            corpusType = SearchCorpusType.HERITAGE,
                            title = sahabi.nameEnglish,
                            subtitle = sahabi.honorific,
                            snippet = sahabi.virtuesAndRole,
                            arabicSnippet = sahabi.nameArabic,
                            referenceId = "sahabi_${sahabi.id}",
                            metadataBadge = sahabi.category
                        )
                    )
                }
            }

            // 4. Search Historical Eras
            CaliphsAndSahabaSeedData.ISLAMIC_ERAS.forEach { era ->
                val match = era.name.lowercase().contains(q) ||
                        era.description.lowercase().contains(q) ||
                        era.keyMilestones.any { it.lowercase().contains(q) }

                if (match) {
                    results.add(
                        UnifiedSearchResult(
                            corpusType = SearchCorpusType.HERITAGE,
                            title = era.name,
                            subtitle = "Capital: ${era.capital} (${era.period})",
                            snippet = era.description,
                            referenceId = "era_${era.id}",
                            metadataBadge = "History Chronicle"
                        )
                    )
                }
            }
        }

        // 5. Search Hadiths (from Seed data)
        if (corpusFilter == SearchCorpusType.ALL || corpusFilter == SearchCorpusType.HADITH) {
            HadithSeedData.ALL_HADITHS.forEach { hadith ->
                val match = hadith.narrator.lowercase().contains(q) ||
                        hadith.translation.lowercase().contains(q) ||
                        hadith.collection.lowercase().contains(q) ||
                        hadith.arabicText.contains(q) ||
                        hadith.bookNumber.toString() == q

                if (match) {
                    results.add(
                        UnifiedSearchResult(
                            corpusType = SearchCorpusType.HADITH,
                            title = "${hadith.collection.uppercase()} - #${hadith.hadithNumber}",
                            subtitle = "Narrated by ${hadith.narrator}",
                            snippet = hadith.translation,
                            arabicSnippet = hadith.arabicText.take(120),
                            referenceId = "hadith_${hadith.collection}_${hadith.hadithNumber}",
                            metadataBadge = hadith.grade
                        )
                    )
                }
            }
        }

        // 6. Search Islamic Books
        if (corpusFilter == SearchCorpusType.ALL || corpusFilter == SearchCorpusType.BOOKS) {
            IslamicBookSeedData.FOUNDATIONAL_BOOKS.forEach { book ->
                val match = book.title.lowercase().contains(q) ||
                        book.author.lowercase().contains(q) ||
                        book.summary.lowercase().contains(q) ||
                        book.chapters.any { it.titleEnglish.lowercase().contains(q) || it.textContent.lowercase().contains(q) }

                if (match) {
                    results.add(
                        UnifiedSearchResult(
                            corpusType = SearchCorpusType.BOOKS,
                            title = book.title,
                            subtitle = "By ${book.author}",
                            snippet = book.summary,
                            arabicSnippet = book.arabicTitle,
                            referenceId = "book_${book.id}",
                            metadataBadge = book.category.displayName
                        )
                    )
                }
            }
        }

        return results
    }
}
