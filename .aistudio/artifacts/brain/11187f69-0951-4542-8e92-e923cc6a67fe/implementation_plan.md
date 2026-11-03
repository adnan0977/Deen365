# Implementation Plan: Tri-Corpus Deep Scholarship (Quran.com, Sunnah.com & Alim.org)

Deliver a balanced set of top scholarly modules requested by the user:
1. **Quran.com**: Word-by-Word Translation & Root Grammar Explorer (tri-consonant roots, morphology, parts of speech, and corpus occurrences).
2. **Sunnah.com**: Visual Sanad Narrator Chain Drilldown (interactive node tree from collector to Prophet ﷺ, transmission formulas, reliability grades, and narrator biographies).
3. **Alim.org**: Comparative Fiqh Matrix (4 Sunni Madhabs: Hanafi, Maliki, Shafi'i, Hanbali) and Asbab al-Nuzul (occasions of revelation).

---

## 1. User Review Required & Critical Design Decisions

> [!IMPORTANT]
> The user confirmed a **balanced combination across all three platforms** with specific emphasis on:
> - **Quran.com:** Word-by-Word Translation & Root Grammar
> - **Sunnah.com:** Visual Sanad Narrator Transmission Chains
> - **Alim.org:** Comparative Fiqh Rulings across the 4 Madhabs

- **No Breaking Changes:** Retains existing Room database schemas, notification services, prayer clocks, and test suites.
- **Material Design 3 Consistency:** All new cards, tree nodes, chips, and modals use standardized M3 tokens, custom elevation, 48dp minimum touch targets, and `Modifier.testTag` attributes.

---

## 2. Proposed Architecture & Module Specifications

```
+---------------------------------------------------------------------------------------------------------+
|                                    Tri-Corpus Scholarly Architecture                                    |
|                                                                                                         |
|  +--------------------------------+  +-------------------------------+  +----------------------------+  |
|  | Quran.com Module               |  | Sunnah.com Module             |  | Alim.org Module            |  |
|  | - WordGrammar & Root Model     |  | - SanadTransmissionChain Model|  | - Comparative Fiqh Matrix  |  |
|  | - Root Occurrences & Stem      |  | - NarratorNode Ranks & Dates  |  | - 4 Sunni Madhab Rulings   |  |
|  | - Interactive WbW Grammar Chip |  | - Visual Sanad Tree Modal     |  | - Asbab al-Nuzul Contexts  |  |
|  | - WordGrammarModalDialog       |  | - Transmission Link formulas  |  | - ComparativeFiqhDialog    |  |
|  +---------------+----------------+  +---------------+---------------+  +--------------+-------------+  |
|                  |                                   |                                 |                |
|                  +-----------------------------------+---------------------------------+                |
|                                                      v                                                  |
|                                                DeenViewModel                                            |
|                                - wbwGrammarDetail: WordGrammarDetail?                                   |
|                                - selectedSanadChain: SanadChain?                                        |
|                                - selectedFiqhSubject: FiqhTopic?                                        |
|                                                      |                                                  |
|                      +-------------------------------+-------------------------------+                  |
|                      |                               |                               |                  |
|                      v                               v                               v                  |
|              AyahItemCard & Reader          HadithReaderDetailView         IslamicHeritageScreen        |
|            - Tap word -> Grammar Sheet     - "Narration Chain" -> Tree     - "Comparative Fiqh" Button  |
+---------------------------------------------------------------------------------------------------------+
```

---

## 3. Detailed Component Plan

### A. Quran.com: Word-by-Word Translation & Root Grammar
1. **Data Model (`QuranGrammarModels.kt`):**
   - Extend `WordByWordItem` with:
     - `root`: Tri-consonant Arabic root (e.g., `ر-ح-م`, `ع-ب-د`, `ه-د-ي`)
     - `partOfSpeech`: Grammatical classification (Noun / *Ism*, Verb / *Fi'l*, Particle / *Harf*)
     - `morphology`: Case, tense, and person (e.g., *Genitive Masculine Plural*, *Imperfect Active Verb Form I*)
     - `lemma`: Base dictionary entry
     - `occurrenceCount`: How many times the root appears in the Quran (e.g., 560 times)
     - `derivedWords`: Related Quranic words sharing the root
2. **Seed Data (`QuranWordGrammarSeed.kt`):**
   - Detailed grammatical roots and morphology for Surah Al-Fatihah, Ayat al-Kursi, Surah Al-Baqarah (1-5), and key study passages.
3. **Interactive UI (`WordGrammarModalDialog.kt`):**
   - Bottom sheet / modal dialog displaying:
     - Large Arabic word with vowel markers and audio playback button
     - English word translation & phonetic transliteration
     - Root badge (e.g., "Root: ر - ح - م (R-H-M)")
     - Part of speech pill with grammatical case analysis (*I'rab*)
     - Meaning breakdown and root frequency in the Holy Quran
     - List of derivative words and other Ayahs sharing the root

### B. Sunnah.com: Visual Sanad Narrator Chain Drilldown
1. **Data Model (`SanadModels.kt`):**
   - `SanadChain`: ID, hadithId, collectionId, hadithNumber, list of `SanadNode`
   - `SanadNode`:
     - `narratorId`: Unique key
     - `nameEnglish` & `nameArabic`
     - `role`: *Compiler*, *Sheikh of Compiler*, *Intermediate Tabi'i*, *Senior Tabi'i*, *Companion (Sahabi)*, *Prophet ﷺ*
     - `generation`: 1 (Prophet), 2 (Sahaba), 3 (Tabi'in), 4 (Atba al-Tabi'in), 5 (Compilers)
     - `scholarlyGrading`: *Thiqah Thabit*, *Thiqah*, *Saduq*, *Sahabi Jalil*
     - `transmissionFormula`: *Haddathana* (narrated to us), *Akhbarana* (informed us), *'An* (on the authority of)
     - `yearPassedAH`: Year of death in Hijri calendar
     - `biography`: Brief summary of life and scholarly reliability
2. **Seed Data (`SanadChainSeedData.kt`):**
   - Complete chains for canonical hadiths:
     - Sahih Bukhari Hadith 1 (*Intentions*): Al-Humaydi -> Sufyan -> Yahya ibn Sa'id -> Muhammad ibn Ibrahim -> Alqama -> Umar ibn Al-Khattab (RA) -> Prophet Muhammad ﷺ
     - Sahih Bukhari Hadith 2 (*Revelation*): Abdullah ibn Yusuf -> Malik -> Hisham ibn Urwah -> Urwah -> Aisha (RA) -> Prophet Muhammad ﷺ
     - Sahih Muslim Hadith 1 (*Pillars of Islam / Hadith Jibril*): Abu Khaythama -> Waki' -> Kahmas -> Abdullah ibn Buraydah -> Yahya ibn Ya'mar -> Abdullah ibn Umar (RA) -> Umar ibn Al-Khattab (RA) -> Prophet Muhammad ﷺ
     - 40 Hadith Nawawi chains
3. **Interactive UI (`VisualSanadChainDialog.kt`):**
   - Fullscreen modal with vertical timeline/tree diagram:
     - Connecting vertical pipeline with arrows and transmission formulas (*Haddathana*, *'An*)
     - Interactive narrator cards showing Arabic/English names, role, and reliability badges
     - Tapping any narrator card expands the full biographical profile and credentials
     - Header summary indicating unbroken continuity (*Muttasil*) and authenticity rating (*Sahih*)
4. **Integration in `HadithReaderDetailView.kt`:**
   - Link the "Narration Chain" action in each hadith card directly to `onOpenSanadChain(hadith)`.

### C. Alim.org: Comparative Fiqh Matrix (4 Sunni Madhabs) & Asbab al-Nuzul
1. **Data Model (`AlimScholarshipModels.kt`):**
   - `ComparativeFiqhTopic`: ID, topic name (e.g., *Ablution / Wudu Requirements*, *Position of Hands in Salah*, *Qunut in Fajr*, *Zakat on Jewelry*, *Fasting Invalidator Rules*)
   - `MadhabRuling`:
     - `madhab`: Hanafi, Maliki, Shafi'i, Hanbali
     - `ruling`: Summary of the ruling (Fard, Sunnah, Makruh, Mubah, Haram)
     - `details`: Explanation of the school's jurisprudence
     - `dalil`: Evidentiary basis from Quran and Sunnah
     - `foundingImam`: Abu Hanifa, Malik ibn Anas, Ash-Shafi'i, Ahmad ibn Hanbal
   - `AsbabAlNuzulItem`: Surah number, Ayah range, context of revelation, historical event, classical source (Al-Wahidi / As-Suyuti)
2. **Seed Data (`ComparativeFiqhSeedData.kt` & `AsbabAlNuzulSeedData.kt`):**
   - Comprehensive comparative rulings across 6 essential daily topics.
   - Revelation contexts for major Surahs (Al-Fatihah, Al-Baqarah, Ali 'Imran, An-Nur, Al-Ikhlas, Al-Falaq, An-Nas).
3. **Interactive UI (`ComparativeFiqhDialog.kt` & `AsbabAlNuzulDialog.kt`):**
   - Tabbed / 4-column Madhab comparative viewer with side-by-side comparison chips and scriptural evidences.
   - Quick "Occasions of Revelation" chip on Ayah reader cards and in the Islamic Heritage Hub.

---

## 4. Step-by-Step Implementation Sequence

1. **Step 1: Data Models & Classical Seed Datasets**
   - Create `com.example.data.model.QuranGrammarModels.kt` (Word grammar, root analysis, morphology).
   - Create `com.example.data.seed.QuranWordGrammarSeed.kt`.
   - Create `com.example.data.model.SanadModels.kt` and `com.example.data.seed.SanadChainSeedData.kt`.
   - Create `com.example.data.seed.ComparativeFiqhSeedData.kt` and `AsbabAlNuzulSeedData.kt`.

2. **Step 2: Quran.com Word-by-Word Grammar Dialog & Ayah Integration**
   - Build `WordGrammarModalDialog.kt` with root extraction, grammar badges, and occurrence breakdown.
   - Update `AyahItemCard.kt` to bind words with their rich grammar metadata and open the dialog on word tap.

3. **Step 3: Sunnah.com Visual Sanad Transmission Tree**
   - Build `VisualSanadChainDialog.kt` with vertical node tree connectors, transmission verbs, and narrator bio inspection.
   - Wire `HadithReaderDetailView.kt` so tapping "Narration Chain" opens the visual sanad tree for the active hadith.

4. **Step 4: Alim.org Comparative Fiqh & Asbab al-Nuzul Explorer**
   - Build `ComparativeFiqhDialog.kt` with 4-Madhab school selector tabs, dalil references, and ruling badges.
   - Build `AsbabAlNuzulDialog.kt` displaying classical revelation context.
   - Integrate entry points into `HadithAndDuaScreen.kt`, `IslamicHeritageScreen.kt`, and `SurahReaderView.kt`.

5. **Step 5: ViewModel State & Compilation Verification**
   - Update `DeenViewModel.kt` with state holders and action handlers for grammar inspection, sanad trees, and fiqh comparison.
   - Run `compile_applet` to ensure zero compilation or type errors.
   - Run `gradle :app:testDebugUnitTest` to verify data integrity and local tests.
