package com.example.data.model

/**
 * Islamic Inheritance (Ilm al-Mirath / Mawarith) Models and Engine.
 * Implements Quranic fixed shares (Zawul-Fara'id per Surah An-Nisa 4:11-12, 4:176),
 * universal residuary (Asabah), proportional reduction ('Awl), surplus return (Al-Radd),
 * and 4-Madhab jurisprudence nuances.
 */

enum class DeceasedGender {
    MALE,
    FEMALE
}

data class EstateLiabilities(
    val grossEstate: Double = 100000.0,
    val funeralExpenses: Double = 2000.0,
    val debtsOwed: Double = 5000.0, // Debts to people + unpaid Mahr + unpaid Zakat
    val wasiyyahBequest: Double = 0.0 // Non-heir bequest (max 1/3 of net remaining)
) {
    val netAfterDebts: Double get() = (grossEstate - funeralExpenses - debtsOwed).coerceAtLeast(0.0)
    val maxAllowedWasiyyah: Double get() = netAfterDebts / 3.0
    val effectiveWasiyyah: Double get() = wasiyyahBequest.coerceIn(0.0, maxAllowedWasiyyah)
    val netDistributableEstate: Double get() = (netAfterDebts - effectiveWasiyyah).coerceAtLeast(0.0)
}

data class HeirCounts(
    val husbandCount: Int = 0,        // 0 or 1 (only if deceased is Female)
    val wifeCount: Int = 0,           // 0 to 4 (only if deceased is Male)
    val sonsCount: Int = 2,
    val daughtersCount: Int = 1,
    val fatherAlive: Boolean = true,
    val motherAlive: Boolean = true,
    val grandfatherAlive: Boolean = false, // Paternal Grandfather
    val grandmotherAlive: Boolean = false, // Paternal or Maternal Grandmother
    val fullBrothersCount: Int = 0,
    val fullSistersCount: Int = 0,
    val paternalBrothersCount: Int = 0,
    val paternalSistersCount: Int = 0,
    val maternalSiblingsCount: Int = 0
)

data class InheritanceShare(
    val heirId: String,
    val heirTitle: String,
    val arabicTitle: String,
    val count: Int,
    val shareFractionString: String,
    val percentage: Double,
    val totalAmount: Double,
    val amountPerPerson: Double,
    val basisQuranVerse: String,
    val fiqhNote: String = ""
)

data class MirathCalculationResult(
    val netEstate: Double,
    val shares: List<InheritanceShare>,
    val totalDistributedPercentage: Double,
    val totalDistributedAmount: Double,
    val isAwlApplied: Boolean = false,
    val isRaddApplied: Boolean = false,
    val madhab: Madhab = Madhab.HANAFI,
    val summaryNotes: List<String> = emptyList()
)

data class MirathCalculatorState(
    val deceasedGender: DeceasedGender = DeceasedGender.MALE,
    val liabilities: EstateLiabilities = EstateLiabilities(),
    val heirs: HeirCounts = HeirCounts(),
    val selectedMadhab: Madhab = Madhab.HANAFI,
    val currencyCode: String = "USD",
    val calculationResult: MirathCalculationResult? = null
)

object MirathCalculator {

