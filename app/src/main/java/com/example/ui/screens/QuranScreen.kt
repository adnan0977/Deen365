package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.data.util.TajweedEngine
import com.example.ui.screens.hadith.HadithKitabBrowserDialog
import com.example.ui.screens.heritage.AlimThematicIndexDialog
import com.example.ui.screens.heritage.ComparativeFiqhDialog
import com.example.ui.screens.quran.AyahImageCardGeneratorDialog
import com.example.ui.screens.quran.GoToVerseDialog
import com.example.ui.screens.quran.QuranGoalsAndBookmarksDialog
import com.example.ui.screens.quran.QuranHighlightPaletteDialog
import com.example.ui.screens.quran.QuranMultiTranslationDialog
import com.example.ui.screens.quran.QuranSetGoalBookmarkDialog
import com.example.ui.screens.quran.RabbanaDuasDialog
import com.example.ui.screens.quran.SajdahVersesDialog
import com.example.ui.screens.quran.SurahIndexView
import com.example.ui.screens.quran.SurahReaderView
import com.example.ui.screens.quran.TajweedLegendSheet
import com.example.ui.screens.quran.TajweedRulePopupDialog
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel

@Composable
fun QuranScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    if (state.isQuranExplorerOpen) {
        com.example.ui.screens.quran.QuranExplorerView(
            initialVerseKey = state.quranExplorerVerseKey,
            onBack = { viewModel.closeQuranExplorer() },
            onPlayWordAudio = { viewModel.playWordAudio(it, "") },
            modifier = modifier
        )
        return
    }

    if (state.selectedSurah != null) {
        SurahReaderView(
            surah = state.selectedSurah,
            ayahs = state.currentSurahAyahs,
            isLoading = state.isLoadingAyahs,
            state = state,
            viewModel = viewModel,
            onBack = { viewModel.closeSurah() },
            modifier = modifier
        )
    } else {
        SurahIndexView(
            surahs = state.surahList,
            searchQuery = state.quranSearchQuery,
            onSearchChange = { viewModel.setQuranSearch(it) },
            onSelectSurah = { viewModel.openSurah(it) },
            dailyQuranGoalMinutes = state.dailyQuranGoalMinutes,
            todayReadingSeconds = state.todayReadingSeconds,
            quranStreakDays = state.quranStreakDays,
            isGoalCompletedToday = state.isGoalCompletedToday,
            onChangeGoal = {
                viewModel.openGoalBookmarksDialog()
            },
            onOpenThematicIndex = { viewModel.openAlimThematicIndex() },
            onOpenComparativeFiqh = { viewModel.openComparativeFiqh() },
            onOpenKitabBab = { viewModel.openKitabBabBrowser() },
            onOpenGharibGlossary = { viewModel.openGharibGlossary() },
            onOpenSettings = { viewModel.openSettings() },
            onOpenGoToVerse = { viewModel.openGoToVerseDialog() },
            onOpenRabbana = { viewModel.openRabbanaDialog() },
            onOpenSajdah = { viewModel.openSajdahVersesDialog() },
            onOpenTajweedLegend = { viewModel.toggleTajweedLegend() },
            onOpenQuranExplorer = { viewModel.openQuranExplorer(it) },
            modifier = modifier
        )
    }

    // Parity Dialogs
    RabbanaDuasDialog(
        isOpen = state.isRabbanaDialogOpen,
        onDismiss = { viewModel.closeRabbanaDialog() }
    )

    SajdahVersesDialog(
        isOpen = state.isSajdahVersesDialogOpen,
        onDismiss = { viewModel.closeSajdahVersesDialog() }
    )

    GoToVerseDialog(
        isOpen = state.isGoToVerseDialogOpen,
        onJump = { viewModel.jumpToVerseCoordinate(it) },
        onDismiss = { viewModel.closeGoToVerseDialog() }
    )

    TajweedLegendSheet(
        isOpen = state.isTajweedLegendOpen,
        onDismiss = { viewModel.toggleTajweedLegend() }
    )

    AyahImageCardGeneratorDialog(
        isOpen = state.isAyahImageCardDialogOpen,
        ayah = state.selectedAyahForCard,
        onDismiss = { viewModel.closeAyahImageCardDialog() }
    )

    // On-Verse Tajweed Rule Popup Dialog
    val tajweedAyah = state.selectedTajweedAyah
    if (state.isTajweedRulePopupOpen && tajweedAyah != null) {
        val occurrences = remember(tajweedAyah) {
            TajweedEngine.detectRules(tajweedAyah.arabicText)
        }
        TajweedRulePopupDialog(
            selectedRule = state.selectedTajweedRule,
            occurrences = occurrences,
            surahNumber = tajweedAyah.surahNumber,
            ayahNumber = tajweedAyah.ayahNumber,
            onDismiss = { viewModel.closeTajweedRulePopup() }
        )
    }

    // Alim.org Thematic Subject Index Dialog
    AlimThematicIndexDialog(
        isOpen = state.isAlimThematicIndexOpen,
        onDismiss = { viewModel.closeAlimThematicIndex() },
        onJumpToQuranCoordinate = { coordinate ->
            viewModel.closeAlimThematicIndex()
            viewModel.jumpToVerseCoordinate(coordinate)
        }
    )

    // 4-Madhhab Comparative Fiqh Dialog
    ComparativeFiqhDialog(
        isOpen = state.isComparativeFiqhOpen,
        onDismiss = { viewModel.closeComparativeFiqh() }
    )

    // Sunnah.com Kitab & Bab Browser Dialog
    HadithKitabBrowserDialog(
        isOpen = state.isKitabBabBrowserOpen,
        completedBookIds = state.completedHadithBookIds,
        collectionProgressList = state.collectionProgressList,
        onToggleBookCompleted = { viewModel.toggleHadithBookCompleted(it) },
        onMarkCollectionCompleted = { coll, completed -> viewModel.setCollectionCompleted(coll, completed) },
        onClearAllProgress = { viewModel.clearAllKitabProgress() },
        onSelectBook = { book ->
            viewModel.closeKitabBabBrowser()
            viewModel.setHadithCollectionBrowserFilter(book.collectionId)
            viewModel.setTab(com.example.ui.viewmodel.MainTab.HADITH_DUAS)
        },
        onDismiss = { viewModel.closeKitabBabBrowser() }
    )

    // Sunnah.com Gharib al-Hadith Glossary Dialog
    com.example.ui.screens.hadith.GharibHadithGlossaryDialog(
        isOpen = state.isGharibGlossaryOpen,
        searchQuery = state.gharibSearchQuery,
        selectedCategory = state.selectedGharibCategory,
        onSearchQueryChange = { viewModel.setGharibSearchQuery(it) },
        onCategoryChange = { viewModel.setGharibCategory(it) },
        onJumpToHadith = { coll, num -> viewModel.jumpFromGharibToHadith(coll, num) },
        onDismiss = { viewModel.closeGharibGlossary() }
    )

    // Multi-Translation Comparison Dialog
    QuranMultiTranslationDialog(
        isOpen = state.isMultiTranslationDialogOpen,
        primaryEdition = state.selectedTranslationEdition,
        comparisonEditionIds = state.comparisonTranslationIds,
        onSelectPrimary = { viewModel.setTranslationEdition(it) },
        onToggleComparisonEdition = { viewModel.toggleComparisonTranslation(it) },
        onDismiss = { viewModel.closeMultiTranslationDialog() }
    )

    // Verse Highlighting & Reflection Palette Dialog
    val highlightAyah = state.selectedAyahForHighlight
    val existingHighlight = remember(highlightAyah, state.quranHighlights) {
        highlightAyah?.let { a ->
            state.quranHighlights.firstOrNull { it.surahNumber == a.surahNumber && it.ayahNumber == a.ayahNumber }
        }
    }
    QuranHighlightPaletteDialog(
        isOpen = state.isHighlightPaletteOpen && highlightAyah != null,
        ayah = highlightAyah,
        surahName = state.selectedSurah?.englishName ?: (highlightAyah?.surahNumber?.toString() ?: ""),
        existingHighlight = existingHighlight,
        onSave = { hex, name, cat, note ->
            if (highlightAyah != null) {
                viewModel.saveHighlight(
                    highlightAyah.surahNumber,
                    highlightAyah.ayahNumber,
                    state.selectedSurah?.englishName ?: "${highlightAyah.surahNumber}",
                    hex,
                    name,
                    cat,
                    note
                )
            }
        },
        onRemove = {
            if (highlightAyah != null) {
                viewModel.removeHighlight(highlightAyah.surahNumber, highlightAyah.ayahNumber)
            }
        },
        onDismiss = { viewModel.closeHighlightPalette() }
    )

    // Set Daily Goal Bookmark Checkpoint Dialog
    val goalAyah = state.selectedAyahForGoalBookmark
    QuranSetGoalBookmarkDialog(
        isOpen = state.isSetGoalBookmarkDialogOpen && goalAyah != null,
        ayah = goalAyah,
        surahName = state.selectedSurah?.englishName ?: (goalAyah?.surahNumber?.toString() ?: ""),
        currentGoalMinutes = state.dailyQuranGoalMinutes,
        onSaveGoalBookmark = { label, mins, note ->
            if (goalAyah != null) {
                viewModel.saveGoalBookmark(
                    goalAyah.surahNumber,
                    goalAyah.ayahNumber,
                    state.selectedSurah?.englishName ?: "${goalAyah.surahNumber}",
                    label,
                    mins,
                    note
                )
            }
        },
        onDismiss = { viewModel.closeSetGoalBookmarkDialog() }
    )

    // Unified Goals, Bookmarks & Highlights Hub Dialog
    val bookmarkedAyahs = remember(state.currentSurahAyahs) {
        state.currentSurahAyahs.filter { it.isBookmarked }
    }
    QuranGoalsAndBookmarksDialog(
        isOpen = state.isGoalBookmarksDialogOpen,
        dailyGoalMinutes = state.dailyQuranGoalMinutes,
        todayReadingSeconds = state.todayReadingSeconds,
        quranStreakDays = state.quranStreakDays,
        goalBookmarks = state.quranGoalBookmarks,
        highlights = state.quranHighlights,
        bookmarkedAyahs = bookmarkedAyahs,
        onChangeDailyGoalMinutes = { viewModel.setDailyQuranGoal(it) },
        onToggleGoalCompleted = { id, isComp -> viewModel.toggleGoalBookmarkCompletion(id, isComp) },
        onDeleteGoalBookmark = { viewModel.deleteGoalBookmark(it) },
        onDeleteHighlight = { sNum, aNum -> viewModel.removeHighlight(sNum, aNum) },
        onJumpToVerse = { sNum, aNum ->
            viewModel.closeGoalBookmarksDialog()
            viewModel.jumpToVerseCoordinate("$sNum:$aNum")
        },
        onDismiss = { viewModel.closeGoalBookmarksDialog() }
    )
}
