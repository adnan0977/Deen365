package com.example.data.seed

import com.example.data.model.AsbabAlNuzulItem

object AsbabAlNuzulSeedData {

    val REVELATION_CONTEXTS: List<AsbabAlNuzulItem> = listOf(
        AsbabAlNuzulItem(
            id = "fatihah_opener",
            surahNumber = 1,
            surahName = "Al-Fatihah",
            ayahRange = "1:1 - 1:7",
            title = "The Opening of the Book and the Healing Remedy",
            occasionSummary = "Revealed in Makkah during the earliest dawn of Prophethood as the Mother of the Book (Umm al-Kitab).",
            historicalIncident = "Ali ibn Abi Talib and Abu Maysarah reported that when the Messenger of Allah ﷺ went out, he heard a voice calling 'O Muhammad!' He stopped in awe, and Angel Jibril descended saying: 'Say: Bismillahi ar-Rahmani ar-Rahim, Al-Hamdu Lillahi Rabbil 'Alamin...' revealing the entire foundational Surah as the indispensable soul of every unit of prayer.",
            classicalSource = "Asbab al-Nuzul by Imam Ali ibn Ahmad al-Wahidi",
            spiritualTakeaway = "No prayer is valid without Al-Fatihah; it is a sacred intimate dialogue between the Creator and the servant."
        ),
        AsbabAlNuzulItem(
            id = "baqarah_kursi",
            surahNumber = 2,
            surahName = "Al-Baqarah",
            ayahRange = "2:255",
            title = "Ayat al-Kursi (The Throne Verse) - Supreme Sovereignty",
            occasionSummary = "Revealed at night accompanied by thousands of angels descending in awe of the majesty of the Divine Names.",
            historicalIncident = "Ubayy ibn Ka'b (RA) narrated that the Messenger of Allah ﷺ asked him: 'O Abu al-Mundhir! Do you know which verse from the Book of Allah in your possession is the greatest?' Ubayy said: 'Allah and His Messenger know best.' The Prophet repeated the question, and Ubayy answered: 'Allahu la ilaha illa Huwa, al-Hayyu al-Qayyum...' The Prophet ﷺ struck his chest affectionately and said: 'May knowledge be sweet for you, O Abu al-Mundhir!'",
            classicalSource = "Lubab an-Nuqul fi Asbab an-Nuzul by Imam Jalal al-Din as-Suyuti",
            spiritualTakeaway = "Whoever recites Ayat al-Kursi after every obligatory prayer, nothing stands between him and entering Paradise except death."
        ),
        AsbabAlNuzulItem(
            id = "ikhlas_origin",
            surahNumber = 112,
            surahName = "Al-Ikhlas",
            ayahRange = "112:1 - 112:4",
            title = "The Lineage of the Lord (Sifat ar-Rahman)",
            occasionSummary = "Revealed when the polytheists of Makkah challenged the Prophet ﷺ demanding the genealogy of his God.",
            historicalIncident = "Ubayy ibn Ka'b and Ibn Abbas reported that the Quraysh polytheists came to the Prophet ﷺ and demanded: 'O Muhammad, describe to us the lineage and substance of your Lord! Is He made of gold, silver, copper, or ruby?' Allah then sent down this concise crystal declaration: 'Say: He is Allah, the One. Allah, the Eternal Refuge. He neither begets nor is born, nor is there to Him any equivalent.'",
            classicalSource = "Musnad Ahmad, Jami` at-Tirmidhi, and Al-Wahidi",
            spiritualTakeaway = "Equivalent to one-third of the Quran in theology, establishing uncompromising pure monotheism (Tawhid)."
        )
    )

    fun getForSurah(surahNumber: Int): AsbabAlNuzulItem? {
        return REVELATION_CONTEXTS.firstOrNull { it.surahNumber == surahNumber }
    }
}
