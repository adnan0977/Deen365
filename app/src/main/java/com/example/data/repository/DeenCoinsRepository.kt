package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CoinTransaction(
    val id: String,
    val amount: Int,
    val reason: String,
    val timestamp: Long,
    val isDeduction: Boolean = false
)

data class AdminOffer(
    val id: String,
    val title: String,
    val description: String,
    val rewardCoins: Int,
    val isEnabled: Boolean = true,
    val multiplier: Double = 1.0,
    val isSpecialPromo: Boolean = false
)

data class DeenCoinsState(
    val balance: Int = 48, // Default starter bonus for new users
    val isPremium: Boolean = false,
    val premiumExpiresTimestamp: Long = 0L,
    val premiumRemainingHours: Int = 0,
    val transactions: List<CoinTransaction> = emptyList(),
    val offers: List<AdminOffer> = emptyList(),
    val namazRewardClaimedDate: String = "",
    val fastingRewardClaimedDate: String = "",
    val quranChaptersCompleted: Set<Int> = emptySet(),
    val hadithChaptersCompleted: Set<String> = emptySet(),
    val recentRewardMessage: String? = null
)

class DeenCoinsRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("deen_coins_prefs", Context.MODE_PRIVATE)

    private val _coinsState = MutableStateFlow(loadInitialState())
    val coinsState: StateFlow<DeenCoinsState> = _coinsState.asStateFlow()

    init {
        checkAndRefreshPremiumStatus()
    }

    private fun loadInitialState(): DeenCoinsState {
        val balance = prefs.getInt(KEY_BALANCE, 48) // 48 coins starter = 2 days free premium preview
        val premiumExpiry = prefs.getLong(KEY_PREMIUM_EXPIRY, 0L)
        val namazDate = prefs.getString(KEY_NAMAZ_CLAIM_DATE, "") ?: ""
        val fastingDate = prefs.getString(KEY_FASTING_CLAIM_DATE, "") ?: ""
        val quranChapters = prefs.getStringSet(KEY_QURAN_CHAPTERS, emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        val hadithChapters = prefs.getStringSet(KEY_HADITH_CHAPTERS, emptySet()) ?: emptySet()

        val isPremium = premiumExpiry > System.currentTimeMillis()
        val remainingHours = if (isPremium) {
            val diffMs = premiumExpiry - System.currentTimeMillis()
            ((diffMs / (1000 * 60 * 60)) + 1).toInt().coerceAtLeast(1)
        } else 0

        val transactions = loadTransactions()
        val offers = loadOffers()

        return DeenCoinsState(
            balance = balance,
            isPremium = isPremium,
            premiumExpiresTimestamp = premiumExpiry,
            premiumRemainingHours = remainingHours,
            transactions = transactions,
            offers = offers,
            namazRewardClaimedDate = namazDate,
            fastingRewardClaimedDate = fastingDate,
            quranChaptersCompleted = quranChapters,
            hadithChaptersCompleted = hadithChapters
        )
    }

    fun checkAndRefreshPremiumStatus() {
        val now = System.currentTimeMillis()
        val expiry = prefs.getLong(KEY_PREMIUM_EXPIRY, 0L)
        val isPremium = expiry > now
        val remainingHours = if (isPremium) {
            val diffMs = expiry - now
            ((diffMs / (1000 * 60 * 60)) + 1).toInt().coerceAtLeast(1)
        } else 0

        _coinsState.update {
            it.copy(
                isPremium = isPremium,
                premiumExpiresTimestamp = expiry,
                premiumRemainingHours = remainingHours
            )
        }
    }

    fun earnCoins(amount: Int, reason: String, sourceId: String? = null): Boolean {
        if (amount <= 0) return false
        val activeMultiplier = _coinsState.value.offers
            .firstOrNull { it.id == "weekend_boost" && it.isEnabled }?.multiplier ?: 1.0

        val effectiveAmount = (amount * activeMultiplier).toInt().coerceAtLeast(1)
        val newBalance = _coinsState.value.balance + effectiveAmount

        val tx = CoinTransaction(
            id = System.currentTimeMillis().toString(),
            amount = effectiveAmount,
            reason = reason,
            timestamp = System.currentTimeMillis(),
            isDeduction = false
        )

        val updatedTransactions = (listOf(tx) + _coinsState.value.transactions).take(50)

        prefs.edit()
            .putInt(KEY_BALANCE, newBalance)
            .apply()
        saveTransactions(updatedTransactions)

        _coinsState.update {
            it.copy(
                balance = newBalance,
                transactions = updatedTransactions,
                recentRewardMessage = "+$effectiveAmount Deen Coins! ($reason)"
            )
        }
        return true
    }

    /**
     * Checks if all 5 prayers have been completed today.
     * If completed and not yet claimed for today, awards 24 coins (= 1 Day Premium).
     */
    fun checkAndReward5Namaz(fajr: Boolean, dhuhr: Boolean, asr: Boolean, maghrib: Boolean, isha: Boolean, dateStr: String) {
        if (fajr && dhuhr && asr && maghrib && isha) {
            val claimedDate = _coinsState.value.namazRewardClaimedDate
            if (claimedDate != dateStr) {
                val offer = _coinsState.value.offers.firstOrNull { it.id == "5_namaz" }
                val reward = if (offer != null && offer.isEnabled) offer.rewardCoins else 24

                earnCoins(reward, "Completed all 5 Daily Prayers (Namaz)")
                prefs.edit().putString(KEY_NAMAZ_CLAIM_DATE, dateStr).apply()
                _coinsState.update { it.copy(namazRewardClaimedDate = dateStr) }
            }
        }
    }

    /**
     * Checks if fasting has been completed/marked for today.
     * If completed and not yet claimed for today, awards 24 coins (= 1 Day Premium).
     */
    fun checkAndRewardFasting(isFasting: Boolean, dateStr: String) {
        if (isFasting) {
            val claimedDate = _coinsState.value.fastingRewardClaimedDate
            if (claimedDate != dateStr) {
                val offer = _coinsState.value.offers.firstOrNull { it.id == "fasting" }
                val reward = if (offer != null && offer.isEnabled) offer.rewardCoins else 24

                earnCoins(reward, "Recorded a Full Day of Fasting (Sawm)")
                prefs.edit().putString(KEY_FASTING_CLAIM_DATE, dateStr).apply()
                _coinsState.update { it.copy(fastingRewardClaimedDate = dateStr) }
            }
        }
    }

    /**
     * Checks if a Quran Surah chapter was read. Awards 24 coins per chapter.
     */
    fun rewardQuranChapterCompleted(surahNumber: Int, surahName: String) {
        val completed = _coinsState.value.quranChaptersCompleted
        if (!completed.contains(surahNumber)) {
            val offer = _coinsState.value.offers.firstOrNull { it.id == "quran_chapter" }
            val reward = if (offer != null && offer.isEnabled) offer.rewardCoins else 24

            earnCoins(reward, "Completed Quran Chapter: Surah $surahName")
            val updated = completed + surahNumber
            prefs.edit().putStringSet(KEY_QURAN_CHAPTERS, updated.map { it.toString() }.toSet()).apply()
            _coinsState.update { it.copy(quranChaptersCompleted = updated) }
        }
    }

    /**
     * Checks if a Hadith milestone/chapter was read. Awards 24 coins per milestone.
     */
    fun rewardHadithChapterCompleted(chapterId: String, title: String) {
        val completed = _coinsState.value.hadithChaptersCompleted
        if (!completed.contains(chapterId)) {
            val offer = _coinsState.value.offers.firstOrNull { it.id == "hadith_chapter" }
            val reward = if (offer != null && offer.isEnabled) offer.rewardCoins else 24

            earnCoins(reward, "Studied Hadith Chapter: $title")
            val updated = completed + chapterId
            prefs.edit().putStringSet(KEY_HADITH_CHAPTERS, updated).apply()
            _coinsState.update { it.copy(hadithChaptersCompleted = updated) }
        }
    }

    /**
     * Spends coins manually to unlock Premium Access (1 coin = 1 hour).
     * Common options: 24 coins (24 hours / 1 day), 72 coins (3 days), 168 coins (7 days).
     */
    fun purchasePremiumWithCoins(hours: Int): Boolean {
        if (hours <= 0) return false
        val currentBalance = _coinsState.value.balance
        if (currentBalance < hours) return false

        val newBalance = currentBalance - hours
        val now = System.currentTimeMillis()
        val currentExpiry = _coinsState.value.premiumExpiresTimestamp
        val baseTime = if (currentExpiry > now) currentExpiry else now
        val newExpiry = baseTime + (hours.toLong() * 3600 * 1000L)

        val tx = CoinTransaction(
            id = System.currentTimeMillis().toString(),
            amount = hours,
            reason = "Unlocked $hours Hours of Premium Access",
            timestamp = System.currentTimeMillis(),
            isDeduction = true
        )
        val updatedTransactions = (listOf(tx) + _coinsState.value.transactions).take(50)

        prefs.edit()
            .putInt(KEY_BALANCE, newBalance)
            .putLong(KEY_PREMIUM_EXPIRY, newExpiry)
            .apply()
        saveTransactions(updatedTransactions)

        val remainingHours = (((newExpiry - now) / (1000 * 60 * 60)) + 1).toInt().coerceAtLeast(1)

        _coinsState.update {
            it.copy(
                balance = newBalance,
                isPremium = true,
                premiumExpiresTimestamp = newExpiry,
                premiumRemainingHours = remainingHours,
                transactions = updatedTransactions,
                recentRewardMessage = "Premium Activated for $hours Hours! Enjoy all Pro features."
            )
        }
        return true
    }

    fun clearRecentRewardMessage() {
        _coinsState.update { it.copy(recentRewardMessage = null) }
    }

    // --- Admin Panel Operations ---

    fun toggleOfferEnabled(offerId: String, isEnabled: Boolean) {
        val updated = _coinsState.value.offers.map {
            if (it.id == offerId) it.copy(isEnabled = isEnabled) else it
        }
        saveOffers(updated)
        _coinsState.update { it.copy(offers = updated) }
    }

    fun updateOfferReward(offerId: String, rewardCoins: Int) {
        val updated = _coinsState.value.offers.map {
            if (it.id == offerId) it.copy(rewardCoins = rewardCoins.coerceAtLeast(1)) else it
        }
        saveOffers(updated)
        _coinsState.update { it.copy(offers = updated) }
    }

    fun updateOfferMultiplier(offerId: String, multiplier: Double) {
        val updated = _coinsState.value.offers.map {
            if (it.id == offerId) it.copy(multiplier = multiplier) else it
        }
        saveOffers(updated)
        _coinsState.update { it.copy(offers = updated) }
    }

    fun addCustomAdminOffer(title: String, description: String, rewardCoins: Int) {
        val newOffer = AdminOffer(
            id = "custom_${System.currentTimeMillis()}",
            title = title,
            description = description,
            rewardCoins = rewardCoins,
            isEnabled = true,
            isSpecialPromo = true
        )
        val updated = _coinsState.value.offers + newOffer
        saveOffers(updated)
        _coinsState.update { it.copy(offers = updated) }
    }

    fun adminGrantTestCoins(amount: Int) {
        earnCoins(amount, "Admin Test Coin Grant")
    }

    fun adminToggleInstantPremium(hours: Int = 24) {
        val now = System.currentTimeMillis()
        val currentExpiry = _coinsState.value.premiumExpiresTimestamp
        val newExpiry = if (currentExpiry > now) 0L else now + (hours.toLong() * 3600 * 1000L)
        val isPremium = newExpiry > now
        val remainingHours = if (isPremium) hours else 0

        prefs.edit().putLong(KEY_PREMIUM_EXPIRY, newExpiry).apply()
        _coinsState.update {
            it.copy(
                isPremium = isPremium,
                premiumExpiresTimestamp = newExpiry,
                premiumRemainingHours = remainingHours,
                recentRewardMessage = if (isPremium) "Admin: Premium Forced ON ($hours hrs)" else "Admin: Premium Forced OFF"
            )
        }
    }

    fun adminResetCoins() {
        prefs.edit()
            .putInt(KEY_BALANCE, 24)
            .putLong(KEY_PREMIUM_EXPIRY, 0L)
            .remove(KEY_NAMAZ_CLAIM_DATE)
            .remove(KEY_FASTING_CLAIM_DATE)
            .apply()
        _coinsState.update {
            it.copy(
                balance = 24,
                isPremium = false,
                premiumExpiresTimestamp = 0L,
                premiumRemainingHours = 0,
                namazRewardClaimedDate = "",
                fastingRewardClaimedDate = "",
                recentRewardMessage = "Deen Coins & Premium reset to default"
            )
        }
    }

    // --- Persistence Helpers ---

    private fun loadTransactions(): List<CoinTransaction> {
        val jsonStr = prefs.getString(KEY_TRANSACTIONS, null) ?: return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<CoinTransaction>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CoinTransaction(
                        id = obj.optString("id", i.toString()),
                        amount = obj.optInt("amount", 0),
                        reason = obj.optString("reason", ""),
                        timestamp = obj.optLong("timestamp", 0L),
                        isDeduction = obj.optBoolean("isDeduction", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveTransactions(transactions: List<CoinTransaction>) {
        try {
            val array = JSONArray()
            transactions.take(50).forEach { tx ->
                val obj = JSONObject()
                obj.put("id", tx.id)
                obj.put("amount", tx.amount)
                obj.put("reason", tx.reason)
                obj.put("timestamp", tx.timestamp)
                obj.put("isDeduction", tx.isDeduction)
                array.put(obj)
            }
            prefs.edit().putString(KEY_TRANSACTIONS, array.toString()).apply()
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun loadOffers(): List<AdminOffer> {
        val jsonStr = prefs.getString(KEY_OFFERS, null)
        if (jsonStr != null) {
            try {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<AdminOffer>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        AdminOffer(
                            id = obj.optString("id"),
                            title = obj.optString("title"),
                            description = obj.optString("description"),
                            rewardCoins = obj.optInt("rewardCoins", 24),
                            isEnabled = obj.optBoolean("isEnabled", true),
                            multiplier = obj.optDouble("multiplier", 1.0),
                            isSpecialPromo = obj.optBoolean("isSpecialPromo", false)
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                // fallback to default
            }
        }
        return getDefaultOffers()
    }

    private fun saveOffers(offers: List<AdminOffer>) {
        try {
            val array = JSONArray()
            offers.forEach { o ->
                val obj = JSONObject()
                obj.put("id", o.id)
                obj.put("title", o.title)
                obj.put("description", o.description)
                obj.put("rewardCoins", o.rewardCoins)
                obj.put("isEnabled", o.isEnabled)
                obj.put("multiplier", o.multiplier)
                obj.put("isSpecialPromo", o.isSpecialPromo)
                array.put(obj)
            }
            prefs.edit().putString(KEY_OFFERS, array.toString()).apply()
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun getDefaultOffers(): List<AdminOffer> {
        return listOf(
            AdminOffer(
                id = "5_namaz",
                title = "Pray All 5 Daily Namaz",
                description = "Complete Fajr, Dhuhr, Asr, Maghrib, and Isha in a single day to earn 1 full day of premium (24 Coins).",
                rewardCoins = 24,
                isEnabled = true
            ),
            AdminOffer(
                id = "fasting",
                title = "Complete a Day of Fasting (Sawm)",
                description = "Record a completed fast from Fajr to Maghrib to earn 1 full day of free premium (24 Coins).",
                rewardCoins = 24,
                isEnabled = true
            ),
            AdminOffer(
                id = "quran_chapter",
                title = "Recite a Full Quran Chapter (Surah)",
                description = "Complete reading any chapter of the Holy Quran to earn 24 Deen Coins.",
                rewardCoins = 24,
                isEnabled = true
            ),
            AdminOffer(
                id = "hadith_chapter",
                title = "Read a Hadith Collection Chapter",
                description = "Study a Hadith chapter milestone from Bukhari or Muslim to earn 24 Deen Coins.",
                rewardCoins = 24,
                isEnabled = true
            ),
            AdminOffer(
                id = "weekend_boost",
                title = "Weekend 2x Deen Multiplier",
                description = "Special promotional bonus granting double coins on all spiritual quests.",
                rewardCoins = 0,
                isEnabled = false,
                multiplier = 2.0,
                isSpecialPromo = true
            )
        )
    }

    companion object {
        private const val KEY_BALANCE = "deen_coins_balance"
        private const val KEY_PREMIUM_EXPIRY = "deen_premium_expiry"
        private const val KEY_NAMAZ_CLAIM_DATE = "namaz_reward_claim_date"
        private const val KEY_FASTING_CLAIM_DATE = "fasting_reward_claim_date"
        private const val KEY_QURAN_CHAPTERS = "quran_chapters_claimed"
        private const val KEY_HADITH_CHAPTERS = "hadith_chapters_claimed"
        private const val KEY_TRANSACTIONS = "deen_coin_transactions"
        private const val KEY_OFFERS = "deen_admin_offers"
    }
}
