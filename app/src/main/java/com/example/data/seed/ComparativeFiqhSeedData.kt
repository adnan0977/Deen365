package com.example.data.seed

import com.example.data.model.ComparativeFiqhTopic
import com.example.data.model.Madhab
import com.example.data.model.MadhabRuling

object ComparativeFiqhSeedData {

    val TOPICS: List<ComparativeFiqhTopic> = listOf(
        ComparativeFiqhTopic(
            topicId = "wudu_intentions_order",
            title = "Order (Tartib) and Intention (Niyyah) in Wudu",
            category = "Taharah & Wudu",
            coreQuestion = "Is maintaining the sequential order and formulating the verbal/internal intention an absolute pillar (Fard/Rukn) of Wudu?",
            consensusPoints = "All four schools agree that washing the face, arms to elbows, wiping the head, and washing the feet to ankles are explicitly commanded in Surah Al-Ma'idah (5:6).",
            rulings = listOf(
                MadhabRuling(
                    madhab = Madhab.HANAFI,
                    rulingSummary = "Niyyah and Tartib are confirmed Sunnahs (Sunnah Mu'akkadah), not invalidating Fard conditions.",
                    legalGrade = "Sunnah Mu'akkadah",
                    detailedExplanation = "Water is inherently pure and purifying (Tahur). If one plunges into water or washes the 4 organs out of order without specific intention, the state of minor ritual impurity (Hadath) is still lifted.",
                    primaryDalil = "Quran 5:6 mentions the organs with the conjunction 'Wa' (and), which in classical Arabic grammar indicates combination without requiring chronological sequence.",
                    scholarlyNote = "Intention remains highly rewarded; omitting it without reason is disliked."
                ),
                MadhabRuling(
                    madhab = Madhab.MALIKI,
                    rulingSummary = "Niyyah is an obligatory pillar (Fard); Tartib is a Sunnah.",
                    legalGrade = "Fard (Intention) / Sunnah (Order)",
                    detailedExplanation = "Wudu is an act of devoted worship ('Ibadah) distinguished from customary cooling or hygiene exclusively through the conscious spiritual intention in the heart.",
                    primaryDalil = "Hadith: 'Actions are judged only by intentions' (Bukhari & Muslim).",
                    scholarlyNote = "Continuous washing without long interruption (Muwalah) is also mandatory in the Maliki school."
                ),
                MadhabRuling(
                    madhab = Madhab.SHAFII,
                    rulingSummary = "Both Intention (Niyyah) and Sequential Order (Tartib) are integral pillars (Arkan).",
                    legalGrade = "Rukn (Integral Pillar)",
                    detailedExplanation = "The intention must accompany the first wash of the face. Skipping order or washing the feet before arms invalidates the ablution.",
                    primaryDalil = "The Prophet ﷺ always performed Wudu sequentially and said: 'Begin with that which Allah began with.'",
                    scholarlyNote = "Imam ash-Shafi'i emphasized that Allah listed wiping the head between the two washings (arms and feet) deliberately to enforce order."
                ),
                MadhabRuling(
                    madhab = Madhab.HANBALI,
                    rulingSummary = "Both Niyyah and Tartib, along with continuity (Muwalah), are obligatory conditions.",
                    legalGrade = "Wajib / Shart",
                    detailedExplanation = "Ablution performed contrary to the prophetic sequence or without prior intention does not permit one to pray.",
                    primaryDalil = "Authentic narrations in Sunan Abi Dawud where the Prophet ﷺ instructed a person who washed out of sequence to repeat his ablution.",
                    scholarlyNote = "Also considers reciting the Basmalah at the beginning to be Wajib if remembered."
                )
            )
        ),
        ComparativeFiqhTopic(
            topicId = "salah_hand_placement",
            title = "Placement of Hands in Standing Prayer (Qiyam)",
            category = "Salah (Prayer)",
            coreQuestion = "Where should the hands be placed during the standing recitation in prayer?",
            consensusPoints = "Placing the right hand over the left wrist/forearm is a validated prophetic practice reported across numerous Sahaba.",
            rulings = listOf(
                MadhabRuling(
                    madhab = Madhab.HANAFI,
                    rulingSummary = "Men place hands below the navel; women place hands upon the chest.",
                    legalGrade = "Sunnah",
                    detailedExplanation = "The right hand grasps the left wrist with the thumb and little finger forming a ring below the navel. For women, placing hands over the chest provides maximum modesty (Satr).",
                    primaryDalil = "Report of Ali ibn Abi Talib (RA): 'From the Sunnah in prayer is placing the right hand over the left below the navel' (Musnad Ahmad & Abu Dawud).",
                    scholarlyNote = "Represents physical humility and reverence before the Creator."
                ),
                MadhabRuling(
                    madhab = Madhab.MALIKI,
                    rulingSummary = "Leaving arms hanging at the sides (Sadl) is well-known; placing hands above the navel (Qabd) is also permissible/preferred by many Maliki scholars.",
                    legalGrade = "Mubah / Mustahabb",
                    detailedExplanation = "Imam Malik preferred Sadl (leaving hands at the sides) for obligatory prayers based on the continuous practice ('Amal) of the scholars of Madinah, while validating Qabd in voluntary prayers.",
                    primaryDalil = "The continuous preserved living practice of the early generations of Madinah ('Amal Ahl al-Madinah).",
                    scholarlyNote = "Later Maliki masters like Ibn Abd al-Barr and Al-Baji affirmed that both Sadl and Qabd are authentic Sunnahs."
                ),
                MadhabRuling(
                    madhab = Madhab.SHAFII,
                    rulingSummary = "Hands placed above the navel and below the chest, inclined slightly towards the heart.",
                    legalGrade = "Sunnah",
                    detailedExplanation = "The right hand grasps the left forearm resting on the upper abdomen/chest area, oriented respectfully toward the heart.",
                    primaryDalil = "Hadith of Wa'il ibn Hujr (RA): 'I prayed with the Messenger of Allah ﷺ and he placed his right hand over his left hand upon his chest/upper abdomen' (Sahih Ibn Khuzaymah).",
                    scholarlyNote = "Symbolizes guarding the heart and maintaining undivided spiritual focus."
                ),
                MadhabRuling(
                    madhab = Madhab.HANBALI,
                    rulingSummary = "Hands placed below the navel or directly upon the chest; both options are validated in the school.",
                    legalGrade = "Sunnah",
                    detailedExplanation = "Imam Ahmad recorded multiple transmissions: placing the hands below the navel is the predominant position, with placing them on the chest considered equally permissible.",
                    primaryDalil = "Multiple reports recorded in the Musnad of Imam Ahmad demonstrating breadth in the prophetic practice.",
                    scholarlyNote = "Imam Ahmad said: 'There is leeway in the matter; whether one places them below or above the navel, both are Sunnah.'"
                )
            )
        ),
        ComparativeFiqhTopic(
            topicId = "qunut_fajr",
            title = "Recitation of Qunut in the Fajr Morning Prayer",
            category = "Salah (Prayer)",
            coreQuestion = "Is reciting the supplication of Qunut in the second Rak'ah of Fajr a permanent daily Sunnah, or reserved for calamities (Nazilah)?",
            consensusPoints = "All four schools agree that Qunut is definitely prescribed during major trials and calamities (Qunut an-Nazilah) in the daily prayers.",
            rulings = listOf(
                MadhabRuling(
                    madhab = Madhab.HANAFI,
                    rulingSummary = "Qunut is obligatory (Wajib) in Witr prayer; not prescribed in regular daily Fajr prayer except during public calamity (Nazilah).",
                    legalGrade = "Wajib in Witr / Disliked in regular Fajr",
                    detailedExplanation = "The Prophet ﷺ recited Qunut in Fajr for one month supplicating against specific tribes who killed the reciters of Bi'r Ma'unah, then ceased.",
                    primaryDalil = "Hadith of Abu Hurairah and Anas (RA) that the Prophet's Fajr Qunut was a temporary measure for one month (Sahih Bukhari).",
                    scholarlyNote = "If an individual prays behind a Shafi'i Imam who recites Qunut in Fajr, the Hanafi follower remains quiet and says 'Ameen'."
                ),
                MadhabRuling(
                    madhab = Madhab.MALIKI,
                    rulingSummary = "Qunut in Fajr is an established Sunnah (Mustahabb), recited silently before bowing (Ruku').",
                    legalGrade = "Mustahabb (Recommended)",
                    detailedExplanation = "Preferably recited quietly in the second Rak'ah immediately after the Quranic recitation before bowing, though reciting after rising is permissible.",
                    primaryDalil = "Transmission of the continuous practice of the people of Madinah from the era of the Khulafa ar-Rashidun.",
                    scholarlyNote = "Malikis recite the classical supplication: 'Allahumma inna nasta'inuka wa nastaghfiruka...'"
                ),
                MadhabRuling(
                    madhab = Madhab.SHAFII,
                    rulingSummary = "Qunut in Fajr is an emphasized Sunnah (Sunnah Ab'ad); if omitted, Sujud as-Sahw is recommended.",
                    legalGrade = "Sunnah Ab'ad (Major Sunnah)",
                    detailedExplanation = "Recited aloud by the Imam in the second Rak'ah after rising from Ruku' with hands raised. Omission warrants prostration of forgetfulness (Sujud as-Sahw).",
                    primaryDalil = "Hadith of Anas ibn Malik (RA): 'The Messenger of Allah ﷺ did not cease reciting Qunut in the morning prayer until he departed this world' (Musnad Ahmad & Daraqutni).",
                    scholarlyNote = "The congregants raise their hands and say 'Ameen' to the Imam's supplication."
                ),
                MadhabRuling(
                    madhab = Madhab.HANBALI,
                    rulingSummary = "Qunut in regular Fajr is not prescribed; reserved strictly for Witr and general crises (Qunut an-Nazilah).",
                    legalGrade = "Sunnah in Witr & Calamity / Disliked in routine Fajr",
                    detailedExplanation = "Similar to the Hanafi position, regular daily Fajr Qunut is considered non-standard Sunnah, while highly encouraged when the Ummah undergoes oppression or affliction.",
                    primaryDalil = "Statements of senior Sahaba including Abdullah ibn Mas'ud and Ibn Umar that regular Fajr Qunut was not their daily habit.",
                    scholarlyNote = "Imam Ahmad stated: 'If one prays behind an Imam who recites Qunut in Fajr, follow him and say Ameen to maintain community unity.'"
                )
            )
        )
    )

    fun getTopic(topicId: String): ComparativeFiqhTopic? {
        return TOPICS.firstOrNull { it.topicId == topicId } ?: TOPICS.firstOrNull()
    }
}