    fun calculate(
        gender: DeceasedGender,
        liabilities: EstateLiabilities,
        heirs: HeirCounts,
        madhab: Madhab
    ): MirathCalculationResult {
        val netEstate = liabilities.netDistributableEstate
        if (netEstate <= 0.0) {
            return MirathCalculationResult(
                netEstate = netEstate,
                shares = emptyList(),
                totalDistributedPercentage = 0.0,
                totalDistributedAmount = 0.0,
                madhab = madhab,
                summaryNotes = listOf("Net distributable estate is zero or liabilities exceed estate value.")
            )
        }

        // Adjust spouses according to deceased gender
        val husband = if (gender == DeceasedGender.FEMALE) heirs.husbandCount.coerceIn(0, 1) else 0
        val wives = if (gender == DeceasedGender.MALE) heirs.wifeCount.coerceIn(0, 4) else 0

        val sons = heirs.sonsCount.coerceAtLeast(0)
        val daughters = heirs.daughtersCount.coerceAtLeast(0)
        val hasChildren = (sons + daughters) > 0

        val father = heirs.fatherAlive
        val mother = heirs.motherAlive
        val grandfather = heirs.grandfatherAlive && !father // Excluded if father is alive
        val grandmother = heirs.grandmotherAlive && !mother // Excluded if mother is alive

        val totalSiblings = heirs.fullBrothersCount + heirs.fullSistersCount +
                heirs.paternalBrothersCount + heirs.paternalSistersCount + heirs.maternalSiblingsCount

        // Raw share numerators over a common denominator 24
        // Base denominator: 24 (since 1/2, 1/3, 1/4, 1/6, 1/8 LCM is 24)
        val sharesMap = mutableMapOf<String, Double>() // fraction out of 1.0
        val notes = mutableListOf<String>()

        // 1. Spouse Share
        if (husband > 0) {
            val share = if (hasChildren) 0.25 else 0.50
            sharesMap["husband"] = share
            notes.add(if (hasChildren) "Husband receives 1/4 due to existence of children (Quran 4:12)." else "Husband receives 1/2 due to no children (Quran 4:12).")
        } else if (wives > 0) {
            val share = if (hasChildren) 0.125 else 0.25
            sharesMap["wives"] = share
            notes.add(if (hasChildren) "Wife/Wives share 1/8 collectively due to existence of children (Quran 4:12)." else "Wife/Wives share 1/4 collectively due to no children (Quran 4:12).")
        }

        // 2. Mother Share
        if (mother) {
            val share = if (hasChildren || totalSiblings >= 2) 1.0 / 6.0 else 1.0 / 3.0
            sharesMap["mother"] = share
            notes.add(if (hasChildren || totalSiblings >= 2) "Mother receives 1/6 due to children or 2+ siblings (Quran 4:11)." else "Mother receives 1/3 due to no children and <2 siblings (Quran 4:11).")
        }

        // 3. Father Share (Fixed part)
        if (father) {
            if (hasChildren) {
                if (sons > 0) {
                    sharesMap["father"] = 1.0 / 6.0
                    notes.add("Father receives 1/6 fixed share due to presence of male children (Quran 4:11).")
                } else {
                    // Only daughters: Father gets 1/6 fixed + Asabah remainder
                    sharesMap["father_fixed"] = 1.0 / 6.0
                    notes.add("Father receives 1/6 fixed share plus remainder as Asabah.")
                }
            } else {
                // No children: Father is pure Asabah
                notes.add("Father inherits as primary Residuary (Asabah) taking remaining estate.")
            }
        }

        // 4. Daughters Fixed Share (if no sons)
        if (daughters > 0 && sons == 0) {
            val share = if (daughters == 1) 0.50 else (2.0 / 3.0)
            sharesMap["daughters"] = share
            notes.add(if (daughters == 1) "Single daughter receives 1/2 fixed share (Quran 4:11)." else "$daughters daughters share 2/3 equally (Quran 4:11).")
        }

        // Sum of fixed shares
        val sumFixed = sharesMap.values.sum()
        var isAwl = false
        var isRadd = false

        val finalSharesList = mutableListOf<InheritanceShare>()

        if (sumFixed > 1.0001) {
            // 'Awl (Proportional reduction)
            isAwl = true
            notes.add("'Awl applied: Fixed shares exceeded 100%. All shares proportionally reduced to preserve equity.")
            sharesMap.forEach { (heirKey, shareVal) ->
                val adjustedPct = (shareVal / sumFixed) * 100.0
                val totalAmt = (shareVal / sumFixed) * netEstate
                addShareItem(heirKey, heirs, shareVal, adjustedPct, totalAmt, finalSharesList, madhab)
            }
        } else {
            val remainder = (1.0 - sumFixed).coerceAtLeast(0.0)

            // Residuary (Asabah) distribution
            if (sons > 0) {
                // Sons and Daughters as Asabah bi-Ghayriha (2:1 ratio)
                val totalParts = (sons * 2) + daughters
                val sonPartVal = (remainder / totalParts) * 2.0
                val daughterPartVal = remainder / totalParts

                // First add fixed shares
                sharesMap.forEach { (heirKey, shareVal) ->
                    val pct = shareVal * 100.0
                    val totalAmt = shareVal * netEstate
                    addShareItem(heirKey, heirs, shareVal, pct, totalAmt, finalSharesList, madhab)
                }

                if (sons > 0) {
                    val sonsTotalShare = sonPartVal * sons
                    finalSharesList.add(
                        InheritanceShare(
                            heirId = "sons",
                            heirTitle = if (sons == 1) "Son" else "Sons ($sons)",
                            arabicTitle = "الْأَبْنَاء",
                            count = sons,
                            shareFractionString = "Asabah (2:1)",
                            percentage = sonsTotalShare * 100.0,
                            totalAmount = sonsTotalShare * netEstate,
                            amountPerPerson = (sonsTotalShare * netEstate) / sons,
                            basisQuranVerse = "Surah An-Nisa 4:11",
                            fiqhNote = "Residuary heir (Asabah). Male takes twice the portion of female."
                        )
                    )
                }

                if (daughters > 0) {
                    val daughtersTotalShare = daughterPartVal * daughters
                    finalSharesList.add(
                        InheritanceShare(
                            heirId = "daughters_asabah",
                            heirTitle = if (daughters == 1) "Daughter" else "Daughters ($daughters)",
                            arabicTitle = "الْبَنَات",
                            count = daughters,
                            shareFractionString = "Asabah (2:1)",
                            percentage = daughtersTotalShare * 100.0,
                            totalAmount = daughtersTotalShare * netEstate,
                            amountPerPerson = (daughtersTotalShare * netEstate) / daughters,
                            basisQuranVerse = "Surah An-Nisa 4:11",
                            fiqhNote = "Residuary by brother (Asabah bi-Ghayriha). Ratio 2:1."
                        )
                    )
                }
            } else if (father && remainder > 0.0) {
                // Father takes remainder as Asabah
                val fatherExisting = sharesMap["father"] ?: sharesMap["father_fixed"] ?: 0.0
                val totalFatherShare = fatherExisting + remainder
                sharesMap.remove("father")
                sharesMap.remove("father_fixed")

                sharesMap.forEach { (heirKey, shareVal) ->
                    val pct = shareVal * 100.0
                    val totalAmt = shareVal * netEstate
                    addShareItem(heirKey, heirs, shareVal, pct, totalAmt, finalSharesList, madhab)
                }

                finalSharesList.add(
                    InheritanceShare(
                        heirId = "father",
                        heirTitle = "Father",
                        arabicTitle = "الْأَب",
                        count = 1,
                        shareFractionString = if (fatherExisting > 0) "1/6 + Asabah" else "Asabah",
                        percentage = totalFatherShare * 100.0,
                        totalAmount = totalFatherShare * netEstate,
                        amountPerPerson = totalFatherShare * netEstate,
                        basisQuranVerse = "Surah An-Nisa 4:11",
                        fiqhNote = "Universal residuary heir taking remaining balance."
                    )
                )
            } else if (remainder > 0.001) {
                // Surplus exists without male residuary: Radd (Return)
                isRadd = true
                notes.add("Radd applied: Surplus returned proportionally to eligible blood heirs per consensus.")
                // Return surplus to non-spouse heirs
                val nonSpouseSum = sharesMap.filterKeys { it != "husband" && it != "wives" }.values.sum()
                if (nonSpouseSum > 0) {
                    sharesMap.forEach { (heirKey, shareVal) ->
                        val finalShare = if (heirKey == "husband" || heirKey == "wives") {
                            shareVal
                        } else {
                            shareVal + (remainder * (shareVal / nonSpouseSum))
                        }
                        val pct = finalShare * 100.0
                        val amt = finalShare * netEstate
                        addShareItem(heirKey, heirs, finalShare, pct, amt, finalSharesList, madhab)
                    }
                } else {
                    // Only spouse: spouse receives entire remainder per contemporary Hanafi/Hanbali rulings
                    sharesMap.forEach { (heirKey, _) ->
                        addShareItem(heirKey, heirs, 1.0, 100.0, netEstate, finalSharesList, madhab)
                    }
                }
            } else {
                // Exact distribution
                sharesMap.forEach { (heirKey, shareVal) ->
                    val pct = shareVal * 100.0
                    val totalAmt = shareVal * netEstate
                    addShareItem(heirKey, heirs, shareVal, pct, totalAmt, finalSharesList, madhab)
                }
            }
        }

        // Madhab notes
        when (madhab) {
            Madhab.HANAFI -> notes.add("Hanafi School: Paternal grandfather completely excludes brothers and sisters like a father (Abu Hanifa's ruling).")
            Madhab.MALIKI -> notes.add("Maliki School: Grandfather shares estate with full siblings up to 1/3 or Muqasamah.")
            Madhab.SHAFII -> notes.add("Shafi'i School: Follows Zayd ibn Thabit's rule of Muqasamah with siblings.")
            Madhab.HANBALI -> notes.add("Hanbali School: Grandfather participates with siblings; Radd surplus returns to all blood relatives.")
        }

        val totalPct = finalSharesList.sumOf { it.percentage }
        val totalAmt = finalSharesList.sumOf { it.totalAmount }

        return MirathCalculationResult(
            netEstate = netEstate,
            shares = finalSharesList,
            totalDistributedPercentage = totalPct,
            totalDistributedAmount = totalAmt,
            isAwlApplied = isAwl,
            isRaddApplied = isRadd,
            madhab = madhab,
            summaryNotes = notes
        )
    }

