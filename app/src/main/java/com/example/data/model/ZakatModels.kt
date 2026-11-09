package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Currency information supported by Deen365 Zakat Calculator.
 * Maintains real-time conversions and exchange rates.
 */
data class CurrencyInfo(
    val code: String,
    val name: String,
    val symbol: String,
    val flagEmoji: String,
    val usdExchangeRate: Double // e.g. 1.0 for USD, 3.75 for SAR, 278.5 for PKR
) {
    companion object {
        val ALL_CURRENCIES = listOf(
            CurrencyInfo("USD", "US Dollar", "$", "🇺🇸", 1.0),
            CurrencyInfo("SAR", "Saudi Riyal", "﷼", "🇸🇦", 3.75),
            CurrencyInfo("AED", "UAE Dirham", "د.إ", "🇦🇪", 3.67),
            CurrencyInfo("PKR", "Pakistani Rupee", "Rs", "🇵🇰", 278.50),
            CurrencyInfo("INR", "Indian Rupee", "₹", "🇮🇳", 84.10),
            CurrencyInfo("BDT", "Bangladeshi Taka", "৳", "🇧🇩", 119.80),
            CurrencyInfo("GBP", "British Pound", "£", "🇬🇧", 0.78),
            CurrencyInfo("EUR", "Euro", "€", "🇪🇺", 0.92),
            CurrencyInfo("CAD", "Canadian Dollar", "C$", "🇨🇦", 1.38),
            CurrencyInfo("AUD", "Australian Dollar", "A$", "🇦🇺", 1.51),
            CurrencyInfo("MYR", "Malaysian Ringgit", "RM", "🇲🇾", 4.38),
            CurrencyInfo("IDR", "Indonesian Rupiah", "Rp", "🇮🇩", 15650.0),
            CurrencyInfo("TRY", "Turkish Lira", "₺", "🇹🇷", 34.30),
            CurrencyInfo("QAR", "Qatari Riyal", "ر.ق", "🇶🇦", 3.64),
            CurrencyInfo("KWD", "Kuwaiti Dinar", "د.ك", "🇰🇼", 0.31)
        )

        fun findByCode(code: String): CurrencyInfo {
            return ALL_CURRENCIES.find { it.code.equals(code, ignoreCase = true) } ?: ALL_CURRENCIES.first()
        }
    }
}

/**
 * Gold purity levels according to carat standards.
 */
enum class GoldPurity(val label: String, val karat: Int, val purityFraction: Double) {
    K24("24 Karat (99.9% Pure)", 24, 1.0),
    K22("22 Karat (91.6% Pure)", 22, 22.0 / 24.0),
    K21("21 Karat (87.5% Pure)", 21, 21.0 / 24.0),
    K18("18 Karat (75.0% Pure)", 18, 18.0 / 24.0)
}

/**
 * Live gold and silver prices per gram in USD baseline, convertible to any currency.
 */
data class GoldSilverRates(
    val goldPricePerGramUSD: Double = 88.50, // Benchmark rate ~ $2,750 / troy oz
    val silverPricePerGramUSD: Double = 1.05, // Benchmark rate ~ $32.60 / troy oz
    val lastUpdatedTimestamp: Long = System.currentTimeMillis(),
    val isLiveFetched: Boolean = true,
    val sourceName: String = "Global Precious Metals & Forex Index"
) {
    fun getGoldPricePerGram(currency: CurrencyInfo, purity: GoldPurity = GoldPurity.K24): Double {
        val baseInCurrency = goldPricePerGramUSD * currency.usdExchangeRate
        return baseInCurrency * purity.purityFraction
    }

    fun getSilverPricePerGram(currency: CurrencyInfo): Double {
        return silverPricePerGramUSD * currency.usdExchangeRate
    }

    /**
     * Gold Nisab according to Hadith: 20 Mithqals / Dinars = 85.0 grams of 24K gold.
     */
    fun calculateGoldNisab(currency: CurrencyInfo, grams: Double = 85.0): Double {
        return getGoldPricePerGram(currency, GoldPurity.K24) * grams
    }

    /**
     * Silver Nisab according to Hadith: 200 Dirhams / 5 Awaq = 595.0 grams of pure silver.
     */
    fun calculateSilverNisab(currency: CurrencyInfo, grams: Double = 595.0): Double {
        return getSilverPricePerGram(currency) * grams
    }
}

/**
 * Nisab threshold standard choice:
 * - SILVER (595g): Most beneficial for the poor (Anfa' li al-Fuqara), supported by Hanafi consensus and classical tradition.
 * - GOLD (85g): Modern benchmark based on 20 Dinars.
 */
enum class NisabStandard(val label: String, val weightGrams: Double, val hadithMeasure: String) {
    SILVER("Silver Nisab (595g)", 595.0, "200 Dirhams (5 Awaq) - Preferred by Classical Consensus"),
    GOLD("Gold Nisab (85g)", 85.0, "20 Dinars (20 Mithqals) - Classical Gold Standard")
}

/**
 * User inputs for zakatable assets and immediate deductible liabilities.
 */
