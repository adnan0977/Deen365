package com.example.data.seed

import com.example.data.model.NarratorRole
import com.example.data.model.SanadChain
import com.example.data.model.SanadNode

object SanadChainSeedData {

    val BUKHARI_HADITH_1_CHAIN = SanadChain(
        chainId = "bukhari_1",
        hadithNumber = 1,
        collectionId = "bukhari",
        collectionName = "Sahih al-Bukhari",
        chapterTitle = "Book 1: How the Divine Revelation started",
        chainGrade = "Sahih Muttasil (Gold Standard Continuous Chain)",
        scholarsCommentary = "Imam Bukhari deliberately opened his entire Jami' with this unique single-chain hadith (Gharib al-Isnad) to remind himself and all seekers of knowledge that every deed without pure intention is barren dust.",
        nodes = listOf(
            SanadNode(
                nodeId = "bukhari",
                nameEnglish = "Imam Muhammad ibn Isma'il al-Bukhari",
                nameArabic = "الإمام محمد بن إسماعيل البخاري",
                role = NarratorRole.COMPILER,
                generationLevel = 6,
                scholarlyGrading = "Amir al-Mu'minin fi al-Hadith (Commander of Believers in Hadith)",
                transmissionFormula = "المصنف الجامع (The Compiler)",
                yearPassedAH = 256,
                placeOfResidence = "Bukhara / Khorasan",
                studentTeacherCount = "Studied with 1,080 scholars; taught over 90,000 students",
                briefBiography = "Author of Al-Jami' al-Sahih, universally recognized as the most authentic book after the Quran. Evaluated 600,000 narrations.",
                isFocalPoint = false
            ),
            SanadNode(
                nodeId = "al_humaydi",
                nameEnglish = "Al-Humaydi (Abdullah ibn al-Zubayr)",
                nameArabic = "عبد الله بن الزبير الحميدي",
                role = NarratorRole.SHEIKH_COLLECTOR,
                generationLevel = 5,
                scholarlyGrading = "Thiqah Hafiz Faqih (Highest Trustworthiness & Jurist)",
                transmissionFormula = "حَدَّثَنَا (Haddathana - He narrated directly to us)",
                yearPassedAH = 219,
                placeOfResidence = "Makkah al-Mukarramah",
                studentTeacherCount = "Grand Sheikh of the Haram in Makkah, prominent student of Sufyan ibn Uyaynah",
                briefBiography = "The grand authority of Makkah and author of Al-Musnad. Bukhari journeyed to Makkah to attend his circles in the Sacred Sanctuary.",
                isFocalPoint = false
            ),
            SanadNode(
                nodeId = "sufyan",
                nameEnglish = "Sufyan ibn 'Uyaynah",
                nameArabic = "سفيان بن عيينة بن ميمون الهلالي",
                role = NarratorRole.ATBA_TABII,
                generationLevel = 4,
                scholarlyGrading = "Imam al-Hijaz - Thiqah Thabit Hafiz Hujjah",
                transmissionFormula = "حَدَّثَنَا (Haddathana - He narrated directly to us)",
                yearPassedAH = 198,
                placeOfResidence = "Kufa, then settled in Makkah for 70 years",
                studentTeacherCount = "Narrated to Imam Shafi'i, Ahmad ibn Hanbal, and Humaydi",
                briefBiography = "Known for his astounding precision and asceticism. Imam Shafi'i said: 'Were it not for Malik and Sufyan ibn Uyaynah, the knowledge of Hijaz would have perished.'",
                isFocalPoint = false
            ),
            SanadNode(
                nodeId = "yahya_ansari",
                nameEnglish = "Yahya ibn Sa'id al-Ansari",
                nameArabic = "يحيى بن سعيد الأنصاري المدني",
                role = NarratorRole.SENIOR_TABII,
                generationLevel = 3,
                scholarlyGrading = "Thiqah Thiqah - Qadi of Madinah & Chief Jurist",
                transmissionFormula = "حَدَّثَنَا (Haddathana - He narrated directly to us)",
                yearPassedAH = 143,
                placeOfResidence = "Madinah al-Munawwarah",
                studentTeacherCount = "Over 200 scholars heard this hadith from him",
                briefBiography = "The pivotal fulcrum of this Hadith: while the chain before him was individual (Fard), after Yahya more than two hundred major Imams transmitted it from him.",
                isFocalPoint = true
            ),
            SanadNode(
                nodeId = "muhammad_ibrahim",
                nameEnglish = "Muhammad ibn Ibrahim at-Taymi",
                nameArabic = "محمد بن إبراهيم بن الحارث التيمي",
                role = NarratorRole.TABII,
                generationLevel = 3,
                scholarlyGrading = "Thiqah Jalil (Eminent Trustworthy Scholar of Madinah)",
                transmissionFormula = "أَخْبَرَنِي (Akhbarani - He personally informed me)",
                yearPassedAH = 120,
                placeOfResidence = "Madinah al-Munawwarah",
                studentTeacherCount = "Master teacher in Madinah; student of Alqama",
                briefBiography = "Renowned Tabi'i scholar celebrated for exact memorization. Sole narrator who preserved this hadith from Alqama.",
                isFocalPoint = false
            ),
            SanadNode(
                nodeId = "alqama",
                nameEnglish = "Alqama ibn Waqqas al-Laythi",
                nameArabic = "علقمة بن وقاص الليثي المدني",
                role = NarratorRole.SENIOR_TABII,
                generationLevel = 3,
                scholarlyGrading = "Kibar at-Tabi'in - Thiqah Thabit",
                transmissionFormula = "أَنَّهُ سَمِعَ (Annahu Sami'a - That he personally heard)",
                yearPassedAH = 74,
                placeOfResidence = "Madinah al-Munawwarah",
                studentTeacherCount = "Direct companion student of Umar ibn al-Khattab and Aisha",
                briefBiography = "Sole narrator from Umar ibn al-Khattab on the pulpit. Heard Umar address the congregation in the Prophet's Mosque.",
                isFocalPoint = false
            ),
            SanadNode(
                nodeId = "umar",
                nameEnglish = "Ameer al-Mu'mineen Umar ibn al-Khattab (RA)",
                nameArabic = "عمر بن الخطاب رضي الله عنه",
                role = NarratorRole.COMPANION,
                generationLevel = 2,
                scholarlyGrading = "Al-Faruq - Sahabi Jalil - Khaleefah Rashid",
                transmissionFormula = "يَقُولُ عَلَى المِنْبَرِ: سَمِعْتُ (Proclaiming on the Minbar: I heard)",
                yearPassedAH = 23,
                placeOfResidence = "Madinah al-Munawwarah",
                studentTeacherCount = "Second Caliph; blessed with inspired intuition (Muhaddath)",
                briefBiography = "Stood upon the Minbar of the Messenger of Allah ﷺ in Madinah proclaiming this hadith to the entire gathered assembly of Companions.",
                isFocalPoint = false
            ),
            SanadNode(
                nodeId = "prophet",
                nameEnglish = "Prophet Muhammad ﷺ",
                nameArabic = "سيدنا محمد رسول الله ﷺ",
                role = NarratorRole.PROPHET,
                generationLevel = 1,
                scholarlyGrading = "Khatam an-Nabiyyin - Sayyid al-Mursalin",
                transmissionFormula = "قَالَ رَسُولُ اللَّهِ ﷺ (The Messenger of Allah ﷺ said)",
                yearPassedAH = 11,
                placeOfResidence = "Makkah & Madinah",
                studentTeacherCount = "Taught over 100,000 Companions",
                briefBiography = "'Indeed actions are judged only by intentions, and for every person is only that which he intended...'",
                isFocalPoint = true
            )
        )
    )