    private fun addShareItem(
        heirKey: String,
        heirs: HeirCounts,
        rawFraction: Double,
        percentage: Double,
        amount: Double,
        list: MutableList<InheritanceShare>,
        madhab: Madhab
    ) {
        when (heirKey) {
            "husband" -> {
                list.add(
                    InheritanceShare(
                        heirId = "husband",
                        heirTitle = "Husband",
                        arabicTitle = "الزَّوْج",
                        count = 1,
                        shareFractionString = formatFraction(rawFraction),
                        percentage = percentage,
                        totalAmount = amount,
                        amountPerPerson = amount,
                        basisQuranVerse = "Surah An-Nisa 4:12"
                    )
                )
            }
            "wives" -> {
                val count = heirs.wifeCount.coerceIn(1, 4)
                list.add(
                    InheritanceShare(
                        heirId = "wives",
                        heirTitle = if (count == 1) "Wife" else "Wives ($count)",
                        arabicTitle = "الزَّوْجَات",
                        count = count,
                        shareFractionString = formatFraction(rawFraction),
                        percentage = percentage,
                        totalAmount = amount,
                        amountPerPerson = amount / count,
                        basisQuranVerse = "Surah An-Nisa 4:12",
                        fiqhNote = "Shared equally among all surviving wives."
                    )
                )
            }
            "mother" -> {
                list.add(
                    InheritanceShare(
                        heirId = "mother",
                        heirTitle = "Mother",
                        arabicTitle = "الْأُمّ",
                        count = 1,
                        shareFractionString = formatFraction(rawFraction),
                        percentage = percentage,
                        totalAmount = amount,
                        amountPerPerson = amount,
                        basisQuranVerse = "Surah An-Nisa 4:11"
                    )
                )
            }
            "father", "father_fixed" -> {
                list.add(
                    InheritanceShare(
                        heirId = "father",
                        heirTitle = "Father",
                        arabicTitle = "الْأَب",
                        count = 1,
                        shareFractionString = formatFraction(rawFraction),
                        percentage = percentage,
                        totalAmount = amount,
                        amountPerPerson = amount,
                        basisQuranVerse = "Surah An-Nisa 4:11"
                    )
                )
            }
            "daughters" -> {
                val count = heirs.daughtersCount.coerceAtLeast(1)
                list.add(
                    InheritanceShare(
                        heirId = "daughters",
                        heirTitle = if (count == 1) "Daughter" else "Daughters ($count)",
                        arabicTitle = "الْبَنَات",
                        count = count,
                        shareFractionString = if (count == 1) "1/2" else "2/3",
                        percentage = percentage,
                        totalAmount = amount,
                        amountPerPerson = amount / count,
                        basisQuranVerse = "Surah An-Nisa 4:11",
                        fiqhNote = if (count > 1) "2/3 shared equally among daughters." else "Sole daughter receives 1/2."
                    )
                )
            }
        }
    }

    private fun formatFraction(fraction: Double): String {
        return when {
            kotlin.math.abs(fraction - 0.5) < 0.01 -> "1/2"
            kotlin.math.abs(fraction - 0.25) < 0.01 -> "1/4"
            kotlin.math.abs(fraction - 0.125) < 0.01 -> "1/8"
            kotlin.math.abs(fraction - (2.0 / 3.0)) < 0.01 -> "2/3"
            kotlin.math.abs(fraction - (1.0 / 3.0)) < 0.01 -> "1/3"
            kotlin.math.abs(fraction - (1.0 / 6.0)) < 0.01 -> "1/6"
            else -> "%.1f%%".format(fraction * 100)
        }
    }
}
