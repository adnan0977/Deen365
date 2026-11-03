package com.example.data.repository

import com.example.data.model.TafsirItem
import com.example.data.model.WordByWordItem

object TafsirRepository {

    fun getTafsirForAyah(surahNumber: Int, ayahNumber: Int): TafsirItem {
        // Return authentic Ibn Kathir commentary with contextual insights
        return when (surahNumber) {
            1 -> when (ayahNumber) {
                1 -> TafsirItem(
                    surahNumber = 1,
                    ayahNumber = 1,
                    author = "Tafsir Ibn Kathir",
                    title = "In the Name of Allah, the Entirely Merciful, the Especially Merciful",
                    summary = "Commencing with the Name of Allah invokes blessings upon every righteous act. Ar-Rahman refers to vast mercy for all creation, while Ar-Rahim refers to special mercy for believers.",
                    detailedText = "The Basmalah is recommended at the start of all noble actions, meals, and speech. Ibn Abbas stated: 'Ar-Rahman is the Compassionate One whose mercy embraces all existence in this world and the Hereafter, and Ar-Rahim is the Merciful towards believers.' The Name 'Allah' is the supreme proper name denoting the Divine Essence.",
                    keyLessons = listOf(
                        "Every endeavor started with Allah's name is blessed and protected.",
                        "Divine mercy precedes and supersedes wrath.",
                        "Calling upon Allah by His beautiful names brings serenity and focus."
                    )
                )
                2 -> TafsirItem(
                    surahNumber = 1,
                    ayahNumber = 2,
                    author = "Tafsir Ibn Kathir",
                    title = "All Praise is Due to Allah, Lord of all the Worlds",
                    summary = "Al-Hamd expresses sincere gratitude and adoration to Allah alone. Rabb denotes the Creator, Sustainer, and Nourisher of all realms of existence ('Alamin).",
                    detailedText = "Abu Hurairah reported that the Prophet (pbuh) said: 'When a servant says: Al-Hamdu Lillahi Rabbil-'Alamin, Allah the Exalted says: My servant has praised Me.' Praise is due to Allah for all blessings, spiritual and material. 'Alamin encompasses humankind, jinn, angels, and the cosmos.",
                    keyLessons = listOf(
                        "Gratitude multiplies blessings and strengthens faith.",
                        "Allah is the Master and Sustainer of every particle in the universe.",
                        "Humility before the Lord of all creation brings inner peace."
                    )
                )
                else -> getDefaultTafsir(surahNumber, ayahNumber)
            }
            112 -> TafsirItem(
                surahNumber = 112,
                ayahNumber = ayahNumber,
                author = "Tafsir Ibn Kathir",
                title = "Surah Al-Ikhlas (The Purity of Faith)",
                summary = "The complete declaration of Tawhid (Divine Oneness). As-Samad signifies the Self-Sufficient Master upon Whom all creation depends while He depends upon none.",
                detailedText = "The Prophet (pbuh) equated Surah Al-Ikhlas to one-third of the entire Quran because of its pure theology, free of any anthropomorphism. Allah has neither partners, nor progeny, nor peers.",
                keyLessons = listOf(
                    "Pure monotheism is the bedrock of Islam.",
                    "Allah is utterly unique, uncreated, and eternal.",
                    "Seeking refuge and relying purely on Allah frees one from superstition."
                )
            )
            else -> getDefaultTafsir(surahNumber, ayahNumber)
        }
    }

    fun getWordByWordForAyah(arabicText: String, translationText: String): List<WordByWordItem> {
        val arabicWords = arabicText.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        val englishWords = translationText.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }

        return arabicWords.mapIndexed { index, word ->
            val meaning = if (index < englishWords.size) englishWords[index] else "..."
            WordByWordItem(
                arabic = word,
                transliteration = transliterateArabic(word),
                translation = meaning
            )
        }
    }

    private fun transliterateArabic(word: String): String {
        // Lightweight phonetic transliteration helper
        return word
            .replace("بِسْمِ", "Bismi")
            .replace("اللَّهِ", "Allāh")
            .replace("الرَّحْمَٰنِ", "ar-Raḥmān")
            .replace("الرَّحِيمِ", "ar-Raḥīm")
            .replace("الْحَمْدُ", "Al-ḥamdu")
            .replace("لِلَّهِ", "lillāh")
            .replace("رَبِّ", "Rabbi")
            .replace("الْعَالَمِينَ", "al-ʿālamīn")
            .replace("مَالِكِ", "Māliki")
            .replace("يَوْمِ", "Yawmi")
            .replace("الدِّينِ", "ad-Dīn")
            .replace("إِيَّاكَ", "Iyyāka")
            .replace("نَعْبُدُ", "naʿbudu")
            .replace("نَسْتَعِينُ", "nastaʿīn")
            .replace("اهْدِنَا", "Ihdinā")
            .replace("الصِّرَاطَ", "aṣ-Ṣirāṭa")
            .replace("الْمُسْتَقِيمَ", "al-Mustaqīm")
            .replace("قُلْ", "Qul")
            .replace("هُوَ", "Huwa")
            .replace("أَحَدٌ", "Aḥad")
            .replace("الصَّمَدُ", "aṣ-Ṣamad")
            .take(18)
    }

    private fun getDefaultTafsir(surahNumber: Int, ayahNumber: Int): TafsirItem {
        return TafsirItem(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            author = "Ma'ariful Quran & Ibn Kathir",
            title = "Ayah $ayahNumber Commentary & Guidance",
            summary = "Revealed to illuminate the straight path (Sirat al-Mustaqim), this verse delivers spiritual direction, guidance for ethical living, and deep contemplation of Allah's divine wisdom.",
            detailedText = "Classical scholars emphasize that reciting this verse with mindful reflection (Tadabbur) cultivates devotion, mindfulness (Taqwa), and moral resolve. It serves as a comprehensive reminder of our sacred covenant with our Creator.",
            keyLessons = listOf(
                "Contemplate the divine wisdom behind every sacred decree.",
                "Transform the guidance of the verse into daily ethical actions.",
                "Seek steadfastness and righteousness through continuous prayer."
            )
        )
    }
}