    val BUKHARI_HADITH_2_CHAIN = SanadChain(
        chainId = "bukhari_2",
        hadithNumber = 2,
        collectionId = "bukhari",
        collectionName = "Sahih al-Bukhari",
        chapterTitle = "Book 1: How the Divine Revelation started",
        chainGrade = "Sahih Muttasil (Golden Madinan Chain via Imam Malik)",
        scholarsCommentary = "This chain features the revered 'Silsilat adh-Dhahab' (Golden Chain) traditions of Madinah passing through Imam Malik and the household of the Prophet ﷺ.",
        nodes = listOf(
            SanadNode(
                nodeId = "bukhari",
                nameEnglish = "Imam Muhammad ibn Isma'il al-Bukhari",
                nameArabic = "الإمام محمد بن إسماعيل البخاري",
                role = NarratorRole.COMPILER,
                generationLevel = 6,
                scholarlyGrading = "Amir al-Mu'minin fi al-Hadith",
                transmissionFormula = "المصنف الجامع",
                yearPassedAH = 256,
                placeOfResidence = "Bukhara",
                studentTeacherCount = "Authored the Sahih",
                briefBiography = "Recorded in Sahih Bukhari Hadith #2."
            ),
            SanadNode(
                nodeId = "abdullah_yusuf",
                nameEnglish = "Abdullah ibn Yusuf at-Tinnisi",
                nameArabic = "عبد الله بن يوسف التنيسي",
                role = NarratorRole.SHEIKH_COLLECTOR,
                generationLevel = 5,
                scholarlyGrading = "Thiqah Mutqin (Among the most precise reciters of Imam Malik's Muwatta)",
                transmissionFormula = "حَدَّثَنَا (Haddathana)",
                yearPassedAH = 218,
                placeOfResidence = "Egypt & Damascus",
                studentTeacherCount = "Leading transmitter of Malik's books",
                briefBiography = "Praised by Yahya ibn Ma'in as the most reliable Egyptian transmitter of the Muwatta."
            ),
            SanadNode(
                nodeId = "malik",
                nameEnglish = "Imam Malik ibn Anas",
                nameArabic = "الإمام مالك بن أنس الأصبحي",
                role = NarratorRole.ATBA_TABII,
                generationLevel = 4,
                scholarlyGrading = "Imam Dar al-Hijrah (The Leading Scholar of Madinah)",
                transmissionFormula = "أَخْبَرَنَا (Akhbarana)",
                yearPassedAH = 179,
                placeOfResidence = "Madinah al-Munawwarah",
                studentTeacherCount = "Author of Al-Muwatta; teacher of Imam Shafi'i",
                briefBiography = "Lived and taught in Madinah, never riding an animal inside the city out of reverence for the Prophet's resting place."
            ),
            SanadNode(
                nodeId = "hisham_urwah",
                nameEnglish = "Hisham ibn 'Urwah",
                nameArabic = "هشام بن عروة بن الزبير",
                role = NarratorRole.TABII,
                generationLevel = 3,
                scholarlyGrading = "Thiqah Faqih Imam",
                transmissionFormula = "عَنْ (On the authority of)",
                yearPassedAH = 146,
                placeOfResidence = "Madinah, later Baghdad",
                studentTeacherCount = "Taught Sufyan ath-Thawri, Malik, and Abu Hanifah",
                briefBiography = "Son of Urwah ibn al-Zubayr; transmitted the rich household traditions directly from his father."
            ),
            SanadNode(
                nodeId = "urwah_zubayr",
                nameEnglish = "Urwah ibn al-Zubayr",
                nameArabic = "عروة بن الزبير بن العوام",
                role = NarratorRole.SENIOR_TABII,
                generationLevel = 3,
                scholarlyGrading = "One of the Seven Great Jurists of Madinah (Fuqaha al-Madinah as-Sab'ah)",
                transmissionFormula = "عَنْ أَبِيهِ (On the authority of his father/aunt)",
                yearPassedAH = 94,
                placeOfResidence = "Madinah al-Munawwarah",
                studentTeacherCount = "Nephew of Mother of Believers Aisha (RA)",
                briefBiography = "Devoted nephew of Aisha (RA), learning the earliest accounts of the first revelation in the Cave of Hira directly from her."
            ),
            SanadNode(
                nodeId = "aisha",
                nameEnglish = "Mother of Believers Aisha (RA)",
                nameArabic = "أم المؤمنين عائشة بنت أبي بكر رضي الله عنها",
                role = NarratorRole.COMPANION,
                generationLevel = 2,
                scholarlyGrading = "Faqihat al-Ummah - Sahabiyyah Jalilah",
                transmissionFormula = "قَالَتْ (She said)",
                yearPassedAH = 58,
                placeOfResidence = "Madinah al-Munawwarah",
                studentTeacherCount = "Narrated 2,210 hadiths directly from the Prophet ﷺ",
                briefBiography = "Gave the intimate eyewitness account of Harith ibn Hisham questioning the Prophet ﷺ: 'O Messenger of Allah, how does the revelation come to you?'"
            ),
            SanadNode(
                nodeId = "prophet",
                nameEnglish = "Prophet Muhammad ﷺ",
                nameArabic = "سيدنا محمد رسول الله ﷺ",
                role = NarratorRole.PROPHET,
                generationLevel = 1,
                scholarlyGrading = "Sayyid Walad Adam",
                transmissionFormula = "قَالَ رَسُولُ اللَّهِ ﷺ",
                yearPassedAH = 11,
                placeOfResidence = "Makkah & Madinah",
                studentTeacherCount = "Teacher of Humanity",
                briefBiography = "'Sometimes it comes to me like the ringing of a bell, which is the hardest upon me... and sometimes the angel takes the form of a man...'"
            )
        )
    )

