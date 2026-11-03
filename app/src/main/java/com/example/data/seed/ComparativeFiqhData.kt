package com.example.data.seed

data class SchoolPosition(
    val madhhab: String, // "Hanafi", "Shafi'i", "Maliki", "Hanbali"
    val position: String,
    val textualProof: String
)

data class FiqhComparisonItem(
    val id: String,
    val category: String, // "Taharah & Wudu", "Salah & Prayer", "Fasting & Ramadan", "Zakat & Wealth"
    val issueTitle: String,
    val question: String,
    val consensusNote: String,
    val schoolPositions: List<SchoolPosition>
)

object ComparativeFiqhData {

    val ISSUES = listOf(
        FiqhComparisonItem(
            id = "salah_hand_placement",
            category = "Salah & Prayer",
            issueTitle = "Placement of Hands in Qiyam (Standing)",
            question = "Where are the hands placed during standing in prayer after Takbir al-Ihram?",
            consensusNote = "All schools agree that folding or placing hands right over left is established in the Sunnah (with Malikis also viewing Sadl - side hands - as valid).",
            schoolPositions = listOf(
                SchoolPosition(
                    madhhab = "Hanafi",
                    position = "Below the navel for men, resting on the chest for women.",
                    textualProof = "Reported by Ali (RA): 'It is from the Sunnah to place the right hand over the left below the navel in prayer.' (Abu Dawud)"
                ),
                SchoolPosition(
                    madhhab = "Shafi'i",
                    position = "Above the navel and below the chest, inclined slightly to the left over the heart.",
                    textualProof = "Ibn Khuzaymah narration from Wa'il ibn Hujr: 'I prayed with the Messenger of Allah and he placed his right hand over his left hand upon his chest.'"
                ),
                SchoolPosition(
                    madhhab = "Maliki",
                    position = "Sadl (leaving hands at sides) in obligatory prayers; placing on chest in voluntary prayers is encouraged.",
                    textualProof = "Imam Malik noted the prevailing continuous practice ('Amal) of the scholars of Madinah who let their hands rest at their sides."
                ),
                SchoolPosition(
                    madhhab = "Hanbali",
                    position = "Below the navel or directly over the lower chest (both are valid narrations from Imam Ahmad).",
                    textualProof = "Wa'il ibn Hujr and Ali (RA) narrations, with Imam Ahmad observing both practices are sound."
                )
            )
        ),
        FiqhComparisonItem(
            id = "salah_ameen_aloud",
            category = "Salah & Prayer",
            issueTitle = "Saying Ameen Aloud in Audible Prayers",
            question = "Should the follower say 'Ameen' aloud or silently behind the Imam during Fajr, Maghrib, and Isha?",
            consensusNote = "All four schools agree that saying Ameen brings immense reward and seals the supplication of Surah Al-Fatihah.",
            schoolPositions = listOf(
                SchoolPosition(
                    madhhab = "Hanafi",
                    position = "Said silently by both the Imam and the followers in all prayers.",
                    textualProof = "Quran 7:55 ('Call upon your Lord in humility and in secret') and hadith of Ibn Mas'ud that Ameen is a supplication recited softly."
                ),
                SchoolPosition(
                    madhhab = "Shafi'i",
                    position = "Said aloud by both the Imam and the followers in all audible prayers.",
                    textualProof = "Sahih al-Bukhari: 'When the Imam says Ameen, say Ameen, for if one's Ameen coincides with the angels, past sins are forgiven.'"
                ),
                SchoolPosition(
                    madhhab = "Maliki",
                    position = "Said silently by the follower; the Imam does not say it aloud in audible prayers.",
                    textualProof = "Practice of the people of Madinah where the congregation softly confirms the Imam's invocation."
                ),
                SchoolPosition(
                    madhhab = "Hanbali",
                    position = "Said aloud by the Imam and the followers in audible prayers.",
                    textualProof = "Hadith of Abu Hurairah where the Prophet (pbuh) raised his voice with Ameen so those in the first row heard him clearly."
                )
            )
        ),
        FiqhComparisonItem(
            id = "wudu_touching_skin",
            category = "Taharah & Wudu",
            issueTitle = "Touching the Opposite Gender & Wudu",
            question = "Does casual skin-to-skin contact with a non-Mahram person nullify Wudu?",
            consensusNote = "All agree that touching with deliberate sexual desire invalidates Wudu according to most jurists.",
            schoolPositions = listOf(
                SchoolPosition(
                    madhhab = "Hanafi",
                    position = "Does not break Wudu unless it leads to physical arousal/emission.",
                    textualProof = "Hadith of Aishah (RA) that the Prophet kissed one of his wives and then went out to prayer without repeating Wudu."
                ),
                SchoolPosition(
                    madhhab = "Shafi'i",
                    position = "Breaks Wudu unconditionally if skin touches directly without a barrier.",
                    textualProof = "Literal meaning of Surah An-Nisa 4:43: '...or you have touched women (aw lāmastumu an-nisā') and find not water.'"
                ),
                SchoolPosition(
                    madhhab = "Maliki",
                    position = "Breaks Wudu only if done with physical pleasure or desire (Shahwah).",
                    textualProof = "Balancing the Quranic verse with the Prophetic practice of kissing his wives prior to Salah."
                ),
                SchoolPosition(
                    madhhab = "Hanbali",
                    position = "Breaks Wudu only if skin contact occurs with desire/pleasure.",
                    textualProof = "Corroborates the Maliki synthesis between the verse and authentic Sunnah traditions."
                )
            )
        ),
        FiqhComparisonItem(
            id = "fasting_intention_timing",
            category = "Fasting & Ramadan",
            issueTitle = "Timing of the Intention (Niyyah) for Fasting",
            question = "When must the intention to fast Ramadan be formed in the heart?",
            consensusNote = "The intention is located in the heart and vocalization is not a strict condition in any school.",
            schoolPositions = listOf(
                SchoolPosition(
                    madhhab = "Hanafi",
                    position = "Valid if formed anytime before the Islamic midday (Dahwah Kubra).",
                    textualProof = "Hadith of Salamah ibn al-Akwa on the day of Ashura where the Prophet instructed those who had not eaten to fast."
                ),
                SchoolPosition(
                    madhhab = "Shafi'i",
                    position = "Must be made every single night before the true dawn (Fajr).",
                    textualProof = "Prophetic hadith: 'Whoever does not establish the fast before dawn has no fast.' (An-Nasa'i)"
                ),
                SchoolPosition(
                    madhhab = "Maliki",
                    position = "A single comprehensive intention at the start of Ramadan suffices for the entire month.",
                    textualProof = "Ramadan is one unified act of continuous worship unless interrupted by sickness or travel."
                ),
                SchoolPosition(
                    madhhab = "Hanbali",
                    position = "Must be renewed every night before Fajr for each obligatory day.",
                    textualProof = "Each day of Ramadan is an independent obligation requiring individual determination."
                )
            )
        ),
        FiqhComparisonItem(
            id = "zakat_personal_jewelry",
            category = "Zakat & Wealth",
            issueTitle = "Zakat on Women's Personal Gold Jewelry",
            question = "Is Zakat due on lawful gold jewelry kept for personal use and adornment?",
            consensusNote = "All agree that gold held as an investment, bullion, or exceeding normal customary limits is subject to 2.5% Zakat.",
            schoolPositions = listOf(
                SchoolPosition(
                    madhhab = "Hanafi",
                    position = "Obligatory (2.5%) on all gold and silver reaching Nisab, even if worn personally.",
                    textualProof = "A woman visited the Prophet with her daughter wearing heavy gold bangles; he asked: 'Do you pay zakat on this?' and warned of hellfire. (Abu Dawud)"
                ),
                SchoolPosition(
                    madhhab = "Shafi'i",
                    position = "Exempt from Zakat if kept for customary permissible personal adornment.",
                    textualProof = "Narration of Jabir (RA): 'There is no zakat on jewelry' and analogy to personal clothes and household items."
                ),
                SchoolPosition(
                    madhhab = "Maliki",
                    position = "Exempt from Zakat if within customary limits for personal wear.",
                    textualProof = "Reported that Aisha (RA) looked after her brother's orphaned daughters who wore jewelry and did not pay zakat on it."
                ),
                SchoolPosition(
                    madhhab = "Hanbali",
                    position = "Exempt from Zakat for customary personal use; liable if kept as financial reserve.",
                    textualProof = "Distinction between personal utility items and wealth meant for growth (Namaa)."
                )
            )
        )
    )
}
