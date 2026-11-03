package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AyahEntity
import com.example.data.local.DailyReflectionEntity
import com.example.data.local.DeenDatabase
import com.example.data.local.DhikrDailyLogEntity
import com.example.data.local.DhikrItemEntity
import com.example.data.local.FastingRecordEntity
import com.example.data.local.HadithEntity
import com.example.data.local.IslamicVideoEntity
import com.example.data.local.PrayerRecordEntity
import com.example.data.local.UserHabitEntity
import com.example.data.remote.AiCategory
import com.example.data.remote.AiGuidanceResult
import com.example.data.remote.GeminiSpiritualService
import com.example.data.repository.DailyReflectionRepository
import com.example.data.repository.DhikrRepository
import com.example.data.repository.HadithRepository
import com.example.data.repository.IslamicVideoRepository
import com.example.data.repository.PrayerTimesRepository
import com.example.data.repository.QuranRepository
import com.example.data.repository.TrackerRepository
import com.example.data.seed.DuaItem
import com.example.data.seed.DuaSeedData
import com.example.data.seed.HadithCollectionMeta
import com.example.data.seed.SurahMeta
import com.example.data.seed.TasbihPreset
import com.example.audio.AudioRecitationManager
import com.example.data.repository.SubscriptionPlanItem
import com.example.data.repository.UserProfile
import com.example.data.repository.UserRepository
import com.example.data.seed.NamazGuideData
import com.example.data.seed.NamazStep
import com.example.data.seed.PrayerGuide
import com.example.domain.CalculationMethod
import com.example.domain.CityPreset
import com.example.domain.DarkModeConfig
import com.example.domain.HijriDate
import com.example.audio.QuranAudioPlayer
import com.example.data.model.QuranAudioReciter
import com.example.data.model.QuranReadingMode
import com.example.data.model.TafsirItem
import com.example.data.model.TranslationEdition
import com.example.data.repository.TafsirRepository
import com.example.domain.QiblaTheme
import com.example.domain.TasbihTheme
import com.example.ui.model.HomeGridModule
import com.example.domain.IslamicCalendarHelper
import com.example.domain.PrayerSchedule
import com.example.domain.PrayerTimeCalculator
import com.example.domain.QiblaCalculator
import com.example.domain.QiblaDirection
import com.example.notification.AlertSoundMode
import com.example.notification.PrayerNotificationHelper
import com.example.notification.PrayerNotificationSchedulerService
import com.example.notification.PrayerNotificationSettings
import com.example.sensor.QiblaSensorData
import com.example.sensor.QiblaSensorManager
import com.example.ui.theme.AppThemeType
import com.example.data.audio.SalahAudioPlayer
import com.example.data.audio.SalahAudioPlaybackState
import com.example.data.audio.RepeatMode
import com.example.data.audio.ReciterProfile
import com.example.data.local.DuaEntity
import com.example.data.repository.DuaRepository
import com.example.data.repository.DeenCoinsRepository
import com.example.data.repository.DeenCoinsState
import com.example.data.repository.AdminOffer
import com.example.data.repository.NamazOfflineRepository
import com.example.data.repository.NamazDownloadProgress
import com.example.data.seed.NamazPrayerProcessData
import com.example.data.seed.DetailedPrayerPlan
import com.example.data.seed.HadithSeedData
import com.example.data.model.CommunityComment
import com.example.data.model.CommunityPost
import com.example.data.model.CommunityTopicCategory
import com.example.data.model.ContentReportItem
import com.example.data.model.DuaRequestItem
import com.example.data.repository.CommunityRepository
import com.example.data.remote.AiSpiritualGuideEngine
import com.example.data.remote.PersonalizedSpiritualGuidance
import com.example.data.seed.DetailedSahabiBiography
import com.example.data.seed.InteractiveLearningModulesData
import com.example.data.seed.LearningLesson
import com.example.data.seed.LearningTrack
import com.example.data.seed.SahabaBiographiesSeedData
import kotlin.math.abs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MainTab {
    DASHBOARD,
    QURAN,
    VIDEOS,
    AI_GUIDE,
    HADITH_DUAS,
    NAMAZ,
    QIBLA,
    FASTING,
    MOSQUES,
    TASBIH,
    NAMAZ_FASTING,
    ISLAMIC_HERITAGE,
    ISLAMIC_BOOKS,
    COMMUNITY,
    INTERACTIVE_LEARNING,
    SAHABA_BIOGRAPHIES,
    MORE,
    SETTINGS
}

enum class VideoFilterType {
    BY_SPEAKER,
    BY_TOPIC
}

data class QuranSuiteState(
    val isRabbanaDialogOpen: Boolean = false,
    val isSajdahVersesDialogOpen: Boolean = false,
    val isSurahInfoDialogOpen: Boolean = false,
    val isTajweedLegendOpen: Boolean = false,
    val isGoToVerseDialogOpen: Boolean = false,
    val isAyahImageCardDialogOpen: Boolean = false,
    val selectedAyahForCard: AyahEntity? = null,
    val hifzRepeatCount: Int = 1,
    val hifzPauseDelaySeconds: Int = 2,
    val isDualTranslationEnabled: Boolean = false,
    val isTajweedModeEnabled: Boolean = false,
    val selectedTajweedRule: com.example.data.util.TajweedRuleType? = null,
    val selectedTajweedAyah: AyahEntity? = null,
    val isTajweedRulePopupOpen: Boolean = false,
    val activeWordAudioText: String? = null,
    val dailyQuranGoalMinutes: Int = 15,
    val todayReadingSeconds: Int = 240,
    val quranStreakDays: Int = 5,
    val isGoalCompletedToday: Boolean = false,
    val comparisonTranslationIds: Set<String> = setOf("en.khattab", "en.sahih"),
    val isMultiTranslationDialogOpen: Boolean = false,
    val isGoalBookmarksDialogOpen: Boolean = false,
    val isHighlightPaletteOpen: Boolean = false,
    val selectedAyahForHighlight: AyahEntity? = null,
    val quranHighlights: List<com.example.data.local.QuranHighlightEntity> = emptyList(),
    val quranGoalBookmarks: List<com.example.data.local.QuranGoalBookmarkEntity> = emptyList(),
    val isSetGoalBookmarkDialogOpen: Boolean = false,
    val selectedAyahForGoalBookmark: AyahEntity? = null,
    val selectedWordGrammarDetail: com.example.data.model.WordGrammarDetail? = null,
    val isWordGrammarDialogOpen: Boolean = false,
    val selectedAsbabAlNuzul: com.example.data.model.AsbabAlNuzulItem? = null,
    val isAsbabAlNuzulDialogOpen: Boolean = false,
    val isQuranExplorerOpen: Boolean = false,
    val quranExplorerVerseKey: String = "1:1"
)

data class SunnahAlimSuiteState(
    val isGharibGlossaryOpen: Boolean = false,
    val gharibSearchQuery: String = "",
    val selectedGharibCategory: String = "All Categories",
    val selectedGharibEntry: com.example.data.seed.GharibGlossaryEntry? = null,
    val isImamBiographiesOpen: Boolean = false,
    val selectedNarratorDetail: com.example.data.seed.NarratorDetail? = null,
    val isKitabBabBrowserOpen: Boolean = false,
    val isGoToHadithDialogOpen: Boolean = false,
    val isSanadChainVisible: Boolean = false,
    val completedHadithBookIds: Set<String> = emptySet(),
    val isAlimThematicIndexOpen: Boolean = false,
    val isComparativeFiqhOpen: Boolean = false,
    val collectionProgressList: List<com.example.data.repository.CollectionProgress> = emptyList(),
    val selectedHadithCollectionDetail: String? = null,
    val selectedHadithChapterNumber: Int? = null,
    val isMarkReadTutorialOpen: Boolean = false,
    val hadithReadingStreak: Int = 1,
    val selectedSanadChain: com.example.data.model.SanadChain? = null,
    val isVisualSanadDialogOpen: Boolean = false,
    val selectedComparativeFiqhTopicId: String? = null
)