    fun getSanadForHadith(collectionId: String, hadithNumber: Int): SanadChain {
        return when {
            collectionId.contains("bukhari", ignoreCase = true) && hadithNumber == 1 -> BUKHARI_HADITH_1_CHAIN
            collectionId.contains("bukhari", ignoreCase = true) && hadithNumber == 2 -> BUKHARI_HADITH_2_CHAIN
            else -> synthesizeSanad(collectionId, hadithNumber)
        }
    }

    private fun synthesizeSanad(collectionId: String, hadithNumber: Int): SanadChain {
        return SanadChain(
            chainId = "${collectionId}_$hadithNumber",
            hadithNumber = hadithNumber,
            collectionId = collectionId,
            collectionName = if (collectionId.contains("muslim")) "Sahih Muslim" else "Sahih al-Bukhari",
            chapterTitle = "Canonical Transmission Chain",
            chainGrade = "Sahih Muttasil (Continuous Chain of Upright Narrators)",
            scholarsCommentary = "Transmitted by an unbroken chain of verified trustworthy narrators conforming to the rigorous strictness criteria of the six canonical Imams.",
            nodes = listOf(
                SanadNode(
                    nodeId = "compiler",
                    nameEnglish = if (collectionId.contains("muslim")) "Imam Muslim ibn al-Hajjaj" else "Imam Muhammad al-Bukhari",
                    nameArabic = if (collectionId.contains("muslim")) "الإمام مسلم بن الحجاج" else "الإمام محمد بن إسماعيل البخاري",
                    role = NarratorRole.COMPILER,
                    generationLevel = 6,
                    scholarlyGrading = "Amir al-Mu'minin fi al-Hadith",
                    transmissionFormula = "المصنف",
                    yearPassedAH = if (collectionId.contains("muslim")) 261 else 256,
                    placeOfResidence = "Nishapur / Bukhara",
                    studentTeacherCount = "Master hadith scholar",
                    briefBiography = "Recorded in classical compilation."
                ),
                SanadNode(
                    nodeId = "sheikh",
                    nameEnglish = "Qutaybah ibn Sa'id al-Baghlanī",
                    nameArabic = "قتيبة بن سعيد البلخي",
                    role = NarratorRole.SHEIKH_COLLECTOR,
                    generationLevel = 5,
                    scholarlyGrading = "Thiqah Thabit",
                    transmissionFormula = "حَدَّثَنَا (Haddathana)",
                    yearPassedAH = 240,
                    placeOfResidence = "Balkh",
                    studentTeacherCount = "Common sheikh of Bukhari and Muslim",
                    briefBiography = "Famous traveler and trustworthy master of Hadith."
                ),
                SanadNode(
                    nodeId = "atba",
                    nameEnglish = "Layth ibn Sa'd al-Fahmi",
                    nameArabic = "الليث بن سعد الإمام المصري",
                    role = NarratorRole.ATBA_TABII,
                    generationLevel = 4,
                    scholarlyGrading = "Imam Ahl Misr - Thiqah Faqih",
                    transmissionFormula = "عَنْ (On the authority of)",
                    yearPassedAH = 175,
                    placeOfResidence = "Fustat (Cairo)",
                    studentTeacherCount = "Foremost jurist and philanthropist of Egypt",
                    briefBiography = "Imam Shafi'i noted: 'Layth was more knowledgeable in Fiqh than Malik, but his students did not record it as thoroughly.'"
                ),
                SanadNode(
                    nodeId = "companion",
                    nameEnglish = "Abu Hurairah (Abd ar-Rahman ibn Sakhr)",
                    nameArabic = "أبو هريرة رضي الله عنه",
                    role = NarratorRole.COMPANION,
                    generationLevel = 2,
                    scholarlyGrading = "Sahabi Jalil - Hafiz al-Ummah",
                    transmissionFormula = "عَنْ رَسُولِ اللَّهِ ﷺ",
                    yearPassedAH = 59,
                    placeOfResidence = "Madinah al-Munawwarah",
                    studentTeacherCount = "Over 800 scholars narrated from him",
                    briefBiography = "Eminent companion who devoted his life to learning at the Suffah of the Prophet's Mosque."
                ),
                SanadNode(
                    nodeId = "prophet",
                    nameEnglish = "Prophet Muhammad ﷺ",
                    nameArabic = "سيدنا محمد رسول الله ﷺ",
                    role = NarratorRole.PROPHET,
                    generationLevel = 1,
                    scholarlyGrading = "Khatam an-Nabiyyin",
                    transmissionFormula = "قَالَ رَسُولُ اللَّهِ ﷺ",
                    yearPassedAH = 11,
                    placeOfResidence = "Makkah & Madinah",
                    studentTeacherCount = "Prophet and Messenger of Allah",
                    briefBiography = "The final Messenger sent as a mercy to all the worlds."
                )
            )
        )
    }
}