data class ZakatAssetInput(
    // 1. Cash & Bank
    val cashInHandAndBank: Double = 0.0,
    val foreignCurrenciesInLocalVal: Double = 0.0,

    // 2. Gold Assets (in grams)
    val goldGrams24k: Double = 0.0,
    val goldGrams22k: Double = 0.0,
    val goldGrams21k: Double = 0.0,
    val goldGrams18k: Double = 0.0,

    // 3. Silver Assets (in grams)
    val silverGrams: Double = 0.0,

    // 4. Trade & Investments
    val businessInventoryGoods: Double = 0.0,
    val sharesAndMutualFunds: Double = 0.0,
    val cryptoAssets: Double = 0.0,
    val moneyOwedToYouRecoverable: Double = 0.0,
    val otherZakatableAssets: Double = 0.0,

    // 5. Deductible Liabilities (Immediate)
    val immediateDebtsDueNow: Double = 0.0,
    val currentMonthBillsAndExpenses: Double = 0.0
)

/**
 * Calculated result conforming strictly to Prophetic Hadith rules (2.5% / 1/40th).
 */
data class ZakatCalculationResult(
    val totalCashValue: Double,
    val totalGoldValue: Double,
    val totalSilverValue: Double,
    val totalBusinessAndInvestments: Double,
    val grossAssets: Double,
    val totalLiabilities: Double,
    val netZakatableWealth: Double,
    val nisabThresholdGold: Double,
    val nisabThresholdSilver: Double,
    val selectedNisabStandard: NisabStandard,
    val effectiveNisabThreshold: Double,
    val isZakatEligible: Boolean, // netZakatableWealth >= effectiveNisabThreshold
    val zakatPayable: Double, // netZakatableWealth * 0.025 if eligible, else 0.0
    val currency: CurrencyInfo,
    val isHawlMet: Boolean = true,
    val calculationDateFormatted: String = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
) {
    val isZakatPayable: Boolean get() = isZakatEligible
    val zakatPayableAmount: Double get() = zakatPayable
    val isNetWealthAboveNisab: Boolean get() = netZakatableWealth >= effectiveNisabThreshold
}

/**
 * Saved historical record of past Zakat payments and calculations.
 */
data class SavedZakatRecord(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val currencyCode: String,
    val netWealth: Double,
    val zakatAmount: Double,
    val notes: String = "",
    val isPaid: Boolean = true,
    val nisabStandard: String = "Silver (595g)"
) {
    val formattedDate: String
        get() = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))
}

/**
 * The 8 Categories of Zakat Beneficiaries specified in the Holy Quran (Surah At-Tawbah 9:60).
 */
data class ZakatBeneficiary(
    val id: String,
    val arabicTitle: String,
    val englishTitle: String,
    val description: String,
    val iconEmoji: String,
    val number: Int = 1
) {
    val title: String get() = englishTitle
    val arabicName: String get() = arabicTitle

    companion object {
        val EIGHT_CATEGORIES = listOf(
            ZakatBeneficiary(
                id = "fuqara",
                arabicTitle = "الْفُقَرَاء",
                englishTitle = "The Poor (Al-Fuqara)",
                description = "Those who possess virtually nothing or less than half of their basic survival needs.",
                iconEmoji = "🤲",
                number = 1
            ),
            ZakatBeneficiary(
                id = "masakin",
                arabicTitle = "الْمَسَاكِين",
                englishTitle = "The Needy (Al-Masakin)",
                description = "Those who have some income or assets, but it falls substantially below their family's essential living threshold.",
                iconEmoji = "🍞",
                number = 2
            ),
            ZakatBeneficiary(
                id = "amilin",
                arabicTitle = "الْعَامِلِينَ عَلَيْهَا",
                englishTitle = "Zakat Administrators (Al-'Amilin)",
                description = "Official collectors, accountants, and distributors appointed by trustworthy Islamic institutions to manage Zakat funds.",
                iconEmoji = "📋",
                number = 3
            ),
            ZakatBeneficiary(
                id = "muallafah",
                arabicTitle = "الْمُؤَلَّفَةِ قُلُوبُهُمْ",
                englishTitle = "Reconciling Hearts (Al-Mu'allafah)",
                description = "New Muslims in vulnerable social circumstances or those whose hearts are to be softened towards the truth of Islam.",
                iconEmoji = "🤝",
                number = 4
            ),
            ZakatBeneficiary(
                id = "riqab",
                arabicTitle = "فِي الرِّقَاب",
                englishTitle = "Freeing Captives (Fi ar-Riqab)",
                description = "Ransoming oppressed captives, emancipating enslaved persons, and freeing human trafficking victims from bondage.",
                iconEmoji = "⛓️",
                number = 5
            ),
            ZakatBeneficiary(
                id = "gharimin",
                arabicTitle = "الْغَارِمِين",
                englishTitle = "The Debt-Ridden (Al-Gharimin)",
                description = "Those burdened by overwhelming legitimate debt incurred for essential medical, emergency, or living needs with no means to repay.",
                iconEmoji = "⚖️",
                number = 6
            ),
            ZakatBeneficiary(
                id = "sabilillah",
                arabicTitle = "فِي سَبِيلِ اللَّهِ",
                englishTitle = "In the Cause of Allah (Fi Sabilillah)",
                description = "Defending the oppressed, Islamic education, spreading authentic sacred knowledge, and community humanitarian welfare.",
                iconEmoji = "🛡️",
                number = 7
            ),
            ZakatBeneficiary(
                id = "ibn_sabil",
                arabicTitle = "ابْنِ السَّبِيل",
                englishTitle = "The Stranded Wayfarer (Ibn as-Sabil)",
                description = "A traveler stranded far from home who has lost their funds or documentation, even if they are well-off in their homeland.",
                iconEmoji = "🧭",
                number = 8
            )
        )

        val MASARIF_LIST: List<ZakatBeneficiary> get() = EIGHT_CATEGORIES
    }
}