data class DeenUiState(
    val currentTab: MainTab = MainTab.DASHBOARD,
    val previousTab: MainTab = MainTab.DASHBOARD,
    val isSettingsDialogOpen: Boolean = false,
    val selectedBottomTabs: List<MainTab> = listOf(
        MainTab.DASHBOARD,
        MainTab.NAMAZ,
        MainTab.AI_GUIDE,
        MainTab.QURAN,
        MainTab.SETTINGS
    ),
    val selectedThemeType: AppThemeType = AppThemeType.EMERALD,
    val darkModeConfig: DarkModeConfig = DarkModeConfig.SYSTEM,
    val isDarkMode: Boolean = false,
    val userProfile: UserProfile? = null,
    val isUserAccountDialogOpen: Boolean = false,
    val isAdminDialogOpen: Boolean = false,
    val subscriptionPlans: List<SubscriptionPlanItem> = emptyList(),
    val showSubscriptionRequiredDialog: Boolean = false,
    val isFastingToday: Boolean = false,
    val fastingTrackerDaysCount: Int = 14,
    val fastingMonthTotalDays: Int = 30,
    val fastingStreak: Int = 4,
    val qiblaSensorData: QiblaSensorData = QiblaSensorData(),
    val qiblaDirection: QiblaDirection = QiblaCalculator.calculateQibla(PrayerTimeCalculator.POPULAR_CITIES[0].latitude, PrayerTimeCalculator.POPULAR_CITIES[0].longitude),
    val isDuaGridView: Boolean = true,
    val selectedLearningDua: DuaItem? = null,

    // Prayer & Fasting
    val selectedCity: CityPreset = PrayerTimeCalculator.POPULAR_CITIES[0],
    val selectedCalculationMethod: CalculationMethod = CalculationMethod.KARACHI,
    val asrJuristicMethod: com.example.domain.AsrJuristicMethod = com.example.domain.AsrJuristicMethod.STANDARD,
    val isUsingDeviceLocation: Boolean = false,
    val isDetectingLocation: Boolean = false,
    val prayerSchedule: PrayerSchedule = PrayerTimeCalculator.calculateSchedule(PrayerTimeCalculator.POPULAR_CITIES[0], method = CalculationMethod.KARACHI),
    val hijriDate: HijriDate = IslamicCalendarHelper.getTodayHijriDate(),
    val qiblaBearing: Float = 0f,
    val prayerRecord: PrayerRecordEntity = PrayerRecordEntity(dateStr = ""),
    val prayerHistory: List<PrayerRecordEntity> = emptyList(),
    val prayerStats: com.example.sync.HistoricalPrayerStats = com.example.sync.HistoricalPrayerStats(),
    val prayerSyncStatus: com.example.sync.PrayerSyncStatus = com.example.sync.PrayerSyncStatus.Idle,
    val lastPrayerSyncTime: Long = 0L,
    val isSyncingPrayers: Boolean = false,
    val isFastedToday: Boolean = false,
    val todayFastingRecord: FastingRecordEntity? = null,
    val fastingHistory: List<FastingRecordEntity> = emptyList(),
    val fastingStats: TrackerRepository.FastingHistoryStats = TrackerRepository.FastingHistoryStats(),
    val totalFastedDaysCount: Int = 0,

    // Prayer Notification & Adhan Alert System
    val prayerAlertsEnabled: Boolean = true,
    val notifyFajr: Boolean = true,
    val notifySunrise: Boolean = false,
    val notifyDhuhr: Boolean = true,
    val notifyAsr: Boolean = true,
    val notifyMaghrib: Boolean = true,
    val notifyIsha: Boolean = true,
    val notifySunsetFasting: Boolean = true,
    val reminderOffsetMinutes: Int = 0,
    val alertSoundMode: AlertSoundMode = AlertSoundMode.FULL_ADHAN,
    val nextScheduledAlertText: String = "",
    val schedulerServiceStatus: String = "Location-Aware Prayer Notifications Active",

    // Qibla Live Compass (Magnetometer & Accelerometer)
    val deviceHeading: Float = 0f,
    val isCompassSensorAvailable: Boolean = true,
    val compassAccuracy: Int = 3,
    val isDeviceFlat: Boolean = true,
    val isQiblaAligned: Boolean = false,
    val angleDifferenceToQibla: Float = 0f,

    // Quran
    val surahList: List<SurahMeta> = emptyList(),
    val quranSearchQuery: String = "",
    val selectedSurah: SurahMeta? = null,
    val currentSurahAyahs: List<AyahEntity> = emptyList(),
    val isLoadingAyahs: Boolean = false,

    // Hadith
    val hadithCollections: List<HadithCollectionMeta> = emptyList(),
    val selectedCollectionId: String = "nawawi",
    val selectedBookNumber: Int = 1,
    val currentHadiths: List<HadithEntity> = emptyList(),
    val allHadiths: List<HadithEntity> = emptyList(),
    val isLoadingHadiths: Boolean = false,
    val hadithFavoritesOnly: Boolean = false,
    val hadithStudiedOnly: Boolean = false,
    val hadithMemorizedOnly: Boolean = false,
    val hadithStudiedCount: Int = 0,
    val hadithMemorizedCount: Int = 0,
    val hadithFavoriteCount: Int = 0,
    val hadithSearchQuery: String = "",
    val selectedHadithCategory: String = "All",
    val selectedHadithNarrator: String = "All",
    val hadithBrowserCollection: String = "All",
    val hadithBrowserCategories: List<String> = HadithSeedData.CATEGORIES,
    val hadithBrowserNarrators: List<String> = HadithSeedData.PROMINENT_NARRATORS,

    // Duas & Tasbih
    val selectedDuaCategory: String = "All",
    val duasList: List<DuaItem> = DuaSeedData.DUAS,
    val duaEntities: List<DuaEntity> = emptyList(),
    val duaFavoritesOnly: Boolean = false,
    val duaMemorizedOnly: Boolean = false,
    val duaMemorizedCount: Int = 0,
    val duaFavoriteCount: Int = 0,
    val duaSearchQuery: String = "",
    val selectedDuaFolder: String = "All",
    val duaCustomFolders: List<String> = emptyList(),
    val duaViewModeTab: Int = 0, // 0: Categories Explorer, 1: All Invocations, 2: Bookmarks & Folders
    val selectedDuaForOrganizing: DuaEntity? = null,
    val hadithDuaActiveSubTab: Int = 0, // 0: Hadith, 1: Duas, 2: Namaz Guide, 3: Tasbih
    val tasbihPresets: List<TasbihPreset> = DuaSeedData.TASBIH_PRESETS,
    val selectedTasbih: TasbihPreset = DuaSeedData.TASBIH_PRESETS[0],
    val tasbihCount: Int = 0,
    val tasbihLap: Int = 1,
    val totalTasbihCount: Int = 0,
    val duaProgressCounts: Map<String, Int> = emptyMap(),

    // Interactive Namaz & Dua Audio Guide
    val prayerGuides: List<PrayerGuide> = NamazGuideData.PRAYERS,
    val selectedPrayerGuide: PrayerGuide = NamazGuideData.PRAYERS[0],
    val namazSteps: List<NamazStep> = NamazGuideData.STEPS,
    val currentNamazStepIndex: Int = 0,
    val isNamazGuideActive: Boolean = false,
    val playingAudioId: String? = null,

    // Daily Quranic Reflection Widget (Room Cached)
    val dailyQuranicReflection: DailyReflectionEntity? = null,
    val viewingReflectionDetails: DailyReflectionEntity? = null,
    val isLoadingReflection: Boolean = false,

    // AI Guidance
    val aiCategory: AiCategory = AiCategory.DAILY_REFLECTION,
    val aiInputQuery: String = "",
    val isLoadingAi: Boolean = false,
    val currentAiGuidance: AiGuidanceResult? = null,
    val completedActionSteps: Set<Int> = emptySet(),
    val aiGuidanceHistory: List<AiGuidanceResult> = emptyList(),

    // Islamic Videos, Admin & AI Habits
    val videoFilterType: VideoFilterType = VideoFilterType.BY_SPEAKER,
    val selectedSpeakerFilter: String = "All Speakers",
    val selectedTopicFilter: String = "All Topics",
    val videoSearchQuery: String = "",
    val allVideos: List<IslamicVideoEntity> = emptyList(),
    val filteredVideos: List<IslamicVideoEntity> = emptyList(),
    val customVideos: List<IslamicVideoEntity> = emptyList(),
    val isAdminLoggedIn: Boolean = false,
    val userHabit: UserHabitEntity = UserHabitEntity(),
    val isGeneratingAiHabit: Boolean = false,
    val aiHabitInsight: String = "",

    // Dhikr Tracking Module
    val dhikrItems: List<DhikrItemEntity> = emptyList(),
    val selectedDhikrId: String = "subhanallah",
    val todayDhikrLogs: Map<String, DhikrDailyLogEntity> = emptyMap(),
    val todayTotalDhikrCount: Int = 0,
    val dhikrVibrationEnabled: Boolean = true,
    val dhikrSoundEnabled: Boolean = true,

    // Deen Coins & Subscription Economy
    val coinsState: DeenCoinsState = DeenCoinsState(),
    val isCoinsHubOpen: Boolean = false,
    val isAdminOffersOpen: Boolean = false,
    val isReciterDialogOpen: Boolean = false,

    // Home Screen Reorderable Grid
    val homeGridModuleIds: List<String> = listOf("quran", "namaz", "ai_guide", "fasting", "community", "learning", "sahaba", "tasbih", "hadith_duas", "qibla", "heritage", "books", "mosques", "videos", "rewards"),
    val isHomeGridReorderingMode: Boolean = false,

    // Namaz Audio & Learning State
    val salahAudioState: SalahAudioPlaybackState = SalahAudioPlaybackState(),
    val selectedPrayerProcessId: String = "fajr",
    val isNamazLearnModeActive: Boolean = false,
    val isDeenAiSheetOpen: Boolean = false,
    val isBottomMenuCustomizerOpen: Boolean = false,
    val premiumFeaturePromptText: String? = null,

    // Offline Namaz Learning & Room DB Storage
    val namazDownloadStates: Map<String, NamazDownloadProgress> = emptyMap(),
    val downloadedPrayerIds: Set<String> = emptySet(),
    val totalNamazCachedSizeBytes: Long = 0L,
    val isDownloadingAllNamaz: Boolean = false,

    // Qibla & Tasbih Visual Themes
    val selectedQiblaTheme: QiblaTheme = QiblaTheme.EMERALD_GOLD,
    val selectedTasbihTheme: TasbihTheme = TasbihTheme.EMERALD_GOLD,

    // Quran Audio Recitation & Study Suite
    val currentPlayingSurah: Int? = null,
    val currentPlayingAyahNumber: Int? = null,
    val isQuranAudioPlaying: Boolean = false,
    val selectedReciter: QuranAudioReciter = QuranAudioReciter.MISHARY,
    val quranPlaybackSpeed: Float = 1.0f,
    val selectedTranslationEdition: TranslationEdition = TranslationEdition.CLEAR_QURAN,
    val isWordByWordEnabled: Boolean = false,
    val arabicFontSizeSp: Int = 26,
    val quranReadingMode: QuranReadingMode = QuranReadingMode.TRANSLATION_LIST,
    val activeTafsirAyah: AyahEntity? = null,
    val activeTafsirData: TafsirItem? = null,
    val isTafsirOpen: Boolean = false,
    val isQuranDisplaySettingsOpen: Boolean = false,
    val isReciterPickerOpen: Boolean = false,

    // Admin Pro Module Gating
    val proModuleIds: Set<String> = setOf("mosques"),
    val proModuleUnlockPrompt: HomeGridModule? = null,

    // Mosque Timings & Custom Alarms
    val isMosqueTimingsDialogOpen: Boolean = false,
    val isGlobalMosqueActive: Boolean = false,
    val mosqueTimings: List<com.example.domain.MosquePrayerTiming> = emptyList(),
    val mosqueName: String = "My Local Masjid",

    // AI Quran Verse Explainer
    val isAyahExplainerOpen: Boolean = false,
    val activeExplainingAyah: AyahEntity? = null,
    val ayahExplanationResult: com.example.data.model.AyahExplanationResult? = null,
    val isLoadingAyahExplanation: Boolean = false,

    // Hadith & Dua AI Companion
    val hadithDuaAiResult: AiGuidanceResult? = null,
    val isLoadingHadithDuaAi: Boolean = false,

    // Personalized Spiritual Guidance
    val personalizedGuidanceResult: AiGuidanceResult? = null,
    val isLoadingPersonalizedGuidance: Boolean = false,

    // Islamic Heritage (Alim.org)
    val selectedProphetStory: com.example.data.seed.ProphetStory? = null,
    val selectedCaliphProfile: com.example.data.seed.CaliphProfile? = null,
    val selectedSahabiProfile: com.example.data.seed.SahabiProfile? = null,
    val isUnifiedSearchOpen: Boolean = false,
    val unifiedSearchQuery: String = "",
    val unifiedSearchResults: List<com.example.domain.UnifiedSearchResult> = emptyList(),
    val unifiedSearchFilter: com.example.domain.SearchCorpusType = com.example.domain.SearchCorpusType.ALL,

    // Islamic Books Library & Reader
    val islamicBooksList: List<com.example.data.model.IslamicBook> = com.example.data.seed.IslamicBookSeedData.FOUNDATIONAL_BOOKS,
    val selectedBook: com.example.data.model.IslamicBook? = null,
    val currentReadingChapterIndex: Int = 0,
    val readerFontSizeSp: Int = 18,
    val readerTheme: String = "light", // "light", "sepia", "midnight"
    val isBookDrawerOpen: Boolean = false,
    val isCrawlingBooks: Boolean = false,

    // Fasting Assistant Extended
    val isFastingAssistantTabActive: Boolean = false,
    val isSunnahReminderEnabled: Boolean = true,

    // Quran & Sunnah/Alim Extended Sub-States
    val quranSuiteState: QuranSuiteState = QuranSuiteState(),
    val sunnahAlimState: SunnahAlimSuiteState = SunnahAlimSuiteState(),

    // Community Suite
    val communityPosts: List<CommunityPost> = emptyList(),
    val communityDuaRequests: List<DuaRequestItem> = emptyList(),
    val communityComments: Map<String, List<CommunityComment>> = emptyMap(),
    val communityReports: List<ContentReportItem> = emptyList(),
    val selectedCommunityCategory: CommunityTopicCategory? = null,
    val isCreatePostDialogOpen: Boolean = false,
    val isCreateDuaDialogOpen: Boolean = false,
    val activeCommentsPostId: String? = null,
    val reportingPostId: String? = null,
    val isModerationPanelOpen: Boolean = false,

    // Interactive Learning Modules
    val learningTracks: List<LearningTrack> = InteractiveLearningModulesData.ALL_TRACKS,
    val selectedLearningTrackId: String? = null,
    val activeLearningLessonId: String? = null,
    val activeQuizLessonId: String? = null,
    val activeQuizAnswers: Map<String, Int> = emptyMap(),
    val isQuizSubmitted: Boolean = false,
    val completedLessonIds: Set<String> = emptySet(),

    // Sahaba Biographies
    val sahabaList: List<DetailedSahabiBiography> = SahabaBiographiesSeedData.ALL_SAHABA,
    val selectedSahabiId: String? = null,
    val sahabaFilterCategory: String = "All",

    // AI-Powered Spiritual Guide
    val aiSpiritualGuidance: PersonalizedSpiritualGuidance? = null,
    val isAiSpiritualGuideLoading: Boolean = false,
    val aiGuideFocusArea: String = "salah_consistency",
    val completedPracticalDeeds: Set<Int> = emptySet()
) {
    // Delegated getters for Quran Suite
    val isRabbanaDialogOpen: Boolean get() = quranSuiteState.isRabbanaDialogOpen
    val isSajdahVersesDialogOpen: Boolean get() = quranSuiteState.isSajdahVersesDialogOpen
    val isSurahInfoDialogOpen: Boolean get() = quranSuiteState.isSurahInfoDialogOpen
    val isTajweedLegendOpen: Boolean get() = quranSuiteState.isTajweedLegendOpen
    val isGoToVerseDialogOpen: Boolean get() = quranSuiteState.isGoToVerseDialogOpen
    val isAyahImageCardDialogOpen: Boolean get() = quranSuiteState.isAyahImageCardDialogOpen
    val selectedAyahForCard: AyahEntity? get() = quranSuiteState.selectedAyahForCard
    val hifzRepeatCount: Int get() = quranSuiteState.hifzRepeatCount
    val hifzPauseDelaySeconds: Int get() = quranSuiteState.hifzPauseDelaySeconds
    val isDualTranslationEnabled: Boolean get() = quranSuiteState.isDualTranslationEnabled
    val isTajweedModeEnabled: Boolean get() = quranSuiteState.isTajweedModeEnabled
    val selectedTajweedRule: com.example.data.util.TajweedRuleType? get() = quranSuiteState.selectedTajweedRule
    val selectedTajweedAyah: AyahEntity? get() = quranSuiteState.selectedTajweedAyah
    val isTajweedRulePopupOpen: Boolean get() = quranSuiteState.isTajweedRulePopupOpen
    val activeWordAudioText: String? get() = quranSuiteState.activeWordAudioText
    val dailyQuranGoalMinutes: Int get() = quranSuiteState.dailyQuranGoalMinutes
    val todayReadingSeconds: Int get() = quranSuiteState.todayReadingSeconds
    val quranStreakDays: Int get() = quranSuiteState.quranStreakDays
    val isGoalCompletedToday: Boolean get() = quranSuiteState.isGoalCompletedToday
    val comparisonTranslationIds: Set<String> get() = quranSuiteState.comparisonTranslationIds
    val isMultiTranslationDialogOpen: Boolean get() = quranSuiteState.isMultiTranslationDialogOpen
    val isGoalBookmarksDialogOpen: Boolean get() = quranSuiteState.isGoalBookmarksDialogOpen
    val isHighlightPaletteOpen: Boolean get() = quranSuiteState.isHighlightPaletteOpen
    val selectedAyahForHighlight: AyahEntity? get() = quranSuiteState.selectedAyahForHighlight
    val quranHighlights: List<com.example.data.local.QuranHighlightEntity> get() = quranSuiteState.quranHighlights
    val quranGoalBookmarks: List<com.example.data.local.QuranGoalBookmarkEntity> get() = quranSuiteState.quranGoalBookmarks
    val isSetGoalBookmarkDialogOpen: Boolean get() = quranSuiteState.isSetGoalBookmarkDialogOpen
    val selectedAyahForGoalBookmark: AyahEntity? get() = quranSuiteState.selectedAyahForGoalBookmark
    val selectedWordGrammarDetail: com.example.data.model.WordGrammarDetail? get() = quranSuiteState.selectedWordGrammarDetail
    val isWordGrammarDialogOpen: Boolean get() = quranSuiteState.isWordGrammarDialogOpen
    val selectedAsbabAlNuzul: com.example.data.model.AsbabAlNuzulItem? get() = quranSuiteState.selectedAsbabAlNuzul
    val isAsbabAlNuzulDialogOpen: Boolean get() = quranSuiteState.isAsbabAlNuzulDialogOpen
    val isQuranExplorerOpen: Boolean get() = quranSuiteState.isQuranExplorerOpen
    val quranExplorerVerseKey: String get() = quranSuiteState.quranExplorerVerseKey

    // Delegated getters for Sunnah & Alim Suite
    val isGharibGlossaryOpen: Boolean get() = sunnahAlimState.isGharibGlossaryOpen
    val gharibSearchQuery: String get() = sunnahAlimState.gharibSearchQuery
    val selectedGharibCategory: String get() = sunnahAlimState.selectedGharibCategory
    val selectedGharibEntry: com.example.data.seed.GharibGlossaryEntry? get() = sunnahAlimState.selectedGharibEntry
    val isImamBiographiesOpen: Boolean get() = sunnahAlimState.isImamBiographiesOpen
    val selectedNarratorDetail: com.example.data.seed.NarratorDetail? get() = sunnahAlimState.selectedNarratorDetail
    val isKitabBabBrowserOpen: Boolean get() = sunnahAlimState.isKitabBabBrowserOpen
    val isGoToHadithDialogOpen: Boolean get() = sunnahAlimState.isGoToHadithDialogOpen
    val isSanadChainVisible: Boolean get() = sunnahAlimState.isSanadChainVisible
    val completedHadithBookIds: Set<String> get() = sunnahAlimState.completedHadithBookIds
    val isAlimThematicIndexOpen: Boolean get() = sunnahAlimState.isAlimThematicIndexOpen
    val isComparativeFiqhOpen: Boolean get() = sunnahAlimState.isComparativeFiqhOpen
    val collectionProgressList: List<com.example.data.repository.CollectionProgress> get() = sunnahAlimState.collectionProgressList
    val selectedHadithCollectionDetail: String? get() = sunnahAlimState.selectedHadithCollectionDetail
    val selectedHadithChapterNumber: Int? get() = sunnahAlimState.selectedHadithChapterNumber
    val isMarkReadTutorialOpen: Boolean get() = sunnahAlimState.isMarkReadTutorialOpen
    val hadithReadingStreak: Int get() = sunnahAlimState.hadithReadingStreak
    val selectedSanadChain: com.example.data.model.SanadChain? get() = sunnahAlimState.selectedSanadChain
    val isVisualSanadDialogOpen: Boolean get() = sunnahAlimState.isVisualSanadDialogOpen
    val selectedComparativeFiqhTopicId: String? get() = sunnahAlimState.selectedComparativeFiqhTopicId
}

class DeenViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DeenDatabase.getDatabase(application)
    private val quranRepo = QuranRepository(db.ayahDao(), db.quranStudyDao(), viewModelScope)
    private val hadithRepo = HadithRepository(db.hadithDao(), viewModelScope)
    val kitabProgressRepo = com.example.data.repository.KitabProgressRepository(db.kitabCompletionDao(), db.hadithKitabTrackerDao(), viewModelScope)
    private val duaRepo = DuaRepository(db.duaDao(), viewModelScope)
    private val trackerRepo = TrackerRepository(db.trackerDao())
    private val reflectionRepo = DailyReflectionRepository(db.dailyReflectionDao(), db.ayahDao(), viewModelScope)
    private val prayerTimesRepo = PrayerTimesRepository(db.prayerTimeDao())
    private val videoRepo = IslamicVideoRepository(db.islamicVideoDao(), db.userHabitDao(), viewModelScope)
    private val dhikrRepo = DhikrRepository(db.dhikrDao(), viewModelScope)
    private val qiblaSensorManager = QiblaSensorManager(application)
    private val audioManager = AudioRecitationManager(application)
    private val quranAudioPlayer = QuranAudioPlayer(application)
    val userRepo = UserRepository(viewModelScope)
    val deenCoinsRepo = DeenCoinsRepository(application)
    val salahAudioPlayer = SalahAudioPlayer.getInstance(application)
    val namazOfflineRepo = NamazOfflineRepository(db.namazDao(), salahAudioPlayer, application)
    val bookCrawlerService = com.example.data.remote.IslamicBookCrawlerService()
    val communityRepo = CommunityRepository(viewModelScope)
    val aiSpiritualGuideEngine = AiSpiritualGuideEngine()

    private val _uiState = MutableStateFlow(DeenUiState())
    val uiState: StateFlow<DeenUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        observeTracker()
        observeQiblaSensors()
        observeDailyReflection()
        observeAudioPlayback()
        observeVideos()
        observeUserHabits()
        observeDhikrTracker()
        observeUserProfile()
        observeDeenCoins()
        observeSalahAudio()
        observeNamazOffline()
        observeHadithLearningProgress()
        observeKitabProgress()
        observeDuas()
        observeCommunityData()
        loadHomeGridOrder()
        loadThemePreferences()
        autoCachePrayerIfNeeded(_uiState.value.selectedPrayerProcessId)
        loadPersonalizedSpiritualGuidance()
        observeQuranStudyData()
    }

    private fun observeQuranStudyData() {
        viewModelScope.launch {
            quranRepo.getAllHighlights().collect { highlights ->
                _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(quranHighlights = highlights)) }
            }
        }
        viewModelScope.launch {
            quranRepo.getAllGoalBookmarks().collect { goals ->
                _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(quranGoalBookmarks = goals)) }
            }
        }
    }

    private fun observeDeenCoins() {
        viewModelScope.launch {
            deenCoinsRepo.coinsState.collect { coins ->
                _uiState.update { it.copy(coinsState = coins) }
            }
        }
    }

    private fun observeSalahAudio() {
        viewModelScope.launch {
            salahAudioPlayer.playbackState.collect { audioState ->
                _uiState.update { it.copy(salahAudioState = audioState) }
            }
        }
    }

    private fun observeNamazOffline() {
        viewModelScope.launch {
            namazOfflineRepo.downloadStates.collect { states ->
                _uiState.update { it.copy(namazDownloadStates = states) }
            }
        }
        viewModelScope.launch {
            namazOfflineRepo.downloadedPrayerIds.collect { ids ->
                _uiState.update { it.copy(downloadedPrayerIds = ids.toSet()) }
            }
        }
        viewModelScope.launch {
            namazOfflineRepo.totalCachedSizeBytes.collect { bytes ->
                _uiState.update { it.copy(totalNamazCachedSizeBytes = bytes ?: 0L) }
            }
        }
    }

    private fun observeHadithLearningProgress() {
        viewModelScope.launch {
            hadithRepo.allHadiths.collect { list ->
                _uiState.update { it.copy(allHadiths = list) }
            }
        }
        viewModelScope.launch {
            hadithRepo.getFavoriteHadithsCount().collect { count ->
                _uiState.update { it.copy(hadithFavoriteCount = count) }
            }
        }
        viewModelScope.launch {
            hadithRepo.getStudiedCount().collect { count ->
                _uiState.update { it.copy(hadithStudiedCount = count) }
            }
        }
        viewModelScope.launch {
            hadithRepo.getMemorizedCount().collect { count ->
                _uiState.update { it.copy(hadithMemorizedCount = count) }
            }
        }
    }

    private fun observeKitabProgress() {
        viewModelScope.launch {
            kitabProgressRepo.completedBookIds.collect { set ->
                _uiState.update {
                    it.copy(
                        sunnahAlimState = it.sunnahAlimState.copy(completedHadithBookIds = set)
                    )
                }
            }
        }
        viewModelScope.launch {
            kitabProgressRepo.collectionProgressList.collect { list ->
                _uiState.update {
                    it.copy(
                        sunnahAlimState = it.sunnahAlimState.copy(collectionProgressList = list)
                    )
                }
            }
        }
    }

    private fun observeDuas() {
        viewModelScope.launch {
            duaRepo.getAllDuas().collect { list ->
                _uiState.update { it.copy(duaEntities = list) }
            }
        }
        viewModelScope.launch {
            duaRepo.getMemorizedCount().collect { count ->
                _uiState.update { it.copy(duaMemorizedCount = count) }
            }
        }
        viewModelScope.launch {
            duaRepo.getFavoriteCount().collect { count ->
                _uiState.update { it.copy(duaFavoriteCount = count) }
            }
        }
        viewModelScope.launch {
            duaRepo.getCustomFolders().collect { folders ->
                _uiState.update { it.copy(duaCustomFolders = folders) }
            }
        }
    }

    private fun observeCommunityData() {
        viewModelScope.launch {
            communityRepo.posts.collect { posts ->
                _uiState.update { it.copy(communityPosts = posts) }
            }
        }
        viewModelScope.launch {
            communityRepo.duaRequests.collect { duas ->
                _uiState.update { it.copy(communityDuaRequests = duas) }
            }
        }
        viewModelScope.launch {
            communityRepo.comments.collect { comments ->
                _uiState.update { it.copy(communityComments = comments) }
            }
        }
        viewModelScope.launch {
            communityRepo.reports.collect { reports ->
                _uiState.update { it.copy(communityReports = reports) }
            }
        }
    }

    private fun loadHomeGridOrder() {
        val prefs = getApplication<Application>().getSharedPreferences("home_grid_prefs", Context.MODE_PRIVATE)
        val defaultCsv = "quran,namaz,ai_guide,fasting,community,learning,sahaba,tasbih,hadith_duas,qibla,heritage,books,mosques,videos,rewards"
        val savedCsv = prefs.getString("grid_order_csv", defaultCsv) ?: defaultCsv
        val list = savedCsv.split(",").filter { it.isNotBlank() }
        val validIds = listOf("quran", "namaz", "ai_guide", "fasting", "community", "learning", "sahaba", "tasbih", "hadith_duas", "qibla", "heritage", "books", "mosques", "videos", "rewards")
        val finalList = (list.filter { validIds.contains(it) } + validIds).distinct()
        _uiState.update { it.copy(homeGridModuleIds = finalList) }
    }

    private fun loadThemePreferences() {
        val prefs = getApplication<Application>().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val savedThemeName = prefs.getString("app_theme", AppThemeType.EMERALD.name)
        val themeType = try {
            AppThemeType.valueOf(savedThemeName ?: AppThemeType.EMERALD.name)
        } catch (e: Exception) {
            AppThemeType.EMERALD
        }
        val savedMode = DarkModeConfig.fromId(prefs.getString("dark_mode_config", DarkModeConfig.SYSTEM.id))
        val savedQiblaThemeId = prefs.getString("qibla_theme", null)
        val savedTasbihThemeId = prefs.getString("tasbih_theme", null)

        val qiblaTheme = savedQiblaThemeId?.let { QiblaTheme.fromId(it) } ?: QiblaTheme.EMERALD_GOLD
        val tasbihTheme = savedTasbihThemeId?.let { TasbihTheme.fromId(it) } ?: TasbihTheme.EMERALD_GOLD

        val isDark = (savedMode == DarkModeConfig.DARK)

        _uiState.update {
            it.copy(
                selectedThemeType = themeType,
                darkModeConfig = savedMode,
                isDarkMode = isDark,
                selectedQiblaTheme = if (isDark && qiblaTheme == QiblaTheme.EMERALD_GOLD) QiblaTheme.CELESTIAL_MIDNIGHT else qiblaTheme,
                selectedTasbihTheme = if (isDark && tasbihTheme == TasbihTheme.EMERALD_GOLD) TasbihTheme.CELESTIAL_MIDNIGHT else tasbihTheme
            )
        }
    }

    fun toggleHomeGridReorderMode() {
        _uiState.update { it.copy(isHomeGridReorderingMode = !it.isHomeGridReorderingMode) }
        triggerHaptic(40)
    }

    fun moveHomeGridModuleUp(moduleId: String) {
        val current = _uiState.value.homeGridModuleIds.toMutableList()
        val index = current.indexOf(moduleId)
        if (index > 0) {
            val item = current.removeAt(index)
            current.add(index - 1, item)
            saveHomeGridOrder(current)
            _uiState.update { it.copy(homeGridModuleIds = current) }
            triggerHaptic(30)
        }
    }

    fun moveHomeGridModuleDown(moduleId: String) {
        val current = _uiState.value.homeGridModuleIds.toMutableList()
        val index = current.indexOf(moduleId)
        if (index in 0 until current.size - 1) {
            val item = current.removeAt(index)
            current.add(index + 1, item)
            saveHomeGridOrder(current)
            _uiState.update { it.copy(homeGridModuleIds = current) }
            triggerHaptic(30)
        }
    }

    fun resetHomeGridOrder() {
        val defaultOrder = listOf("quran", "namaz", "ai_guide", "fasting", "community", "learning", "sahaba", "tasbih", "hadith_duas", "qibla", "heritage", "books", "mosques", "videos", "rewards")
        saveHomeGridOrder(defaultOrder)
        _uiState.update { it.copy(homeGridModuleIds = defaultOrder) }
        triggerHaptic(50)
    }

    private fun saveHomeGridOrder(order: List<String>) {
        val prefs = getApplication<Application>().getSharedPreferences("home_grid_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("grid_order_csv", order.joinToString(",")).apply()
    }

    // --- Deen Coins & Subscription Actions ---
    fun openCoinsHub() {
        deenCoinsRepo.checkAndRefreshPremiumStatus()
        _uiState.update { it.copy(isCoinsHubOpen = true, premiumFeaturePromptText = null) }
        triggerHaptic(30)
    }

    fun closeCoinsHub() {
        _uiState.update { it.copy(isCoinsHubOpen = false, premiumFeaturePromptText = null) }
    }

    fun openAdminOffers() {
        _uiState.update { it.copy(isAdminOffersOpen = true) }
        triggerHaptic(30)
    }

    fun closeAdminOffers() {
        _uiState.update { it.copy(isAdminOffersOpen = false) }
    }

    fun purchasePremiumWithCoins(hours: Int) {
        val success = deenCoinsRepo.purchasePremiumWithCoins(hours)
        if (success) {
            triggerHaptic(90)
        }
    }

    fun toggleAdminOffer(offerId: String, isEnabled: Boolean) {
        deenCoinsRepo.toggleOfferEnabled(offerId, isEnabled)
        triggerHaptic(30)
    }

    fun grantAdminTestCoins(amount: Int) {
        deenCoinsRepo.adminGrantTestCoins(amount)
        triggerHaptic(50)
    }

    fun toggleAdminInstantPremium() {
        deenCoinsRepo.adminToggleInstantPremium()
        triggerHaptic(60)
    }

    fun resetAdminCoins() {
        deenCoinsRepo.adminResetCoins()
        triggerHaptic(70)
    }

    fun openReciterDialog() {
        _uiState.update { it.copy(isReciterDialogOpen = true) }
    }

    fun closeReciterDialog() {
        _uiState.update { it.copy(isReciterDialogOpen = false) }
    }

    fun rewardQuranChapterCompleted(surahName: String, surahNumber: Int = 1) {
        deenCoinsRepo.rewardQuranChapterCompleted(surahNumber, surahName)
        triggerHaptic(50)
    }

    fun rewardHadithChapterCompleted(title: String) {
        deenCoinsRepo.rewardHadithChapterCompleted(title, title)
        triggerHaptic(50)
    }

    // --- Namaz Audio Learning & Repeat Engine ---
    fun setSalahRepeatMode(mode: RepeatMode) {
        salahAudioPlayer.setRepeatMode(mode)
        triggerHaptic(30)
    }

    fun setSalahReciter(reciter: ReciterProfile) {
        salahAudioPlayer.setSelectedReciter(reciter)
        triggerHaptic(30)
    }

    fun playSalahStepAudio(audioId: String, title: String, arabicText: String, streamUrl: String? = null, localFilePath: String? = null) {
        salahAudioPlayer.playRecitation(audioId, title, arabicText, streamUrl, localFilePath)
        triggerHaptic(35)
    }

    fun stopSalahAudio() {
        salahAudioPlayer.stopAudio()
    }

    fun selectPrayerProcess(prayerId: String) {
        _uiState.update { it.copy(selectedPrayerProcessId = prayerId) }
        triggerHaptic(30)
        autoCachePrayerIfNeeded(prayerId)
    }

    fun autoCachePrayerIfNeeded(prayerId: String) {
        viewModelScope.launch {
            val isCached = namazOfflineRepo.isPrayerDownloaded(prayerId)
            val currentState = _uiState.value.namazDownloadStates[prayerId]
            if (!isCached && (currentState == null || !currentState.isDownloading)) {
                namazOfflineRepo.cachePrayerForOffline(prayerId)
            }
        }
    }

    fun downloadPrayerForOffline(prayerId: String) {
        viewModelScope.launch {
            namazOfflineRepo.cachePrayerForOffline(prayerId)
            triggerHaptic(40)
        }
    }

    fun downloadAllPrayersForOffline() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDownloadingAllNamaz = true) }
            namazOfflineRepo.downloadAllPrayers()
            _uiState.update { it.copy(isDownloadingAllNamaz = false) }
            triggerHaptic(60)
        }
    }

    fun clearPrayerOfflineCache(prayerId: String) {
        viewModelScope.launch {
            namazOfflineRepo.clearPrayerCache(prayerId)
            triggerHaptic(30)
        }
    }

    fun clearAllNamazOfflineCache() {
        viewModelScope.launch {
            namazOfflineRepo.clearAllNamazCache()
            triggerHaptic(50)
        }
    }

    fun setNamazLearnMode(active: Boolean) {
        _uiState.update { it.copy(isNamazLearnModeActive = active) }
        if (!active) {
            salahAudioPlayer.stopAudio()
        }
        triggerHaptic(35)
    }

    fun openDeenAiSheet() {
        _uiState.update { it.copy(isDeenAiSheetOpen = true) }
        triggerHaptic(40)
    }

    fun closeDeenAiSheet() {
        _uiState.update { it.copy(isDeenAiSheetOpen = false) }
    }

    fun openBottomMenuCustomizer() {
        if (!_uiState.value.coinsState.isPremium) {
            _uiState.update {
                it.copy(
                    isCoinsHubOpen = true,
                    premiumFeaturePromptText = "Bottom navigation customization is exclusive to Premium. Unlock with 24 Deen Coins (or complete 5 Namaz today)!"
                )
            }
            return
        }
        _uiState.update { it.copy(isBottomMenuCustomizerOpen = true) }
    }

    fun closeBottomMenuCustomizer() {
        _uiState.update { it.copy(isBottomMenuCustomizerOpen = false) }
    }

    fun clearRecentCoinRewardMessage() {
        deenCoinsRepo.clearRecentRewardMessage()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userRepo.userProfile.collect { profile ->
                _uiState.update { it.copy(userProfile = profile) }
                if (profile != null) {
                    com.example.sync.PrayerSyncService.startSync(getApplication())
                }
            }
        }
        viewModelScope.launch {
            userRepo.subscriptionPlans.collect { plans ->
                _uiState.update { it.copy(subscriptionPlans = plans) }
            }
        }
    }

    private fun observeAudioPlayback() {
        viewModelScope.launch {
            audioManager.currentPlayingId.collect { playingId ->
                _uiState.update { it.copy(playingAudioId = playingId) }
            }
        }
    }

    fun playRecitation(id: String, arabicText: String, transliteration: String = "") {
        audioManager.playRecitation(id, arabicText, transliteration)
        triggerHaptic(30)
    }

    fun stopRecitation() {
        audioManager.stop()
    }

    fun toggleRecitation(id: String, arabicText: String, transliteration: String = "") {
        if (_uiState.value.playingAudioId == id) {
            audioManager.stop()
        } else {
            audioManager.playRecitation(id, arabicText, transliteration)
            triggerHaptic(35)
        }
    }

    // --- Interactive Namaz Guide ---
    fun selectPrayerGuide(prayerId: String) {
        val guide = NamazGuideData.PRAYERS.find { it.id == prayerId } ?: NamazGuideData.PRAYERS[0]
        _uiState.update {
            it.copy(
                selectedPrayerGuide = guide,
                currentNamazStepIndex = 0
            )
        }
        audioManager.stop()
        triggerHaptic(30)
    }

    fun goToNamazStep(stepIndex: Int) {
        val totalSteps = NamazGuideData.STEPS.size
        val safeIndex = stepIndex.coerceIn(0, totalSteps - 1)
        _uiState.update { it.copy(currentNamazStepIndex = safeIndex) }
        audioManager.stop()
        triggerHaptic(30)
    }

    fun nextNamazStep() {
        val current = _uiState.value.currentNamazStepIndex
        val totalSteps = NamazGuideData.STEPS.size
        if (current < totalSteps - 1) {
            goToNamazStep(current + 1)
        }
    }

    fun prevNamazStep() {
        val current = _uiState.value.currentNamazStepIndex
        if (current > 0) {
            goToNamazStep(current - 1)
        }
    }

    fun setNamazGuideActive(active: Boolean) {
        _uiState.update { it.copy(isNamazGuideActive = active) }
        if (!active) {
            audioManager.stop()
        }
    }

    private fun loadInitialData() {
        val initialCity = PrayerTimeCalculator.POPULAR_CITIES[0]
        val schedule = PrayerTimeCalculator.calculateSchedule(initialCity)
        val bearing = QiblaCalculator.calculateQiblaBearing(initialCity.latitude, initialCity.longitude)
        qiblaSensorManager.setLocation(initialCity.latitude, initialCity.longitude)
        val hijri = IslamicCalendarHelper.getTodayHijriDate()
        val surahs = quranRepo.getSurahList()
        val collections = hadithRepo.getCollections()

        // Load default offline AI reflection
        val initialReflection = GeminiSpiritualService.getOfflineSpiritualWisdom(AiCategory.DAILY_REFLECTION, "")

        val app = getApplication<Application>()
        val initialMosqueName = com.example.notification.MosqueTimingHelper.getMosqueName(app)
        val isGlobalMosque = com.example.notification.MosqueTimingHelper.isGlobalMosqueActive(app)
        val initialTimings = com.example.notification.MosqueTimingHelper.getMosqueTimings(app, schedule)

        val initialHadithAi = com.example.data.remote.GeminiHadithDuaCompanionService.getOfflineHadithDuaForTheme("Anxiety & Peace of Mind")
        val initialPersonalGuidance = com.example.data.remote.GeminiPersonalizedGuidanceService.getOfflinePersonalizedGuidance("Fasting active: true, 5/5 prayers", "Servant of Allah")

        _uiState.update {
            it.copy(
                selectedCity = initialCity,
                prayerSchedule = schedule,
                qiblaBearing = bearing,
                hijriDate = hijri,
                surahList = surahs,
                hadithCollections = collections,
                currentAiGuidance = initialReflection,
                aiGuidanceHistory = listOf(initialReflection),
                mosqueName = initialMosqueName,
                isGlobalMosqueActive = isGlobalMosque,
                mosqueTimings = initialTimings,
                hadithDuaAiResult = initialHadithAi,
                personalizedGuidanceResult = initialPersonalGuidance
            )
        }

        // Preload default Hadiths for 40 Nawawi
        selectHadithBook("nawawi", 1)

        // Store daily prayer times in Room for offline access
        viewModelScope.launch {
            prayerTimesRepo.calculateAndStorePrayerTimes(
                latitude = initialCity.latitude,
                longitude = initialCity.longitude,
                timezone = initialCity.timezone,
                cityName = initialCity.name
            )
        }

        // Initialize prayer notifications & alarms
        updatePrayerNotificationSchedule(schedule)
    }

    private fun observeTracker() {
        val today = trackerRepo.getTodayDateStr()
        viewModelScope.launch {
            trackerRepo.seedHistoricalDataIfEmpty()
            trackerRepo.seedHistoricalFastingDataIfEmpty()
            trackerRepo.getPrayerRecord(today).collect { record ->
                val rec = record ?: PrayerRecordEntity(dateStr = today)
                _uiState.update {
                    it.copy(prayerRecord = rec)
                }
                deenCoinsRepo.checkAndReward5Namaz(rec.fajr, rec.dhuhr, rec.asr, rec.maghrib, rec.isha, today)
            }
        }
        viewModelScope.launch {
            trackerRepo.getAllPrayerRecords().collect { allRecords ->
                val stats = trackerRepo.calculateHistoricalStats(allRecords)
                _uiState.update {
                    it.copy(
                        prayerHistory = allRecords,
                        prayerStats = stats
                    )
                }
            }
        }
        viewModelScope.launch {
            com.example.sync.PrayerSyncService.syncStatus.collect { status ->
                _uiState.update {
                    it.copy(
                        prayerSyncStatus = status,
                        isSyncingPrayers = status is com.example.sync.PrayerSyncStatus.Syncing
                    )
                }
            }
        }
        viewModelScope.launch {
            com.example.sync.PrayerSyncService.lastSyncTime.collect { time ->
                _uiState.update { it.copy(lastPrayerSyncTime = time) }
            }
        }
        viewModelScope.launch {
            trackerRepo.getFastingRecord(today).collect { fastingRecord ->
                val isFasted = fastingRecord?.isFasted == true
                _uiState.update {
                    it.copy(
                        isFastedToday = isFasted,
                        todayFastingRecord = fastingRecord
                    )
                }
                if (isFasted) {
                    deenCoinsRepo.checkAndRewardFasting(true, today)
                }
            }
        }
        viewModelScope.launch {
            trackerRepo.getAllFastingRecords().collect { allRecords ->
                val stats = trackerRepo.calculateFastingStats(allRecords)
                _uiState.update {
                    it.copy(
                        fastingHistory = allRecords,
                        fastingStats = stats
                    )
                }
            }
        }
        viewModelScope.launch {
            trackerRepo.getTotalFastedDaysCount().collect { count ->
                _uiState.update { it.copy(totalFastedDaysCount = count) }
            }
        }
    }

    private fun observeQiblaSensors() {
        viewModelScope.launch {
            qiblaSensorManager.sensorData.collect { sensorData ->
                val qibla = _uiState.value.qiblaBearing
                val heading = if (sensorData.declination != 0f) sensorData.trueHeading else sensorData.headingDegrees

                var diff = (qibla - heading) % 360f
                if (diff > 180f) diff -= 360f
                if (diff < -180f) diff += 360f

                val isAligned = abs(diff) <= 3.5f

                if (isAligned && !_uiState.value.isQiblaAligned) {
                    triggerHaptic(90)
                }

                _uiState.update {
                    it.copy(
                        qiblaSensorData = sensorData,
                        deviceHeading = heading,
                        isCompassSensorAvailable = sensorData.isSensorAvailable,
                        compassAccuracy = sensorData.accuracy,
                        isDeviceFlat = sensorData.isFlat,
                        isQiblaAligned = isAligned,
                        angleDifferenceToQibla = diff
                    )
                }
            }
        }
    }

    fun startQiblaCompass() {
        qiblaSensorManager.startListening()
    }

    fun stopQiblaCompass() {
        qiblaSensorManager.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        qiblaSensorManager.stopListening()
        audioManager.shutdown()
        quranAudioPlayer.shutdown()
    }

    fun setTab(tab: MainTab) {
        val prev = _uiState.value.currentTab
        _uiState.update {
            it.copy(
                currentTab = tab,
                previousTab = if (tab != MainTab.SETTINGS && tab != MainTab.MORE) tab else prev
            )
        }
        if (tab == MainTab.NAMAZ_FASTING || tab == MainTab.QIBLA) {
            startQiblaCompass()
        } else {
            stopQiblaCompass()
        }
        if (tab == MainTab.NAMAZ || tab == MainTab.NAMAZ_FASTING) {
            autoCachePrayerIfNeeded(_uiState.value.selectedPrayerProcessId)
        }
    }

    fun openSettings() {
        val current = _uiState.value.currentTab
        _uiState.update {
            it.copy(
                previousTab = if (current != MainTab.SETTINGS) current else it.previousTab,
                isSettingsDialogOpen = true
            )
        }
    }

    fun closeSettings() {
        _uiState.update { it.copy(isSettingsDialogOpen = false) }
    }

    fun updateBottomTabs(tabs: List<MainTab>) {
        if (!_uiState.value.coinsState.isPremium) {
            _uiState.update {
                it.copy(
                    isCoinsHubOpen = true,
                    premiumFeaturePromptText = "Customizing bottom navigation order is a Premium feature. Unlock Pro with 24 Deen Coins (or complete 5 Namaz today)!"
                )
            }
            return
        }
        val sanitized = tabs.distinct().take(5)
        val finalTabs = if (sanitized.size == 5) sanitized else listOf(
            MainTab.DASHBOARD,
            MainTab.NAMAZ,
            MainTab.AI_GUIDE,
            MainTab.QURAN,
            MainTab.SETTINGS
        )
        _uiState.update { it.copy(selectedBottomTabs = finalTabs) }
        viewModelScope.launch {
            val currentHabit = _uiState.value.userHabit
            val updated = currentHabit.copy(selectedBottomTabsCsv = finalTabs.joinToString(",") { it.name })
            videoRepo.saveUserHabit(updated)
        }
    }

    // --- Prayer & Fasting ---
    fun selectCalculationMethod(method: CalculationMethod) {
        val city = _uiState.value.selectedCity
        val asrMethod = _uiState.value.asrJuristicMethod
        val isGps = _uiState.value.isUsingDeviceLocation
        val schedule = PrayerTimeCalculator.calculateScheduleForCoordinates(
            latitude = city.latitude,
            longitude = city.longitude,
            timezone = city.timezone,
            cityName = city.name,
            method = method,
            asrMethod = asrMethod,
            isUsingGpsLocation = isGps
        )
        _uiState.update {
            it.copy(
                selectedCalculationMethod = method,
                prayerSchedule = schedule
            )
        }
        updatePrayerNotificationSchedule(schedule)
    }

    fun selectAsrJuristicMethod(asrMethod: com.example.domain.AsrJuristicMethod) {
        val city = _uiState.value.selectedCity
        val method = _uiState.value.selectedCalculationMethod
        val isGps = _uiState.value.isUsingDeviceLocation
        val schedule = PrayerTimeCalculator.calculateScheduleForCoordinates(
            latitude = city.latitude,
            longitude = city.longitude,
            timezone = city.timezone,
            cityName = city.name,
            method = method,
            asrMethod = asrMethod,
            isUsingGpsLocation = isGps
        )
        _uiState.update {
            it.copy(
                asrJuristicMethod = asrMethod,
                prayerSchedule = schedule
            )
        }
        updatePrayerNotificationSchedule(schedule)
    }

    fun selectCity(city: CityPreset) {
        val method = _uiState.value.selectedCalculationMethod
        val asrMethod = _uiState.value.asrJuristicMethod
        val schedule = PrayerTimeCalculator.calculateScheduleForCoordinates(
            latitude = city.latitude,
            longitude = city.longitude,
            timezone = city.timezone,
            cityName = city.name,
            method = method,
            asrMethod = asrMethod,
            isUsingGpsLocation = false
        )
        val bearing = QiblaCalculator.calculateQiblaBearing(city.latitude, city.longitude)
        val qiblaDir = QiblaCalculator.calculateQibla(city.latitude, city.longitude)
        qiblaSensorManager.setLocation(city.latitude, city.longitude)
        val heading = _uiState.value.deviceHeading
        var diff = (bearing - heading) % 360f
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f
        _uiState.update {
            it.copy(
                selectedCity = city,
                prayerSchedule = schedule,
                qiblaBearing = bearing,
                qiblaDirection = qiblaDir,
                angleDifferenceToQibla = diff,
                isQiblaAligned = abs(diff) <= 4.0f,
                isUsingDeviceLocation = false
            )
        }
        updatePrayerNotificationSchedule(schedule)

        // Store daily prayer times in Room for offline access
        viewModelScope.launch {
            prayerTimesRepo.calculateAndStorePrayerTimes(
                latitude = city.latitude,
                longitude = city.longitude,
                timezone = city.timezone,
                cityName = city.name,
                method = method
            )
        }
    }

    fun detectAndApplyDeviceLocation(context: android.content.Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDetectingLocation = true) }
            try {
                val result = com.example.domain.DeviceLocationProvider.getDeviceLocation(context)
                if (result != null) {
                    val cityPreset = CityPreset(
                        name = result.localityName,
                        country = result.countryName,
                        latitude = result.latitude,
                        longitude = result.longitude,
                        timezone = result.timezoneHours
                    )
                    val method = _uiState.value.selectedCalculationMethod
                    val asrMethod = _uiState.value.asrJuristicMethod
                    val schedule = PrayerTimeCalculator.calculateScheduleForCoordinates(
                        latitude = result.latitude,
                        longitude = result.longitude,
                        timezone = result.timezoneHours,
                        cityName = result.localityName,
                        method = method,
                        asrMethod = asrMethod,
                        isUsingGpsLocation = true
                    )
                    val bearing = QiblaCalculator.calculateQiblaBearing(result.latitude, result.longitude)
                    val qiblaDir = QiblaCalculator.calculateQibla(result.latitude, result.longitude)
                    val heading = _uiState.value.deviceHeading
                    var diff = (bearing - heading) % 360f
                    if (diff > 180f) diff -= 360f
                    if (diff < -180f) diff += 360f

                    _uiState.update {
                        it.copy(
                            selectedCity = cityPreset,
                            prayerSchedule = schedule,
                            qiblaBearing = bearing,
                            qiblaDirection = qiblaDir,
                            angleDifferenceToQibla = diff,
                            isQiblaAligned = abs(diff) <= 4.0f,
                            isUsingDeviceLocation = true,
                            isDetectingLocation = false
                        )
                    }
                    updatePrayerNotificationSchedule(schedule)

                    prayerTimesRepo.calculateAndStorePrayerTimes(
                        latitude = result.latitude,
                        longitude = result.longitude,
                        timezone = result.timezoneHours,
                        cityName = result.localityName,
                        method = method
                    )
                } else {
                    _uiState.update { it.copy(isDetectingLocation = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isDetectingLocation = false) }
            }
        }
    }

    // --- Prayer Notification System Helper & Controls ---
    private fun updatePrayerNotificationSchedule(schedule: PrayerSchedule) {
        val context = getApplication<Application>()
        val settings = PrayerNotificationHelper.getSettings(context)
        if (settings.alertsEnabled) {
            PrayerNotificationHelper.scheduleDailyPrayerAlarms(context, schedule, settings)
        }
        val nextInfo = PrayerNotificationHelper.getNextScheduledAlarmInfo(context, schedule, settings)
        val nextText = if (nextInfo != null) {
            "Next: ${nextInfo.prayerName} at ${nextInfo.formattedTime} (${nextInfo.remainingFormatted})"
        } else if (!settings.alertsEnabled) {
            "Prayer alerts disabled"
        } else {
            "No alerts scheduled"
        }
        val summary = PrayerNotificationSchedulerService.getLatestScheduleSummary(context)
        _uiState.update {
            it.copy(
                prayerAlertsEnabled = settings.alertsEnabled,
                notifyFajr = settings.notifyFajr,
                notifySunrise = settings.notifySunrise,
                notifyDhuhr = settings.notifyDhuhr,
                notifyAsr = settings.notifyAsr,
                notifyMaghrib = settings.notifyMaghrib,
                notifyIsha = settings.notifyIsha,
                notifySunsetFasting = settings.notifySunsetFastingReminder,
                reminderOffsetMinutes = settings.reminderOffsetMinutes,
                alertSoundMode = settings.soundMode,
                nextScheduledAlertText = nextText,
                schedulerServiceStatus = summary
            )
        }
    }

    fun refreshNotificationState() {
        updatePrayerNotificationSchedule(_uiState.value.prayerSchedule)
    }

    fun scheduleLocationBasedPrayerNotifications(context: Context = getApplication()) {
        PrayerNotificationSchedulerService.startScheduling(context, forceGps = true)
        updatePrayerNotificationSchedule(_uiState.value.prayerSchedule)
    }

    fun toggleGlobalPrayerAlerts(enabled: Boolean, context: Context = getApplication()) {
        val current = PrayerNotificationHelper.getSettings(context)
        val updated = current.copy(alertsEnabled = enabled)
        PrayerNotificationHelper.saveSettings(context, updated)
        if (enabled) {
            PrayerNotificationSchedulerService.startScheduling(context)
        } else {
            PrayerNotificationSchedulerService.cancelAll(context)
        }
        updatePrayerNotificationSchedule(_uiState.value.prayerSchedule)
    }

    fun toggleIndividualPrayerAlert(prayerName: String, enabled: Boolean, context: Context = getApplication()) {
        val current = PrayerNotificationHelper.getSettings(context)
        val updated = when (prayerName.lowercase().trim()) {
            "fajr" -> current.copy(notifyFajr = enabled)
            "sunrise" -> current.copy(notifySunrise = enabled)
            "dhuhr" -> current.copy(notifyDhuhr = enabled)
            "asr" -> current.copy(notifyAsr = enabled)
            "maghrib" -> current.copy(notifyMaghrib = enabled)
            "isha" -> current.copy(notifyIsha = enabled)
            else -> current
        }
        PrayerNotificationHelper.saveSettings(context, updated)
        if (updated.alertsEnabled) {
            PrayerNotificationSchedulerService.startScheduling(context)
        }
        updatePrayerNotificationSchedule(_uiState.value.prayerSchedule)
    }

    fun setPrayerReminderOffset(minutes: Int, context: Context = getApplication()) {
        val current = PrayerNotificationHelper.getSettings(context)
        val updated = current.copy(reminderOffsetMinutes = minutes)
        PrayerNotificationHelper.saveSettings(context, updated)
        if (updated.alertsEnabled) {
            PrayerNotificationSchedulerService.startScheduling(context)
        }
        updatePrayerNotificationSchedule(_uiState.value.prayerSchedule)
    }

    fun setPrayerAlertSoundMode(mode: AlertSoundMode, context: Context = getApplication()) {
        val current = PrayerNotificationHelper.getSettings(context)
        val updated = current.copy(soundMode = mode)
        PrayerNotificationHelper.saveSettings(context, updated)
        if (updated.alertsEnabled) {
            PrayerNotificationSchedulerService.startScheduling(context)
        }
        updatePrayerNotificationSchedule(_uiState.value.prayerSchedule)
    }

    fun triggerTestPrayerNotification(context: Context = getApplication(), prayerName: String = "Asr") {
        PrayerNotificationSchedulerService.sendTestNotification(context, prayerName)
    }

    fun toggleSunsetFastingReminder(enabled: Boolean, context: Context = getApplication()) {
        val current = PrayerNotificationHelper.getSettings(context)
        val updated = current.copy(notifySunsetFastingReminder = enabled)
        PrayerNotificationHelper.saveSettings(context, updated)
        if (updated.alertsEnabled) {
            PrayerNotificationSchedulerService.startScheduling(context)
        }
        updatePrayerNotificationSchedule(_uiState.value.prayerSchedule)
    }

    fun triggerTestFastingNotification(context: Context = getApplication()) {
        val sunsetTime = _uiState.value.prayerSchedule.iftar.ifEmpty { _uiState.value.prayerSchedule.maghrib }
        val cityName = _uiState.value.selectedCity.name
        PrayerNotificationHelper.triggerInstantTestFastingNotification(
            context = context,
            cityName = cityName,
            sunsetTime = sunsetTime
        )
    }

    fun updateCoordinates(
        latitude: Double,
        longitude: Double,
        cityName: String = "Custom Location",
        timezone: Double = (java.util.TimeZone.getDefault().rawOffset / 3600000.0)
    ) {
        val customCity = CityPreset(cityName, "Coordinates", latitude, longitude, timezone)
        selectCity(customCity)
    }

    fun togglePrayer(prayerName: String, dateStr: String? = null) {
        viewModelScope.launch {
            val targetDate = dateStr ?: trackerRepo.getTodayDateStr()
            trackerRepo.togglePrayer(prayerName, targetDate)
            triggerHaptic(50)
            com.example.sync.PrayerSyncService.startSync(getApplication())
        }
    }

    fun syncPrayerAttendanceNow() {
        com.example.sync.PrayerSyncService.startSync(getApplication())
    }

    fun toggleHistoricalPrayer(dateStr: String, prayerName: String) {
        togglePrayer(prayerName, dateStr)
    }

    fun toggleFasting(fastingType: String? = null) {
        viewModelScope.launch {
            trackerRepo.toggleFasting(defaultType = fastingType)
            triggerHaptic(60)
        }
    }

    fun toggleFastingForDate(dateStr: String, defaultType: String = "Ramadan") {
        viewModelScope.launch {
            trackerRepo.toggleFasting(dateStr = dateStr, defaultType = defaultType)
            triggerHaptic(50)
        }
    }

    fun setFastingType(fastingType: String) {
        viewModelScope.launch {
            trackerRepo.updateFastingType(trackerRepo.getTodayDateStr(), fastingType)
            triggerHaptic(40)
        }
    }

    fun getPastDaysForFasting(): List<TrackerRepository.DateInfo> {
        return trackerRepo.getPastDays(7)
    }

    fun logPastFast(dateStr: String, fastingType: String, isQadha: Boolean = false, note: String = "") {
        viewModelScope.launch {
            trackerRepo.logFastRecord(
                dateStr = dateStr,
                isFasted = true,
                fastingType = fastingType,
                isQadha = isQadha,
                note = note
            )
            triggerHaptic(50)
        }
    }

    fun deleteFastRecord(dateStr: String) {
        viewModelScope.launch {
            trackerRepo.deleteFastRecord(dateStr)
            triggerHaptic(40)
        }
    }

    fun updateFastNote(dateStr: String, note: String) {
        viewModelScope.launch {
            val records = trackerRepo.getAllFastingRecordsSync()
            val existing = records.find { it.dateStr == dateStr }
            if (existing != null) {
                trackerRepo.logFastRecord(
                    dateStr = dateStr,
                    isFasted = existing.isFasted,
                    fastingType = existing.fastingType,
                    isQadha = existing.isQadha,
                    note = note
                )
                triggerHaptic(30)
            }
        }
    }

    fun seedFastingHistory() {
        viewModelScope.launch {
            trackerRepo.seedHistoricalFastingDataIfEmpty()
            triggerHaptic(30)
        }
    }

    // --- Quran On-Demand Room Caching ---
    fun setQuranSearch(query: String) {
        _uiState.update { it.copy(quranSearchQuery = query) }
    }

    fun openSurah(surah: SurahMeta) {
        _uiState.update {
            it.copy(
                selectedSurah = surah,
                isLoadingAyahs = true
            )
        }
        viewModelScope.launch {
            quranRepo.getAyahsForSurah(surah.number).collect { ayahs ->
                _uiState.update {
                    it.copy(
                        currentSurahAyahs = ayahs,
                        isLoadingAyahs = false
                    )
                }
            }
        }
    }

    fun closeSurah() {
        _uiState.update { it.copy(selectedSurah = null, currentSurahAyahs = emptyList()) }
    }

    fun toggleBookmarkAyah(ayah: AyahEntity) {
        viewModelScope.launch {
            quranRepo.toggleBookmark(ayah.surahNumber, ayah.ayahNumber, ayah.isBookmarked)
            triggerHaptic(40)
        }
    }

    // --- Hadith On-Demand Room Caching ---
    fun selectHadithBook(collection: String, bookNumber: Int) {
        _uiState.update {
            it.copy(
                selectedCollectionId = collection,
                selectedBookNumber = bookNumber,
                isLoadingHadiths = true
            )
        }
        viewModelScope.launch {
            hadithRepo.getHadithsForBook(collection, bookNumber).collect { hadiths ->
                _uiState.update {
                    it.copy(
                        currentHadiths = hadiths,
                        isLoadingHadiths = false
                    )
                }
            }
        }
    }

    fun toggleFavoriteHadith(hadith: HadithEntity) {
        viewModelScope.launch {
            hadithRepo.toggleFavorite(hadith.collection, hadith.hadithNumber, hadith.isFavorite)
            triggerHaptic(35)
        }
    }

    fun toggleHadithStudied(hadith: HadithEntity) {
        viewModelScope.launch {
            hadithRepo.toggleStudied(hadith.collection, hadith.hadithNumber, hadith.isRead)
            if (!hadith.isRead) {
                deenCoinsRepo.rewardHadithChapterCompleted(
                    "${hadith.collection}_${hadith.hadithNumber}",
                    "Hadith #${hadith.hadithNumber} (${hadith.collection})"
                )
            }
            triggerHaptic(40)
        }
    }

    fun toggleHadithMemorized(hadith: HadithEntity) {
        viewModelScope.launch {
            hadithRepo.toggleMemorized(hadith.collection, hadith.hadithNumber, hadith.isMemorized)
            triggerHaptic(50)
        }
    }

    fun setHadithFavoritesFilter(enabled: Boolean) {
        _uiState.update { it.copy(hadithFavoritesOnly = enabled, hadithStudiedOnly = false, hadithMemorizedOnly = false) }
    }

    fun toggleHadithFavoritesOnly() {
        _uiState.update {
            val next = !it.hadithFavoritesOnly
            it.copy(hadithFavoritesOnly = next, hadithStudiedOnly = false, hadithMemorizedOnly = false)
        }
        triggerHaptic(35)
    }

    fun setHadithStudiedFilter(enabled: Boolean) {
        _uiState.update { it.copy(hadithStudiedOnly = enabled, hadithFavoritesOnly = false, hadithMemorizedOnly = false) }
    }

    fun setHadithMemorizedFilter(enabled: Boolean) {
        _uiState.update { it.copy(hadithMemorizedOnly = enabled, hadithFavoritesOnly = false, hadithStudiedOnly = false) }
    }

    fun setHadithSearchQuery(query: String) {
        _uiState.update { it.copy(hadithSearchQuery = query) }
    }

    fun setHadithCategoryFilter(category: String) {
        _uiState.update { it.copy(selectedHadithCategory = category) }
        triggerHaptic(25)
    }

    fun setHadithNarratorFilter(narrator: String) {
        _uiState.update { it.copy(selectedHadithNarrator = narrator) }
        triggerHaptic(25)
    }

    fun setHadithCollectionBrowserFilter(collection: String) {
        _uiState.update { it.copy(hadithBrowserCollection = collection) }
        triggerHaptic(25)
    }

    fun clearHadithFilters() {
        _uiState.update {
            it.copy(
                hadithSearchQuery = "",
                selectedHadithCategory = "All",
                selectedHadithNarrator = "All",
                hadithBrowserCollection = "All",
                hadithFavoritesOnly = false,
                hadithStudiedOnly = false,
                hadithMemorizedOnly = false
            )
        }
        triggerHaptic(30)
    }

    // --- Duas & Learning Actions ---
    fun selectDuaCategory(category: String) {
        val filtered = if (category == "All") {
            DuaSeedData.DUAS
        } else {
            DuaSeedData.DUAS.filter { it.category == category }
        }
        _uiState.update {
            it.copy(
                selectedDuaCategory = category,
                duasList = filtered,
                duaFavoritesOnly = false,
                duaMemorizedOnly = false
            )
        }
    }

    fun toggleFavoriteDua(duaId: String, currentFavorite: Boolean) {
        viewModelScope.launch {
            duaRepo.toggleFavorite(duaId, currentFavorite)
            triggerHaptic(30)
        }
    }

    fun toggleDuaMemorized(duaId: String, currentMemorized: Boolean) {
        viewModelScope.launch {
            duaRepo.toggleMemorized(duaId, currentMemorized)
            triggerHaptic(40)
        }
    }

    fun incrementDuaCount(duaId: String, recommendedCount: Int) {
        viewModelScope.launch {
            val entity = _uiState.value.duaEntities.firstOrNull { it.id == duaId }
            val current = entity?.practiceCount ?: (_uiState.value.duaProgressCounts[duaId] ?: 0)
            val next = current + 1
            duaRepo.incrementPractice(duaId, current)
            _uiState.update { it.copy(duaProgressCounts = it.duaProgressCounts + (duaId to next)) }
            if (next >= recommendedCount) {
                triggerHaptic(90)
            } else {
                triggerHaptic(25)
            }
        }
    }

    fun resetDuaCount(duaId: String) {
        viewModelScope.launch {
            duaRepo.resetPractice(duaId)
            _uiState.update { it.copy(duaProgressCounts = it.duaProgressCounts + (duaId to 0)) }
            triggerHaptic(30)
        }
    }

    fun setDuaFavoritesFilter(enabled: Boolean) {
        _uiState.update { it.copy(duaFavoritesOnly = enabled, duaMemorizedOnly = false) }
    }

    fun setDuaMemorizedFilter(enabled: Boolean) {
        _uiState.update { it.copy(duaMemorizedOnly = enabled, duaFavoritesOnly = false) }
    }

    fun setDuaSearchQuery(query: String) {
        _uiState.update { it.copy(duaSearchQuery = query) }
    }

    fun setDuaViewModeTab(tab: Int) {
        _uiState.update { it.copy(duaViewModeTab = tab) }
    }

    fun selectDuaFolder(folder: String) {
        _uiState.update { it.copy(selectedDuaFolder = folder) }
    }

    fun openDuaOrganizeDialog(dua: DuaEntity?) {
        _uiState.update { it.copy(selectedDuaForOrganizing = dua) }
    }

    fun saveDuaCustomFolderAndNote(duaId: String, folder: String, note: String) {
        viewModelScope.launch {
            duaRepo.updateCustomFolder(duaId, folder.trim())
            duaRepo.updateUserNote(duaId, note.trim())
            _uiState.update { it.copy(selectedDuaForOrganizing = null) }
            triggerHaptic(40)
        }
    }

    fun setHadithDuaSubTab(tab: Int) {
        _uiState.update { it.copy(hadithDuaActiveSubTab = tab) }
    }

    fun openDuaCategory(category: String) {
        selectDuaCategory(category)
        _uiState.update {
            it.copy(
                duaViewModeTab = 1, // All / Filtered Invocations
                hadithDuaActiveSubTab = 1
            )
        }
    }

    fun openDuaBookmarks() {
        _uiState.update {
            it.copy(
                duaViewModeTab = 2, // Bookmarks & Folders
                hadithDuaActiveSubTab = 1,
                currentTab = MainTab.HADITH_DUAS
            )
        }
    }

    fun openDuaLibrary(viewModeTab: Int = 0) {
        _uiState.update {
            it.copy(
                duaViewModeTab = viewModeTab,
                hadithDuaActiveSubTab = 1,
                currentTab = MainTab.HADITH_DUAS
            )
        }
    }

    fun selectTasbihPreset(preset: TasbihPreset) {
        _uiState.update {
            it.copy(
                selectedTasbih = preset,
                tasbihCount = 0,
                tasbihLap = 1
            )
        }
    }

    fun incrementTasbih() {
        val currentCount = _uiState.value.tasbihCount + 1
        val target = _uiState.value.selectedTasbih.targetCount
        val total = _uiState.value.totalTasbihCount + 1

        if (currentCount >= target) {
            // Target achieved chime / vibration
            triggerHaptic(120)
            _uiState.update {
                it.copy(
                    tasbihCount = 0,
                    tasbihLap = it.tasbihLap + 1,
                    totalTasbihCount = total
                )
            }
        } else {
            triggerHaptic(30)
            _uiState.update {
                it.copy(
                    tasbihCount = currentCount,
                    totalTasbihCount = total
                )
            }
        }
    }

    fun resetTasbih() {
        _uiState.update {
            it.copy(
                tasbihCount = 0,
                tasbihLap = 1
            )
        }
        triggerHaptic(50)
    }

    // --- Deen365 AI Spiritual Guidance ---
    fun setAiCategory(category: AiCategory) {
        _uiState.update { it.copy(aiCategory = category) }
        // Auto-fetch reflection for this category
        requestAiGuidance(category, "")
    }

    fun setAiQuery(query: String) {
        _uiState.update { it.copy(aiInputQuery = query) }
    }

    fun requestAiGuidance(category: AiCategory = _uiState.value.aiCategory, customQuery: String = _uiState.value.aiInputQuery) {
        if (!userRepo.isSubscriberOrAdmin()) {
            _uiState.update { it.copy(showSubscriptionRequiredDialog = true, isLoadingAi = false) }
            return
        }
        _uiState.update { it.copy(isLoadingAi = true, completedActionSteps = emptySet()) }
        viewModelScope.launch {
            val result = GeminiSpiritualService.getSpiritualGuidance(category, customQuery)
            _uiState.update {
                it.copy(
                    isLoadingAi = false,
                    currentAiGuidance = result,
                    aiInputQuery = "",
                    aiGuidanceHistory = listOf(result) + it.aiGuidanceHistory.take(15)
                )
            }
        }
    }

    // --- Authentication & User Roles (Firebase) ---
    fun isUserSubscribed(): Boolean = userRepo.isSubscriberOrAdmin()

    fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        userRepo.signInWithEmail(email, password, onSuccess, onError)
    }

    fun registerWithEmail(email: String, password: String, displayName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        userRepo.registerWithEmail(email, password, displayName, onSuccess, onError)
    }

    fun playAudio(id: String, arabicText: String, transliteration: String = "") {
        toggleRecitation(id, arabicText, transliteration)
    }

    fun addCustomVideo(video: com.example.data.local.IslamicVideoEntity) {
        addVideoByAdmin(video)
    }

    fun deleteCustomVideo(videoId: String) {
        deleteVideoByAdmin(videoId)
    }

    fun sendPasswordReset(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        userRepo.sendPasswordReset(email, onSuccess, onError)
    }

    fun signInWithGoogleToken(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        userRepo.signInWithGoogleToken(idToken, onSuccess, onError)
    }

    fun signOut() {
        userRepo.signOut()
    }

    fun upgradeToSubscriber(planName: String) {
        userRepo.upgradeToSubscriber(planName, onSuccess = {}, onError = {})
        _uiState.update { it.copy(showSubscriptionRequiredDialog = false) }
    }

    fun openUserAccountDialog() {
        _uiState.update { it.copy(isUserAccountDialogOpen = true) }
    }

    fun closeUserAccountDialog() {
        _uiState.update { it.copy(isUserAccountDialogOpen = false) }
    }

    fun openAdminDialog() {
        _uiState.update { it.copy(isAdminDialogOpen = true) }
    }

    fun closeAdminDialog() {
        _uiState.update { it.copy(isAdminDialogOpen = false) }
    }

    fun dismissSubscriptionRequiredDialog() {
        _uiState.update { it.copy(showSubscriptionRequiredDialog = false) }
    }

    // --- Qibla & Tasbih Visual Themes ---
    fun setQiblaTheme(theme: QiblaTheme) {
        _uiState.update { it.copy(selectedQiblaTheme = theme) }
        getApplication<Application>().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
            .edit().putString("qibla_theme", theme.id).apply()
        triggerHaptic(25)
    }

    fun setTasbihTheme(theme: TasbihTheme) {
        _uiState.update { it.copy(selectedTasbihTheme = theme) }
        getApplication<Application>().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
            .edit().putString("tasbih_theme", theme.id).apply()
        triggerHaptic(25)
    }

    // --- Admin Module PRO Controls & Gating ---
    fun toggleModuleProStatus(moduleId: String, isPro: Boolean) {
        _uiState.update { state ->
            val updated = if (isPro) state.proModuleIds + moduleId else state.proModuleIds - moduleId
            state.copy(proModuleIds = updated)
        }
        triggerHaptic(30)
    }

    fun showProModuleUnlockPrompt(module: HomeGridModule) {
        _uiState.update { it.copy(proModuleUnlockPrompt = module) }
    }

    fun dismissProModuleUnlockPrompt() {
        _uiState.update { it.copy(proModuleUnlockPrompt = null) }
    }

    fun isModuleUnlocked(module: HomeGridModule): Boolean {
        val isMarkedPro = module.isPro || _uiState.value.proModuleIds.contains(module.id)
        if (!isMarkedPro) return true
        // Admin or active Premium user gets unrestricted access
        return _uiState.value.userProfile?.role == "admin" || _uiState.value.userProfile?.role == "subscriber" || _uiState.value.coinsState.isPremium
    }

    // --- Quran.com Audio Recitation Engine ---
    fun playSurahAudio(surahNumber: Int, startAyah: Int = 1) {
        val surahAyahs = _uiState.value.currentSurahAyahs
        val targetAyah = surahAyahs.find { it.ayahNumber == startAyah } ?: surahAyahs.firstOrNull()
        val ayahNum = targetAyah?.ayahNumber ?: startAyah
        val url = _uiState.value.selectedReciter.getAyahAudioUrl(surahNumber, ayahNum)

        _uiState.update {
            it.copy(
                currentPlayingSurah = surahNumber,
                currentPlayingAyahNumber = ayahNum,
                isQuranAudioPlaying = true
            )
        }

        quranAudioPlayer.playAyahAudio(
            url = url,
            ayahNumber = ayahNum,
            speed = _uiState.value.quranPlaybackSpeed,
            onComplete = {
                // Continuous recitation: proceed to next verse
                val nextAyah = surahAyahs.find { it.ayahNumber == ayahNum + 1 }
                if (nextAyah != null) {
                    playSurahAudio(surahNumber, nextAyah.ayahNumber)
                } else {
                    _uiState.update { it.copy(isQuranAudioPlaying = false, currentPlayingAyahNumber = null) }
                }
            }
        )
    }

    fun playSingleAyahAudio(ayah: AyahEntity) {
        if (_uiState.value.isQuranAudioPlaying && _uiState.value.currentPlayingAyahNumber == ayah.ayahNumber) {
            pauseQuranAudio()
        } else {
            val url = _uiState.value.selectedReciter.getAyahAudioUrl(ayah.surahNumber, ayah.ayahNumber)
            _uiState.update {
                it.copy(
                    currentPlayingSurah = ayah.surahNumber,
                    currentPlayingAyahNumber = ayah.ayahNumber,
                    isQuranAudioPlaying = true
                )
            }
            quranAudioPlayer.playAyahAudio(
                url = url,
                ayahNumber = ayah.ayahNumber,
                speed = _uiState.value.quranPlaybackSpeed,
                onComplete = {
                    _uiState.update { it.copy(isQuranAudioPlaying = false, currentPlayingAyahNumber = null) }
                }
            )
        }
    }

    fun togglePlayPauseQuranAudio() {
        if (_uiState.value.isQuranAudioPlaying) {
            pauseQuranAudio()
        } else {
            val surah = _uiState.value.selectedSurah
            if (surah != null) {
                val currentAyah = _uiState.value.currentPlayingAyahNumber ?: 1
                playSurahAudio(surah.number, currentAyah)
            }
        }
    }

    fun pauseQuranAudio() {
        quranAudioPlayer.pause()
        _uiState.update { it.copy(isQuranAudioPlaying = false) }
    }

    fun resumeQuranAudio() {
        quranAudioPlayer.resume()
        _uiState.update { it.copy(isQuranAudioPlaying = true) }
    }

    fun stopQuranAudio() {
        quranAudioPlayer.stop()
        _uiState.update { it.copy(isQuranAudioPlaying = false, currentPlayingAyahNumber = null) }
    }

    fun nextAyahAudio() {
        val surah = _uiState.value.selectedSurah ?: return
        val currentAyah = _uiState.value.currentPlayingAyahNumber ?: 1
        val next = (currentAyah + 1).coerceAtMost(surah.numberOfAyahs)
        playSurahAudio(surah.number, next)
    }

    fun previousAyahAudio() {
        val surah = _uiState.value.selectedSurah ?: return
        val currentAyah = _uiState.value.currentPlayingAyahNumber ?: 1
        val prev = (currentAyah - 1).coerceAtLeast(1)
        playSurahAudio(surah.number, prev)
    }

    fun setReciter(reciter: QuranAudioReciter) {
        _uiState.update { it.copy(selectedReciter = reciter, isReciterPickerOpen = false) }
        if (_uiState.value.isQuranAudioPlaying) {
            val surah = _uiState.value.selectedSurah
            val ayah = _uiState.value.currentPlayingAyahNumber ?: 1
            if (surah != null) {
                playSurahAudio(surah.number, ayah)
            }
        }
    }

    fun setQuranPlaybackSpeed(speed: Float) {
        _uiState.update { it.copy(quranPlaybackSpeed = speed) }
        quranAudioPlayer.setSpeed(speed)
    }

    fun toggleWordByWord() {
        _uiState.update { it.copy(isWordByWordEnabled = !it.isWordByWordEnabled) }
    }

    fun setArabicFontSize(sp: Int) {
        _uiState.update { it.copy(arabicFontSizeSp = sp.coerceIn(18, 40)) }
    }

    fun setQuranReadingMode(mode: QuranReadingMode) {
        _uiState.update { it.copy(quranReadingMode = mode) }
    }

    fun setTranslationEdition(edition: TranslationEdition) {
        _uiState.update { it.copy(selectedTranslationEdition = edition) }
    }

    fun openTafsir(ayah: AyahEntity) {
        val tafsir = TafsirRepository.getTafsirForAyah(ayah.surahNumber, ayah.ayahNumber)
        _uiState.update { it.copy(activeTafsirAyah = ayah, activeTafsirData = tafsir, isTafsirOpen = true) }
    }

    fun closeTafsir() {
        _uiState.update { it.copy(isTafsirOpen = false, activeTafsirAyah = null, activeTafsirData = null) }
    }

    fun openQuranDisplaySettings() {
        _uiState.update { it.copy(isQuranDisplaySettingsOpen = true) }
    }

    fun closeQuranDisplaySettings() {
        _uiState.update { it.copy(isQuranDisplaySettingsOpen = false) }
    }

    fun openReciterPicker() {
        _uiState.update { it.copy(isReciterPickerOpen = true) }
    }

    fun closeReciterPicker() {
        _uiState.update { it.copy(isReciterPickerOpen = false) }
    }

    fun setTheme(theme: AppThemeType) {
        if (theme.isPremiumOnly && !_uiState.value.coinsState.isPremium) {
            _uiState.update {
                it.copy(
                    isCoinsHubOpen = true,
                    premiumFeaturePromptText = "The '${theme.displayName}' theme is exclusive to Premium users. Unlock Pro with 24 Deen Coins (or complete 5 Namaz today)!"
                )
            }
            return
        }
        _uiState.update { it.copy(selectedThemeType = theme) }
        getApplication<Application>().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
            .edit().putString("app_theme", theme.name).apply()
        triggerHaptic(30)
    }

    fun setDarkModeConfig(config: DarkModeConfig, isSystemInDark: Boolean = false) {
        val prefs = getApplication<Application>().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("dark_mode_config", config.id).apply()

        val isEffectiveDark = when (config) {
            DarkModeConfig.SYSTEM -> isSystemInDark
            DarkModeConfig.LIGHT -> false
            DarkModeConfig.DARK -> true
        }

        _uiState.update { current ->
            // Automatic sync to midnight variants when in dark mode
            val newQibla = if (isEffectiveDark && current.selectedQiblaTheme == QiblaTheme.EMERALD_GOLD) {
                QiblaTheme.CELESTIAL_MIDNIGHT
            } else if (!isEffectiveDark && current.selectedQiblaTheme == QiblaTheme.CELESTIAL_MIDNIGHT) {
                QiblaTheme.EMERALD_GOLD
            } else {
                current.selectedQiblaTheme
            }

            val newTasbih = if (isEffectiveDark && current.selectedTasbihTheme == TasbihTheme.EMERALD_GOLD) {
                TasbihTheme.CELESTIAL_MIDNIGHT
            } else if (!isEffectiveDark && current.selectedTasbihTheme == TasbihTheme.CELESTIAL_MIDNIGHT) {
                TasbihTheme.EMERALD_GOLD
            } else {
                current.selectedTasbihTheme
            }

            current.copy(
                darkModeConfig = config,
                isDarkMode = isEffectiveDark,
                selectedQiblaTheme = newQibla,
                selectedTasbihTheme = newTasbih
            )
        }
        triggerHaptic(30)
    }

    fun onSystemDarkModeChanged(isSystemInDark: Boolean) {
        if (_uiState.value.darkModeConfig == DarkModeConfig.SYSTEM) {
            val isEffectiveDark = isSystemInDark
            _uiState.update { current ->
                val newQibla = if (isEffectiveDark && current.selectedQiblaTheme == QiblaTheme.EMERALD_GOLD) {
                    QiblaTheme.CELESTIAL_MIDNIGHT
                } else if (!isEffectiveDark && current.selectedQiblaTheme == QiblaTheme.CELESTIAL_MIDNIGHT) {
                    QiblaTheme.EMERALD_GOLD
                } else {
                    current.selectedQiblaTheme
                }

                val newTasbih = if (isEffectiveDark && current.selectedTasbihTheme == TasbihTheme.EMERALD_GOLD) {
                    TasbihTheme.CELESTIAL_MIDNIGHT
                } else if (!isEffectiveDark && current.selectedTasbihTheme == TasbihTheme.CELESTIAL_MIDNIGHT) {
                    TasbihTheme.EMERALD_GOLD
                } else {
                    current.selectedTasbihTheme
                }

                current.copy(
                    isDarkMode = isEffectiveDark,
                    selectedQiblaTheme = newQibla,
                    selectedTasbihTheme = newTasbih
                )
            }
        }
    }

    fun toggleDarkMode(isSystemInDark: Boolean = false) {
        val nextConfig = when (_uiState.value.darkModeConfig) {
            DarkModeConfig.SYSTEM -> DarkModeConfig.DARK
            DarkModeConfig.DARK -> DarkModeConfig.LIGHT
            DarkModeConfig.LIGHT -> DarkModeConfig.SYSTEM
        }
        setDarkModeConfig(nextConfig, isSystemInDark)
    }

    fun toggleFastingToday() {
        _uiState.update {
            val newStatus = !it.isFastingToday
            it.copy(
                isFastingToday = newStatus,
                fastingTrackerDaysCount = if (newStatus) (it.fastingTrackerDaysCount + 1).coerceAtMost(it.fastingMonthTotalDays) else (it.fastingTrackerDaysCount - 1).coerceAtLeast(0),
                fastingStreak = if (newStatus) it.fastingStreak + 1 else (it.fastingStreak - 1).coerceAtLeast(0)
            )
        }
    }

    fun setFastingMonthTotalDays(days: Int) {
        val validDays = days.coerceIn(28, 31)
        _uiState.update { 
            it.copy(
                fastingMonthTotalDays = validDays,
                fastingTrackerDaysCount = it.fastingTrackerDaysCount.coerceAtMost(validDays)
            ) 
        }
    }

    fun incrementFastingTrackerDays() {
        _uiState.update {
            val next = (it.fastingTrackerDaysCount + 1).coerceAtMost(it.fastingMonthTotalDays)
            it.copy(
                fastingTrackerDaysCount = next,
                isFastingToday = if (next > it.fastingTrackerDaysCount) true else it.isFastingToday
            )
        }
    }

    fun decrementFastingTrackerDays() {
        _uiState.update {
            val next = (it.fastingTrackerDaysCount - 1).coerceAtLeast(0)
            it.copy(
                fastingTrackerDaysCount = next,
                isFastingToday = if (next == 0) false else it.isFastingToday
            )
        }
    }

    fun setFastingTrackerDays(count: Int) {
        _uiState.update {
            val valid = count.coerceIn(0, it.fastingMonthTotalDays)
            it.copy(fastingTrackerDaysCount = valid)
        }
    }

    fun toggleDuaViewMode() {
        _uiState.update { it.copy(isDuaGridView = !it.isDuaGridView) }
    }

    fun openLearnDua(dua: DuaItem?) {
        _uiState.update { it.copy(selectedLearningDua = dua) }
    }

    fun toggleActionStep(stepIndex: Int) {
        _uiState.update {
            val current = it.completedActionSteps.toMutableSet()
            if (current.contains(stepIndex)) {
                current.remove(stepIndex)
            } else {
                current.add(stepIndex)
                triggerHaptic(40)
            }
            it.copy(completedActionSteps = current)
        }
    }

    fun askAiAboutAyah(ayah: AyahEntity) {
        explainAyahWithAi(ayah)
    }

    fun explainAyahWithAi(ayah: AyahEntity) {
        val surah = _uiState.value.selectedSurah
        val surahName = surah?.englishName ?: "Surah ${ayah.surahNumber}"
        _uiState.update {
            it.copy(
                isAyahExplainerOpen = true,
                activeExplainingAyah = ayah,
                isLoadingAyahExplanation = true,
                ayahExplanationResult = null
            )
        }
        viewModelScope.launch {
            val result = com.example.data.remote.GeminiAyahExplainerService.explainAyah(
                surahNumber = ayah.surahNumber,
                ayahNumber = ayah.ayahNumber,
                surahName = surahName,
                arabicText = ayah.arabicText,
                translation = ayah.translation
            )
            _uiState.update {
                it.copy(
                    ayahExplanationResult = result,
                    isLoadingAyahExplanation = false
                )
            }
        }
    }

    fun closeAyahExplainer() {
        _uiState.update { it.copy(isAyahExplainerOpen = false, activeExplainingAyah = null) }
    }

    fun searchHadithDuaCompanion(themeOrQuery: String) {
        _uiState.update { it.copy(isLoadingHadithDuaAi = true) }
        viewModelScope.launch {
            val res = com.example.data.remote.GeminiHadithDuaCompanionService.findHadithAndDuaForTheme(themeOrQuery)
            _uiState.update {
                it.copy(
                    hadithDuaAiResult = res,
                    isLoadingHadithDuaAi = false
                )
            }
        }
    }

    fun askAiAboutHadith(hadith: HadithEntity) {
        val query = "Reflect on this Hadith from ${hadith.collection} (${hadith.narrator}): '${hadith.translation}'. How can I apply this Prophetic character today?"
        searchHadithDuaCompanion(query)
    }

    fun generatePersonalizedSpiritualGuidance() {
        _uiState.update { it.copy(isLoadingPersonalizedGuidance = true) }
        viewModelScope.launch {
            val userName = _uiState.value.userProfile?.displayName?.ifBlank { "Servant of Allah" } ?: "Servant of Allah"
            val state = _uiState.value
            val summary = "Completed prayers today: ${state.prayerRecord.completedCount}/5. Fajr: ${state.prayerRecord.fajr}, Dhuhr: ${state.prayerRecord.dhuhr}, Asr: ${state.prayerRecord.asr}, Maghrib: ${state.prayerRecord.maghrib}, Isha: ${state.prayerRecord.isha}. Prayer streak: ${state.prayerStats.currentStreakDays} days. Fasting active today: ${state.isFastingToday}. Fasting streak: ${state.fastingStreak} days."
            val res = com.example.data.remote.GeminiPersonalizedGuidanceService.generatePersonalizedGuidance(summary, userName)
            _uiState.update {
                it.copy(
                    personalizedGuidanceResult = res,
                    isLoadingPersonalizedGuidance = false
                )
            }
        }
    }

    fun openMosqueTimingsDialog(isOpen: Boolean) {
        _uiState.update { it.copy(isMosqueTimingsDialogOpen = isOpen) }
    }

    fun setMosqueName(name: String) {
        com.example.notification.MosqueTimingHelper.setMosqueName(getApplication(), name)
        _uiState.update { it.copy(mosqueName = name) }
    }

    fun toggleGlobalMosqueTiming(isActive: Boolean) {
        com.example.notification.MosqueTimingHelper.setGlobalMosqueActive(getApplication(), isActive)
        val refreshed = com.example.notification.MosqueTimingHelper.getMosqueTimings(getApplication(), _uiState.value.prayerSchedule)
        _uiState.update { it.copy(isGlobalMosqueActive = isActive, mosqueTimings = refreshed) }
        PrayerNotificationHelper.scheduleDailyPrayerAlarms(getApplication(), _uiState.value.prayerSchedule)
    }

    fun updateMosqueTiming(
        prayerId: String,
        manualTime: String,
        isActive: Boolean,
        preAlarmMinutes: Int,
        soundMode: AlertSoundMode
    ) {
        com.example.notification.MosqueTimingHelper.savePrayerTiming(
            context = getApplication(),
            prayerId = prayerId,
            manualTime = manualTime,
            isActive = isActive,
            preAlarmMinutes = preAlarmMinutes,
            alarmSoundMode = soundMode
        )
        val refreshed = com.example.notification.MosqueTimingHelper.getMosqueTimings(getApplication(), _uiState.value.prayerSchedule)
        val allActive = refreshed.all { it.isMosqueTimingActive }
        _uiState.update { it.copy(mosqueTimings = refreshed, isGlobalMosqueActive = allActive) }
        PrayerNotificationHelper.scheduleDailyPrayerAlarms(getApplication(), _uiState.value.prayerSchedule)
    }

    fun resetMosqueTimingsToDefault() {
        val prefs = getApplication<Application>().getSharedPreferences("deen365_mosque_timings", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        val refreshed = com.example.notification.MosqueTimingHelper.getMosqueTimings(getApplication(), _uiState.value.prayerSchedule)
        _uiState.update { it.copy(
            isGlobalMosqueActive = false,
            mosqueTimings = refreshed,
            mosqueName = "My Local Masjid"
        ) }
        PrayerNotificationHelper.scheduleDailyPrayerAlarms(getApplication(), _uiState.value.prayerSchedule)
    }

    fun triggerTestMosqueAlarm(prayerId: String = "fajr", context: Context = getApplication()) {
        com.example.notification.MosqueTimingHelper.triggerInstantTestMosqueAlarm(context, prayerId)
        triggerHaptic(50)
    }

    private fun observeDailyReflection() {
        viewModelScope.launch {
            reflectionRepo.getLatestReflection().collect { reflection ->
                _uiState.update { it.copy(dailyQuranicReflection = reflection) }
            }
        }
    }

    fun refreshDailyQuranicReflection() {
        _uiState.update { it.copy(isLoadingReflection = true) }
        viewModelScope.launch {
            reflectionRepo.fetchDailyReflection(forceNew = true)
            _uiState.update { it.copy(isLoadingReflection = false) }
            triggerHaptic(50)
        }
    }

    fun openDailyReflectionDetails(reflection: DailyReflectionEntity) {
        _uiState.update { it.copy(viewingReflectionDetails = reflection) }
    }

    fun closeDailyReflectionDetails() {
        _uiState.update { it.copy(viewingReflectionDetails = null) }
    }

    fun shareDailyReflection(context: Context, reflection: DailyReflectionEntity) {
        DailyReflectionRepository.shareReflection(context, reflection)
    }

    fun askAiAboutDailyReflection(reflection: DailyReflectionEntity) {
        val query = "Reflect on this Daily Quranic Ayah from Surah ${reflection.surahName} (${reflection.surahNumber}:${reflection.ayahNumber}): '${reflection.arabicText}' - ${reflection.translation}. Theme: ${reflection.theme}. What is the spiritual wisdom and practical life lesson?"
        _uiState.update {
            it.copy(
                currentTab = MainTab.AI_GUIDE,
                aiCategory = AiCategory.DAILY_REFLECTION,
                aiInputQuery = query
            )
        }
        requestAiGuidance(AiCategory.DAILY_REFLECTION, query)
    }

    fun askAiAboutDua(dua: DuaItem) {
        val query = "Reflect on this authentic supplication: '${dua.title}' (${dua.category}) - Arabic: '${dua.arabicText}' - Translation: '${dua.translation}'. Reference: ${dua.reference}. What is its spiritual depth, hadith context, and practical action advice?"
        _uiState.update {
            it.copy(
                currentTab = MainTab.AI_GUIDE,
                aiCategory = AiCategory.DUA_EXPLORATION,
                aiInputQuery = query
            )
        }
        requestAiGuidance(AiCategory.DUA_EXPLORATION, query)
    }

    fun askAiAboutNamazStep(step: NamazStep) {
        val query = "Explain the step of Salah '${step.title}' (Posture: ${step.postureName}, Arabic: '${step.arabicText}', Meaning: '${step.translation}'). How does this movement and recitation bring humility (Khushu') and spiritual elevation in Namaz?"
        _uiState.update {
            it.copy(
                currentTab = MainTab.AI_GUIDE,
                aiCategory = AiCategory.NAMAZ_ADVICE,
                aiInputQuery = query
            )
        }
        requestAiGuidance(AiCategory.NAMAZ_ADVICE, query)
    }

    private fun triggerHaptic(durationMs: Long) {
        try {
            val ctx = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun observeVideos() {
        viewModelScope.launch {
            videoRepo.getAllVideos().collect { videos ->
                _uiState.update { current ->
                    val custom = videos.filter { it.isCustomAdded }
                    val filtered = applyVideoFilters(
                        all = videos,
                        filterType = current.videoFilterType,
                        speaker = current.selectedSpeakerFilter,
                        topic = current.selectedTopicFilter,
                        query = current.videoSearchQuery
                    )
                    current.copy(
                        allVideos = videos,
                        customVideos = custom,
                        filteredVideos = filtered
                    )
                }
            }
        }
    }

    private fun observeUserHabits() {
        viewModelScope.launch {
            videoRepo.getUserHabitFlow().collect { habit ->
                if (habit != null) {
                    val savedTabNames = habit.getSelectedBottomTabNames()
                    val parsedTabs = savedTabNames.mapNotNull { name ->
                        try { MainTab.valueOf(name) } catch (_: Exception) { null }
                    }.filter { it != MainTab.AI_GUIDE && it != MainTab.MORE && it != MainTab.SETTINGS }.take(4)

                    val resolvedTabs = if (parsedTabs.size == 4) parsedTabs else listOf(
                        MainTab.DASHBOARD,
                        MainTab.QURAN,
                        MainTab.VIDEOS,
                        MainTab.NAMAZ_FASTING
                    )

                    _uiState.update {
                        it.copy(
                            userHabit = habit,
                            selectedBottomTabs = resolvedTabs
                        )
                    }
                }
            }
        }
    }

    private fun applyVideoFilters(
        all: List<IslamicVideoEntity>,
        filterType: VideoFilterType,
        speaker: String,
        topic: String,
        query: String
    ): List<IslamicVideoEntity> {
        var result = all
        if (filterType == VideoFilterType.BY_SPEAKER) {
            if (speaker != "All Speakers" && speaker.isNotBlank()) {
                result = result.filter { it.speakerName.equals(speaker, ignoreCase = true) }
            }
        } else {
            if (topic != "All Topics" && topic.isNotBlank()) {
                result = result.filter { it.topicName.equals(topic, ignoreCase = true) }
            }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter {
                it.title.lowercase().contains(q) ||
                it.speakerName.lowercase().contains(q) ||
                it.topicName.lowercase().contains(q) ||
                it.description.lowercase().contains(q)
            }
        }
        return result
    }

    fun setVideoFilterType(type: VideoFilterType) {
        _uiState.update { current ->
            val updated = current.copy(videoFilterType = type)
            updated.copy(
                filteredVideos = applyVideoFilters(
                    all = updated.allVideos,
                    filterType = type,
                    speaker = updated.selectedSpeakerFilter,
                    topic = updated.selectedTopicFilter,
                    query = updated.videoSearchQuery
                )
            )
        }
    }

    fun selectSpeakerFilter(speaker: String) {
        _uiState.update { current ->
            val updated = current.copy(selectedSpeakerFilter = speaker)
            updated.copy(
                filteredVideos = applyVideoFilters(
                    all = updated.allVideos,
                    filterType = updated.videoFilterType,
                    speaker = speaker,
                    topic = updated.selectedTopicFilter,
                    query = updated.videoSearchQuery
                )
            )
        }
    }

    fun selectTopicFilter(topic: String) {
        _uiState.update { current ->
            val updated = current.copy(selectedTopicFilter = topic)
            updated.copy(
                filteredVideos = applyVideoFilters(
                    all = updated.allVideos,
                    filterType = updated.videoFilterType,
                    speaker = updated.selectedSpeakerFilter,
                    topic = topic,
                    query = updated.videoSearchQuery
                )
            )
        }
    }

    fun setVideoSearchQuery(query: String) {
        _uiState.update { current ->
            val updated = current.copy(videoSearchQuery = query)
            updated.copy(
                filteredVideos = applyVideoFilters(
                    all = updated.allVideos,
                    filterType = updated.videoFilterType,
                    speaker = updated.selectedSpeakerFilter,
                    topic = updated.selectedTopicFilter,
                    query = query
                )
            )
        }
    }

    fun loginAdmin(passcode: String): Boolean {
        val isValid = passcode.trim() == "deen365admin" || passcode.trim() == "admin123"
        if (isValid) {
            _uiState.update { it.copy(isAdminLoggedIn = true) }
        }
        return isValid
    }

    fun logoutAdmin() {
        _uiState.update { it.copy(isAdminLoggedIn = false) }
    }

    fun addVideoByAdmin(video: IslamicVideoEntity) {
        viewModelScope.launch {
            videoRepo.addVideo(video)
        }
    }

    fun deleteVideoByAdmin(videoId: String) {
        viewModelScope.launch {
            videoRepo.deleteVideo(videoId)
        }
    }

    fun recordVideoWatch(videoId: String, minutes: Int = 15) {
        viewModelScope.launch {
            videoRepo.recordVideoWatched(videoId, minutes)
        }
    }

    fun saveUserHabit(habit: UserHabitEntity) {
        viewModelScope.launch {
            videoRepo.saveUserHabit(habit)
        }
    }

    fun generateHabitInsightsWithAi() {
        if (!userRepo.isSubscriberOrAdmin()) {
            _uiState.update { it.copy(showSubscriptionRequiredDialog = true, isGeneratingAiHabit = false) }
            return
        }
        val currentState = _uiState.value
        val habit = currentState.userHabit
        val prayerRecord = currentState.prayerRecord
        val completedPrayers = listOf(
            prayerRecord.fajr,
            prayerRecord.dhuhr,
            prayerRecord.asr,
            prayerRecord.maghrib,
            prayerRecord.isha
        ).count { it }
        val watchedCount = habit.getWatchedIdsSet().size

        _uiState.update { it.copy(isGeneratingAiHabit = true) }

        viewModelScope.launch {
            val insight = GeminiSpiritualService.generateHabitAnalysis(
                userName = habit.userName,
                completedPrayers = completedPrayers,
                watchedVideosCount = watchedCount,
                totalStudyMinutes = habit.totalWatchMinutes,
                spiritualGoal = habit.primarySpiritualGoal
            )

            val updatedHabit = habit.copy(
                lastAiHabitInsight = insight,
                lastAnalyzedDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            )
            videoRepo.saveUserHabit(updatedHabit)

            _uiState.update {
                it.copy(
                    isGeneratingAiHabit = false,
                    aiHabitInsight = insight,
                    userHabit = updatedHabit
                )
            }
        }
    }

    // --- Dhikr & Tasbih Daily Tracking Module ---
    private fun getTodayDateStr(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    }

    private fun observeDhikrTracker() {
        val todayStr = getTodayDateStr()
        viewModelScope.launch {
            dhikrRepo.getAllDhikrItems().collect { items ->
                _uiState.update { current ->
                    val defaultSelected = if (items.any { it.id == current.selectedDhikrId }) {
                        current.selectedDhikrId
                    } else {
                        items.firstOrNull()?.id ?: "subhanallah"
                    }
                    current.copy(
                        dhikrItems = items,
                        selectedDhikrId = defaultSelected
                    )
                }
            }
        }
        viewModelScope.launch {
            dhikrRepo.getTodayLogs(todayStr).collect { logs ->
                _uiState.update { it.copy(todayDhikrLogs = logs) }
            }
        }
        viewModelScope.launch {
            dhikrRepo.getTotalCountForDate(todayStr).collect { total ->
                _uiState.update { it.copy(todayTotalDhikrCount = total) }
            }
        }
    }

    fun selectDhikr(dhikrId: String) {
        _uiState.update { it.copy(selectedDhikrId = dhikrId) }
        triggerDhikrHaptic(25)
    }

    fun incrementDhikr(dhikrId: String, amount: Int = 1) {
        val todayStr = getTodayDateStr()
        val item = _uiState.value.dhikrItems.find { it.id == dhikrId }
        val goal = item?.dailyGoal ?: 100
        val existingLog = _uiState.value.todayDhikrLogs[dhikrId]
        val previousCount = existingLog?.count ?: 0

        viewModelScope.launch {
            val newCount = dhikrRepo.incrementDhikr(dhikrId, todayStr, amount, goal)
            if (previousCount < goal && newCount >= goal) {
                // Reached daily goal milestone!
                triggerCelebrationHaptic()
            } else {
                triggerDhikrHaptic(30)
            }
            playBeadSound()
        }
    }

    fun decrementDhikr(dhikrId: String) {
        val todayStr = getTodayDateStr()
        viewModelScope.launch {
            dhikrRepo.decrementDhikr(dhikrId, todayStr)
            triggerDhikrHaptic(20)
        }
    }

    fun resetDhikrToday(dhikrId: String) {
        val todayStr = getTodayDateStr()
        viewModelScope.launch {
            dhikrRepo.resetDhikrToday(dhikrId, todayStr)
            triggerDhikrHaptic(50)
        }
    }

    fun updateDhikrDailyGoal(dhikrId: String, newGoal: Int) {
        val todayStr = getTodayDateStr()
        viewModelScope.launch {
            dhikrRepo.updateDailyGoal(dhikrId, newGoal, todayStr)
            triggerDhikrHaptic(40)
        }
    }

    fun addCustomDhikr(arabic: String, transliteration: String, meaning: String, dailyGoal: Int) {
        viewModelScope.launch {
            val created = dhikrRepo.addCustomDhikr(arabic, transliteration, meaning, dailyGoal)
            _uiState.update { it.copy(selectedDhikrId = created.id) }
            triggerCelebrationHaptic()
        }
    }

    fun deleteCustomDhikr(dhikrId: String) {
        viewModelScope.launch {
            dhikrRepo.deleteCustomDhikr(dhikrId)
            _uiState.update { current ->
                val fallback = current.dhikrItems.firstOrNull { it.id != dhikrId }?.id ?: "subhanallah"
                current.copy(selectedDhikrId = fallback)
            }
        }
    }

    fun toggleDhikrVibration() {
        _uiState.update { it.copy(dhikrVibrationEnabled = !it.dhikrVibrationEnabled) }
        triggerHaptic(30)
    }

    fun toggleDhikrSound() {
        _uiState.update { it.copy(dhikrSoundEnabled = !it.dhikrSoundEnabled) }
        playBeadSound()
    }

    private fun triggerDhikrHaptic(durationMs: Long) {
        if (!_uiState.value.dhikrVibrationEnabled) return
        triggerHaptic(durationMs)
    }

    private fun triggerCelebrationHaptic() {
        if (!_uiState.value.dhikrVibrationEnabled) return
        try {
            val ctx = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    (ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 80, 80, 140), -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(200)
            }
        } catch (_: Exception) {}
    }

    private fun playBeadSound() {
        if (!_uiState.value.dhikrSoundEnabled) return
        try {
            val toneGen = android.media.ToneGenerator(android.media.AudioManager.STREAM_NOTIFICATION, 50)
            toneGen.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 30)
        } catch (_: Exception) {}
    }

    // ==========================================
    // Islamic Heritage (Alim.org) Operations
    // ==========================================

    fun openProphetStory(prophet: com.example.data.seed.ProphetStory) {
        _uiState.update { it.copy(selectedProphetStory = prophet) }
    }

    fun closeProphetStory() {
        _uiState.update { it.copy(selectedProphetStory = null) }
    }

    fun openCaliphProfile(caliph: com.example.data.seed.CaliphProfile) {
        _uiState.update { it.copy(selectedCaliphProfile = caliph) }
    }

    fun closeCaliphProfile() {
        _uiState.update { it.copy(selectedCaliphProfile = null) }
    }

    fun openSahabiProfile(sahabi: com.example.data.seed.SahabiProfile) {
        _uiState.update { it.copy(selectedSahabiProfile = sahabi) }
    }

    fun closeSahabiProfile() {
        _uiState.update { it.copy(selectedSahabiProfile = null) }
    }

    fun openUnifiedSearch(initialQuery: String = "") {
        val results = if (initialQuery.isNotBlank()) {
            com.example.domain.UnifiedCrossSearchEngine.search(initialQuery, _uiState.value.unifiedSearchFilter)
        } else emptyList()
        _uiState.update {
            it.copy(
                isUnifiedSearchOpen = true,
                unifiedSearchQuery = initialQuery,
                unifiedSearchResults = results
            )
        }
    }

    fun closeUnifiedSearch() {
        _uiState.update { it.copy(isUnifiedSearchOpen = false) }
    }

    fun updateUnifiedSearchQuery(query: String) {
        val results = com.example.domain.UnifiedCrossSearchEngine.search(query, _uiState.value.unifiedSearchFilter)
        _uiState.update {
            it.copy(
                unifiedSearchQuery = query,
                unifiedSearchResults = results
            )
        }
    }

    fun setUnifiedSearchFilter(filter: com.example.domain.SearchCorpusType) {
        val results = com.example.domain.UnifiedCrossSearchEngine.search(_uiState.value.unifiedSearchQuery, filter)
        _uiState.update {
            it.copy(
                unifiedSearchFilter = filter,
                unifiedSearchResults = results
            )
        }
    }

    // ==========================================
    // Islamic Books Library & Reader Operations
    // ==========================================

    fun openBook(book: com.example.data.model.IslamicBook) {
        _uiState.update {
            it.copy(
                selectedBook = book,
                currentReadingChapterIndex = book.currentChapterIndex
            )
        }
    }

    fun closeBook() {
        _uiState.update { it.copy(selectedBook = null) }
    }

    fun selectBookChapter(chapterIndex: Int) {
        _uiState.update { current ->
            val updated = current.selectedBook?.copy(currentChapterIndex = chapterIndex)
            current.copy(
                selectedBook = updated,
                currentReadingChapterIndex = chapterIndex,
                isBookDrawerOpen = false
            )
        }
    }

    fun setReaderFontSize(sizeSp: Int) {
        val clamped = sizeSp.coerceIn(14, 34)
        _uiState.update { it.copy(readerFontSizeSp = clamped) }
    }

    fun setReaderTheme(theme: String) {
        _uiState.update { it.copy(readerTheme = theme) }
    }

    fun toggleBookDrawer() {
        _uiState.update { it.copy(isBookDrawerOpen = !it.isBookDrawerOpen) }
    }

    fun toggleBookFavorite(bookId: String) {
        _uiState.update { current ->
            val updatedList = current.islamicBooksList.map {
                if (it.id == bookId) it.copy(isFavorite = !it.isFavorite) else it
            }
            val currentSel = if (current.selectedBook?.id == bookId) {
                current.selectedBook.copy(isFavorite = !current.selectedBook.isFavorite)
            } else current.selectedBook
            current.copy(islamicBooksList = updatedList, selectedBook = currentSel)
        }
    }

    fun crawlNewBooks(query: String = "") {
        viewModelScope.launch {
            _uiState.update { it.copy(isCrawlingBooks = true) }
            val result = bookCrawlerService.crawlBooks(query)
            result.onSuccess { newBooks ->
                _uiState.update { current ->
                    val combined = (current.islamicBooksList + newBooks).distinctBy { it.id }
                    current.copy(
                        islamicBooksList = combined,
                        isCrawlingBooks = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isCrawlingBooks = false) }
            }
        }
    }

    // ==========================================
    // Quran.com Study Suite Operations
    // ==========================================

    fun openRabbanaDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isRabbanaDialogOpen = true)) }
    }

    fun closeRabbanaDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isRabbanaDialogOpen = false)) }
    }

    fun openSajdahVersesDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isSajdahVersesDialogOpen = true)) }
    }

    fun closeSajdahVersesDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isSajdahVersesDialogOpen = false)) }
    }

    fun openSurahInfoDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isSurahInfoDialogOpen = true)) }
    }

    fun closeSurahInfoDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isSurahInfoDialogOpen = false)) }
    }

    fun toggleTajweedLegend() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isTajweedLegendOpen = !it.isTajweedLegendOpen)) }
    }

    fun toggleTajweedMode() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isTajweedModeEnabled = !it.isTajweedModeEnabled)) }
    }

    fun toggleDualTranslation() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isDualTranslationEnabled = !it.isDualTranslationEnabled)) }
    }

    fun openGoToVerseDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isGoToVerseDialogOpen = true)) }
    }

    fun closeGoToVerseDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isGoToVerseDialogOpen = false)) }
    }

    fun jumpToVerseCoordinate(coordinate: String) {
        val parts = coordinate.trim().split(":", ",")
        if (parts.size >= 2) {
            val surahNum = parts[0].trim().toIntOrNull()
            val ayahNum = parts[1].trim().toIntOrNull()
            if (surahNum != null && surahNum in 1..114) {
                val targetSurah = _uiState.value.surahList.firstOrNull { it.number == surahNum }
                if (targetSurah != null) {
                    openSurah(targetSurah)
                    closeGoToVerseDialog()
                }
            }
        }
    }

    fun openAyahImageCardDialog(ayah: AyahEntity) {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isAyahImageCardDialogOpen = true, selectedAyahForCard = ayah)) }
    }

    fun closeAyahImageCardDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isAyahImageCardDialogOpen = false, selectedAyahForCard = null)) }
    }

    fun openTajweedRulePopup(rule: com.example.data.util.TajweedRuleType?, ayah: AyahEntity) {
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    isTajweedRulePopupOpen = true,
                    selectedTajweedRule = rule,
                    selectedTajweedAyah = ayah
                )
            )
        }
    }

    fun closeTajweedRulePopup() {
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    isTajweedRulePopupOpen = false,
                    selectedTajweedRule = null,
                    selectedTajweedAyah = null
                )
            )
        }
    }

    fun playWordAudio(wordArabic: String, wordTransliteration: String) {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(activeWordAudioText = wordArabic)) }
        triggerHaptic(20)
        // Also play standard audio TTS recitation
        viewModelScope.launch {
            try {
                playAudio("word_${wordArabic.hashCode()}", wordArabic, wordTransliteration)
            } catch (e: Exception) {
                // Ignore audio play errors
            }
        }
    }

    fun openWordGrammar(wordArabic: String, wordTransliteration: String = "") {
        val detail = com.example.data.seed.QuranWordGrammarSeed.getGrammarDetail(wordArabic)
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    selectedWordGrammarDetail = detail,
                    isWordGrammarDialogOpen = true
                )
            )
        }
        playWordAudio(wordArabic, wordTransliteration)
    }

    fun closeWordGrammar() {
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    selectedWordGrammarDetail = null,
                    isWordGrammarDialogOpen = false
                )
            )
        }
    }

    fun openAsbabAlNuzul(surahNumber: Int) {
        val item = com.example.data.seed.AsbabAlNuzulSeedData.getForSurah(surahNumber)
            ?: com.example.data.seed.AsbabAlNuzulSeedData.REVELATION_CONTEXTS.first()
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    selectedAsbabAlNuzul = item,
                    isAsbabAlNuzulDialogOpen = true
                )
            )
        }
        triggerHaptic(20)
    }

    fun closeAsbabAlNuzul() {
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    selectedAsbabAlNuzul = null,
                    isAsbabAlNuzulDialogOpen = false
                )
            )
        }
    }

    fun openQuranExplorer(verseKey: String = "1:1") {
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    isQuranExplorerOpen = true,
                    quranExplorerVerseKey = verseKey
                )
            )
        }
        triggerHaptic(30)
    }

    fun closeQuranExplorer() {
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    isQuranExplorerOpen = false
                )
            )
        }
    }

    fun incrementReadingTime(seconds: Int = 1) {
        val nextSec = _uiState.value.todayReadingSeconds + seconds
        val goalSec = _uiState.value.dailyQuranGoalMinutes * 60
        val completed = nextSec >= goalSec
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    todayReadingSeconds = nextSec,
                    isGoalCompletedToday = completed
                )
            )
        }
    }

    fun setDailyQuranGoal(minutes: Int) {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(dailyQuranGoalMinutes = minutes.coerceIn(5, 120))) }
        triggerHaptic(30)
    }

    fun setHifzRepeatSettings(repeatCount: Int, pauseSeconds: Int) {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(hifzRepeatCount = repeatCount, hifzPauseDelaySeconds = pauseSeconds)) }
    }

    // --- Multi-Translation Comparison ---
    fun openMultiTranslationDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isMultiTranslationDialogOpen = true)) }
    }

    fun closeMultiTranslationDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isMultiTranslationDialogOpen = false)) }
    }

    fun toggleComparisonTranslation(editionId: String) {
        val current = _uiState.value.comparisonTranslationIds.toMutableSet()
        if (current.contains(editionId)) {
            if (current.size > 1) { // keep at least one
                current.remove(editionId)
            }
        } else {
            current.add(editionId)
        }
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    comparisonTranslationIds = current,
                    isDualTranslationEnabled = current.size > 1
                )
            )
        }
        triggerHaptic(25)
    }

    // --- Highlighting System ---
    fun openHighlightPalette(ayah: AyahEntity) {
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    isHighlightPaletteOpen = true,
                    selectedAyahForHighlight = ayah
                )
            )
        }
    }

    fun closeHighlightPalette() {
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    isHighlightPaletteOpen = false,
                    selectedAyahForHighlight = null
                )
            )
        }
    }

    fun saveHighlight(
        surahNumber: Int,
        ayahNumber: Int,
        surahName: String,
        colorHex: String,
        colorName: String,
        categoryLabel: String,
        note: String
    ) {
        viewModelScope.launch {
            val entity = com.example.data.local.QuranHighlightEntity(
                id = "${surahNumber}_${ayahNumber}",
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                surahName = surahName,
                colorHex = colorHex,
                colorName = colorName,
                categoryLabel = categoryLabel,
                note = note.trim(),
                timestamp = System.currentTimeMillis()
            )
            quranRepo.saveHighlight(entity)
            closeHighlightPalette()
            triggerHaptic(40)
        }
    }

    fun removeHighlight(surahNumber: Int, ayahNumber: Int) {
        viewModelScope.launch {
            quranRepo.deleteHighlight(surahNumber, ayahNumber)
            closeHighlightPalette()
            triggerHaptic(30)
        }
    }

    // --- Daily Goal Bookmarking System ---
    fun openGoalBookmarksDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isGoalBookmarksDialogOpen = true)) }
    }

    fun closeGoalBookmarksDialog() {
        _uiState.update { it.copy(quranSuiteState = it.quranSuiteState.copy(isGoalBookmarksDialogOpen = false)) }
    }

    fun openSetGoalBookmarkDialog(ayah: AyahEntity) {
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    isSetGoalBookmarkDialogOpen = true,
                    selectedAyahForGoalBookmark = ayah
                )
            )
        }
    }

    fun closeSetGoalBookmarkDialog() {
        _uiState.update {
            it.copy(
                quranSuiteState = it.quranSuiteState.copy(
                    isSetGoalBookmarkDialogOpen = false,
                    selectedAyahForGoalBookmark = null
                )
            )
        }
    }

    fun saveGoalBookmark(
        surahNumber: Int,
        ayahNumber: Int,
        surahName: String,
        goalLabel: String,
        targetMinutes: Int,
        note: String
    ) {
        viewModelScope.launch {
            val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val entity = com.example.data.local.QuranGoalBookmarkEntity(
                id = java.util.UUID.randomUUID().toString(),
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                surahName = surahName,
                goalLabel = goalLabel.ifBlank { "Daily Reading Milestone" },
                targetMinutes = targetMinutes,
                note = note.trim(),
                isCompleted = false,
                dateString = dateStr,
                timestamp = System.currentTimeMillis()
            )
            quranRepo.saveGoalBookmark(entity)
            closeSetGoalBookmarkDialog()
            triggerHaptic(45)
        }
    }

    fun toggleGoalBookmarkCompletion(id: String, currentCompleted: Boolean) {
        viewModelScope.launch {
            quranRepo.updateGoalBookmarkCompletion(id, !currentCompleted)
            triggerHaptic(35)
        }
    }

    fun deleteGoalBookmark(id: String) {
        viewModelScope.launch {
            quranRepo.deleteGoalBookmark(id)
            triggerHaptic(30)
        }
    }

    fun jumpToGoalBookmark(bookmark: com.example.data.local.QuranGoalBookmarkEntity) {
        closeGoalBookmarksDialog()
        jumpToVerseCoordinate("${bookmark.surahNumber}:${bookmark.ayahNumber}")
    }

    fun jumpToHighlight(highlight: com.example.data.local.QuranHighlightEntity) {
        closeGoalBookmarksDialog()
        jumpToVerseCoordinate("${highlight.surahNumber}:${highlight.ayahNumber}")
    }

    // ==========================================
    // Sunnah.com Study Suite Operations
    // ==========================================

    fun openGharibGlossary() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isGharibGlossaryOpen = true)) }
    }

    fun closeGharibGlossary() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isGharibGlossaryOpen = false, selectedGharibEntry = null)) }
    }

    fun setGharibSearchQuery(query: String) {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(gharibSearchQuery = query)) }
    }

    fun setGharibCategory(category: String) {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(selectedGharibCategory = category)) }
    }

    fun selectGharibEntry(entry: com.example.data.seed.GharibGlossaryEntry?) {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(selectedGharibEntry = entry)) }
        if (entry != null) triggerHaptic(25)
    }

    fun jumpFromGharibToHadith(collectionId: String, hadithNumber: Int) {
        _uiState.update {
            it.copy(
                currentTab = MainTab.HADITH_DUAS,
                sunnahAlimState = it.sunnahAlimState.copy(isGharibGlossaryOpen = false, selectedGharibEntry = null)
            )
        }
        jumpToHadithCoordinate("$collectionId:$hadithNumber")
        triggerHaptic(40)
    }

    fun openImamBiographies() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isImamBiographiesOpen = true)) }
    }

    fun closeImamBiographies() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isImamBiographiesOpen = false)) }
    }

    fun openNarratorDetail(narrator: com.example.data.seed.NarratorDetail) {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(selectedNarratorDetail = narrator)) }
    }

    fun closeNarratorDetail() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(selectedNarratorDetail = null)) }
    }

    fun openKitabBabBrowser() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isKitabBabBrowserOpen = true)) }
    }

    fun closeKitabBabBrowser() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isKitabBabBrowserOpen = false)) }
    }

    fun selectHadithCollectionDetail(collectionId: String?) {
        _uiState.update {
            it.copy(
                hadithBrowserCollection = collectionId ?: "All",
                sunnahAlimState = it.sunnahAlimState.copy(
                    selectedHadithCollectionDetail = collectionId,
                    selectedHadithChapterNumber = null
                )
            )
        }
        triggerHaptic(30)
    }

    fun selectHadithChapterNumber(chapterNumber: Int?) {
        _uiState.update {
            it.copy(
                sunnahAlimState = it.sunnahAlimState.copy(
                    selectedHadithChapterNumber = chapterNumber
                )
            )
        }
        triggerHaptic(30)
    }

    fun openMarkReadTutorial() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isMarkReadTutorialOpen = true)) }
    }

    fun closeMarkReadTutorial() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isMarkReadTutorialOpen = false)) }
    }

    fun toggleHadithReadFromCard(hadithId: Int) {
        viewModelScope.launch {
            val hadith = _uiState.value.allHadiths.find { it.hadithNumber == hadithId }
                ?: com.example.data.seed.HadithSeedData.ALL_HADITHS.find { it.hadithNumber == hadithId }
            if (hadith != null) {
                hadithRepo.toggleStudied(hadith.collection, hadith.hadithNumber, hadith.isRead)
            }
            _uiState.update {
                it.copy(
                    sunnahAlimState = it.sunnahAlimState.copy(
                        hadithReadingStreak = maxOf(it.hadithReadingStreak, 1)
                    )
                )
            }
        }
        triggerHaptic(40)
    }

    fun toggleHadithReadFromCard(collection: String, hadithNumber: Int) {
        viewModelScope.launch {
            val hadith = _uiState.value.allHadiths.find {
                it.collection.equals(collection, ignoreCase = true) && it.hadithNumber == hadithNumber
            }
            val currentRead = hadith?.isRead ?: false
            hadithRepo.toggleStudied(collection, hadithNumber, currentRead)
            _uiState.update {
                it.copy(
                    sunnahAlimState = it.sunnahAlimState.copy(
                        hadithReadingStreak = maxOf(it.hadithReadingStreak, 1)
                    )
                )
            }
        }
        triggerHaptic(40)
    }

    fun openGoToHadithDialog() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isGoToHadithDialogOpen = true)) }
    }

    fun closeGoToHadithDialog() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isGoToHadithDialogOpen = false)) }
    }

    fun jumpToHadithCoordinate(coordinate: String) {
        // e.g., "bukhari:1" or "muslim:45"
        val parts = coordinate.trim().split(":", "-")
        if (parts.size >= 2) {
            val coll = parts[0].trim().lowercase()
            val hadithNum = parts[1].trim().toIntOrNull()
            if (hadithNum != null) {
                val match = _uiState.value.allHadiths.firstOrNull {
                    it.collection.equals(coll, ignoreCase = true) && it.hadithNumber == hadithNum
                } ?: HadithSeedData.ALL_HADITHS.firstOrNull {
                    it.collection.equals(coll, ignoreCase = true) && it.hadithNumber == hadithNum
                }
                if (match != null) {
                    _uiState.update {
                        it.copy(
                            hadithSearchQuery = "#$hadithNum",
                            currentHadiths = listOf(match),
                            sunnahAlimState = it.sunnahAlimState.copy(isGoToHadithDialogOpen = false)
                        )
                    }
                } else {
                    _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isGoToHadithDialogOpen = false)) }
                }
            }
        }
    }

    fun toggleSanadChain() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isSanadChainVisible = !it.isSanadChainVisible)) }
    }

    fun toggleHadithBookCompleted(bookId: String) {
        val book = com.example.data.seed.HadithKitabProgressData.PROMINENT_BOOKS.firstOrNull { it.id == bookId }
            ?: com.example.data.seed.HadithKitabItem(
                id = bookId,
                collectionId = bookId.substringBefore("_"),
                bookNumber = 1,
                titleEnglish = "Kitab",
                titleArabic = "كتاب",
                hadithCount = 10,
                overview = ""
            )
        viewModelScope.launch {
            kitabProgressRepo.toggleBookCompletion(book)
        }
        triggerHaptic(40)
    }

    fun setCollectionCompleted(collectionId: String, completed: Boolean) {
        viewModelScope.launch {
            kitabProgressRepo.setCollectionCompleted(collectionId, completed)
        }
        triggerHaptic(40)
    }

    fun clearAllKitabProgress() {
        viewModelScope.launch {
            kitabProgressRepo.clearAllProgress()
        }
        triggerHaptic(40)
    }

    fun openAlimThematicIndex() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isAlimThematicIndexOpen = true)) }
    }

    fun closeAlimThematicIndex() {
        _uiState.update { it.copy(sunnahAlimState = it.sunnahAlimState.copy(isAlimThematicIndexOpen = false)) }
    }

    fun openComparativeFiqh(topicId: String? = null) {
        _uiState.update {
            it.copy(
                sunnahAlimState = it.sunnahAlimState.copy(
                    isComparativeFiqhOpen = true,
                    selectedComparativeFiqhTopicId = topicId
                )
            )
        }
        triggerHaptic(30)
    }

    fun closeComparativeFiqh() {
        _uiState.update {
            it.copy(
                sunnahAlimState = it.sunnahAlimState.copy(
                    isComparativeFiqhOpen = false,
                    selectedComparativeFiqhTopicId = null
                )
            )
        }
    }

    fun openVisualSanad(collectionId: String, hadithNumber: Int) {
        val chain = com.example.data.seed.SanadChainSeedData.getSanadForHadith(collectionId, hadithNumber)
        _uiState.update {
            it.copy(
                sunnahAlimState = it.sunnahAlimState.copy(
                    selectedSanadChain = chain,
                    isVisualSanadDialogOpen = true
                )
            )
        }
        triggerHaptic(30)
    }

    fun closeVisualSanad() {
        _uiState.update {
            it.copy(
                sunnahAlimState = it.sunnahAlimState.copy(
                    selectedSanadChain = null,
                    isVisualSanadDialogOpen = false
                )
            )
        }
    }

    // ==========================================
    // Fasting Assistant Operations
    // ==========================================

    fun toggleSunnahReminder() {
        _uiState.update { it.copy(isSunnahReminderEnabled = !it.isSunnahReminderEnabled) }
        triggerHaptic(30)
    }

    fun setFastingAssistantTab(active: Boolean) {
        _uiState.update { it.copy(isFastingAssistantTabActive = active) }
    }

    // ==========================================
    // Community Operations
    // ==========================================

    fun filterCommunityCategory(category: CommunityTopicCategory?) {
        _uiState.update { it.copy(selectedCommunityCategory = category) }
    }

    fun openCreatePostDialog() {
        _uiState.update { it.copy(isCreatePostDialogOpen = true) }
    }

    fun closeCreatePostDialog() {
        _uiState.update { it.copy(isCreatePostDialogOpen = false) }
    }

    fun submitCommunityPost(
        title: String,
        content: String,
        category: CommunityTopicCategory,
        ayahRef: String?,
        hadithRef: String?
    ) {
        val author = _uiState.value.userProfile?.displayName ?: "Fellow Believer"
        val role = _uiState.value.userProfile?.role ?: "member"
        viewModelScope.launch {
            communityRepo.createPost(
                title = title,
                content = content,
                category = category,
                authorName = author,
                authorRole = role,
                ayahRef = ayahRef,
                hadithRef = hadithRef
            )
            _uiState.update { it.copy(isCreatePostDialogOpen = false) }
            triggerHaptic(40)
        }
    }

    fun toggleLikeCommunityPost(postId: String) {
        communityRepo.toggleLikePost(postId)
        triggerHaptic(25)
    }

    fun openPostComments(postId: String) {
        _uiState.update { it.copy(activeCommentsPostId = postId) }
    }

    fun closePostComments() {
        _uiState.update { it.copy(activeCommentsPostId = null) }
    }

    fun submitComment(postId: String, text: String) {
        val author = _uiState.value.userProfile?.displayName ?: "Fellow Believer"
        viewModelScope.launch {
            communityRepo.addComment(postId, text, author)
            triggerHaptic(30)
        }
    }

    fun openCreateDuaDialog() {
        _uiState.update { it.copy(isCreateDuaDialogOpen = true) }
    }

    fun closeCreateDuaDialog() {
        _uiState.update { it.copy(isCreateDuaDialogOpen = false) }
    }

    fun submitDuaRequest(title: String, details: String) {
        val author = _uiState.value.userProfile?.displayName ?: "Fellow Believer"
        viewModelScope.launch {
            communityRepo.createDuaRequest(title, details, author)
            _uiState.update { it.copy(isCreateDuaDialogOpen = false) }
            triggerHaptic(40)
        }
    }

    fun sayAmeenToDua(requestId: String) {
        communityRepo.sayAmeenToDua(requestId)
        triggerHaptic(35)
    }

    fun openReportPostDialog(postId: String) {
        _uiState.update { it.copy(reportingPostId = postId) }
    }

    fun closeReportPostDialog() {
        _uiState.update { it.copy(reportingPostId = null) }
    }

    fun submitReportPost(postId: String, reason: String) {
        communityRepo.reportPost(postId, reason)
        _uiState.update { it.copy(reportingPostId = null) }
        triggerHaptic(40)
    }

    fun openModerationPanel() {
        _uiState.update { it.copy(isModerationPanelOpen = true) }
    }

    fun closeModerationPanel() {
        _uiState.update { it.copy(isModerationPanelOpen = false) }
    }

    fun moderateHidePost(postId: String) {
        communityRepo.moderateHidePost(postId)
        triggerHaptic(40)
    }

    fun moderateDeletePost(postId: String) {
        communityRepo.moderateDeletePost(postId)
        triggerHaptic(40)
    }

    // ==========================================
    // Interactive Learning Modules Operations
    // ==========================================

    fun selectLearningTrack(trackId: String?) {
        _uiState.update { it.copy(selectedLearningTrackId = trackId, activeLearningLessonId = null) }
    }

    fun selectLearningLesson(lessonId: String?) {
        _uiState.update { it.copy(activeLearningLessonId = lessonId) }
    }

    fun startQuiz(lessonId: String) {
        _uiState.update {
            it.copy(
                activeQuizLessonId = lessonId,
                activeQuizAnswers = emptyMap(),
                isQuizSubmitted = false
            )
        }
        triggerHaptic(30)
    }

    fun selectQuizAnswer(questionId: String, optionIndex: Int) {
        val updated = _uiState.value.activeQuizAnswers.toMutableMap()
        updated[questionId] = optionIndex
        _uiState.update { it.copy(activeQuizAnswers = updated) }
        triggerHaptic(20)
    }

    fun submitQuiz() {
        val lessonId = _uiState.value.activeQuizLessonId ?: return
        val currentAnswers = _uiState.value.activeQuizAnswers
        val allLessons = InteractiveLearningModulesData.ALL_TRACKS.flatMap { it.lessons }
        val lesson = allLessons.firstOrNull { it.id == lessonId } ?: return

        val score = lesson.quizQuestions.count { currentAnswers[it.id] == it.correctOptionIndex }
        val completed = _uiState.value.completedLessonIds.toMutableSet()
        if (score == lesson.quizQuestions.size) {
            completed.add(lessonId)
        }

        _uiState.update {
            it.copy(
                isQuizSubmitted = true,
                completedLessonIds = completed
            )
        }
        triggerHaptic(50)
    }

    fun resetQuiz() {
        _uiState.update {
            it.copy(
                activeQuizAnswers = emptyMap(),
                isQuizSubmitted = false
            )
        }
    }

    fun closeQuizDialog() {
        _uiState.update { it.copy(activeQuizLessonId = null) }
    }

    // ==========================================
    // Sahaba Biographies Operations
    // ==========================================

    fun selectSahabi(sahabiId: String?) {
        _uiState.update { it.copy(selectedSahabiId = sahabiId) }
    }

    fun filterSahabaCategory(category: String) {
        _uiState.update { it.copy(sahabaFilterCategory = category) }
    }

    fun jumpToHadithFromSahaba(collection: String, hadithNumber: Int) {
        jumpToHadithCoordinate("$collection:$hadithNumber")
        _uiState.update { it.copy(currentTab = MainTab.HADITH_DUAS) }
        triggerHaptic(40)
    }

    // ==========================================
    // AI-Powered Spiritual Guide Operations
    // ==========================================

    fun loadPersonalizedSpiritualGuidance(focusArea: String = _uiState.value.aiGuideFocusArea) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiSpiritualGuideLoading = true, aiGuideFocusArea = focusArea) }
            try {
                val prayersToday = _uiState.value.prayerRecord.completedCount
                val streak = _uiState.value.fastingStreak
                val name = _uiState.value.userProfile?.displayName ?: "Believer"
                val guidance = aiSpiritualGuideEngine.generatePersonalizedGuidance(
                    focusArea = focusArea,
                    prayersCompletedToday = prayersToday,
                    fastingStreakDays = streak,
                    userDisplayName = name
                )
                _uiState.update {
                    it.copy(
                        aiSpiritualGuidance = guidance,
                        isAiSpiritualGuideLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isAiSpiritualGuideLoading = false) }
            }
        }
    }

    fun togglePracticalDeed(index: Int) {
        val current = _uiState.value.completedPracticalDeeds.toMutableSet()
        if (current.contains(index)) {
            current.remove(index)
        } else {
            current.add(index)
        }
        _uiState.update { it.copy(completedPracticalDeeds = current) }
        triggerHaptic(25)
    }
}

