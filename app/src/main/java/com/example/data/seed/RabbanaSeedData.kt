package com.example.data.seed

data class RabbanaDua(
    val id: Int,
    val surahName: String,
    val ayahCoordinate: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val theme: String,
    val spiritualBenefit: String
)

object RabbanaSeedData {
    val ALL_DUAS: List<RabbanaDua> = listOf(
        RabbanaDua(
            id = 1,
            surahName = "Al-Baqarah",
            ayahCoordinate = "2:127",
            arabicText = "رَبَّنَا تَقَبَّلْ مِنَّا ۖ إِنَّكَ أَنتَ السَّمِيعُ الْعَلِيمُ",
            transliteration = "Rabbana taqabbal minna, innaka Antas-Sami'ul-'Aleem",
            translation = "Our Lord, accept [this] from us. Indeed You are the Hearing, the Knowing.",
            theme = "Acceptance of Deeds",
            spiritualBenefit = "Supplicated by Ibrahim and Ismail while raising the foundations of the Kaaba; ideal after completing any act of worship."
        ),
        RabbanaDua(
            id = 2,
            surahName = "Al-Baqarah",
            ayahCoordinate = "2:128",
            arabicText = "رَبَّنَا وَاجْعَلْنَا مُسْلِمَيْنِ لَكَ وَمِن ذُرِّيَّتِنَا أُمَّةً مُّسْلِمَةً لَّكَ وَأَرِنَا مَنَاسِكَنَا وَتُبْ عَلَيْنَا ۖ إِنَّكَ أَنتَ التَّوَّابُ الرَّحِيمُ",
            transliteration = "Rabbana waj'alna muslimayni laka wa min dhurriyyatina ummatan muslimatan laka wa arina manasikana wa tub 'alayna, innaka Antat-Tawwabur-Raheem",
            translation = "Our Lord, and make us Muslims [in submission] to You and from our descendants a Muslim nation [in submission] to You. And show us our rites and accept our repentance. Indeed, You are the Accepting of repentance, the Merciful.",
            theme = "Righteous Progeny & Submission",
            spiritualBenefit = "Protects family lineage upon Islam and preserves children in steadfast submission."
        ),
        RabbanaDua(
            id = 3,
            surahName = "Al-Baqarah",
            ayahCoordinate = "2:201",
            arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            transliteration = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar",
            translation = "Our Lord, give us in this world [that which is] good and in the Hereafter [that which is] good and protect us from the punishment of the Fire.",
            theme = "Comprehensive Good of Both Worlds",
            spiritualBenefit = "The most frequent du'a of Prophet Muhammad (ﷺ), recited around the Kaaba between Rukn Yamani and the Black Stone."
        ),
        RabbanaDua(
            id = 4,
            surahName = "Al-Baqarah",
            ayahCoordinate = "2:250",
            arabicText = "رَبَّنَا أَفْرِغْ عَلَيْنَا صَبْرًا وَثَبِّتْ أَقْدَامَنَا وَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
            transliteration = "Rabbana afrigh 'alayna sabran wa thabbit aqdamana wansurna 'alal-qawmil-kafireen",
            translation = "Our Lord, pour upon us patience and plant firmly our feet and give us victory over the disbelieving people.",
            theme = "Steadfastness in Adversity",
            spiritualBenefit = "Supplication of Talut's righteous believers when facing Goliath; grants mental courage under immense pressure."
        ),
        RabbanaDua(
            id = 5,
            surahName = "Al-Baqarah",
            ayahCoordinate = "2:286",
            arabicText = "رَبَّنَا لَا تُؤَاخِذْنَا إِن نَّسِينَا أَوْ أَخْطَأْنَا ۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِن قَبْلِنَا ۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ ۖ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا ۚ أَنتَ مَوْلَانَا فَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
            transliteration = "Rabbana la tu'akhidhna in naseena aw akhta'na, Rabbana wa la tahmil 'alayna isran kama hamaltahu 'alal-ladheena min qablina, Rabbana wa la tuhammilna ma la taqata lana bih, wa'fu 'anna waghfir lana warhamna, Anta mawlana fansurna 'alal-qawmil-kafireen",
            translation = "Our Lord, do not impose blame upon us if we have forgotten or erred. Our Lord, and lay not upon us a burden like that which You laid upon those before us. Our Lord, and burden us not with that which we have no ability to bear. And pardon us; and forgive us; and have mercy upon us. You are our protector, so give us victory over the disbelieving people.",
            theme = "Ultimate Divine Relief & Pardon",
            spiritualBenefit = "Concluding verse of Surah Al-Baqarah; recitation at night protects from all harm and evil."
        ),
        RabbanaDua(
            id = 6,
            surahName = "Ali 'Imran",
            ayahCoordinate = "3:8",
            arabicText = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ",
            transliteration = "Rabbana la tuzigh quloobana ba'da idh hadaytana wa hab lana mil-ladunka rahmah, innaka Antal-Wahhab",
            translation = "Our Lord, let not our hearts deviate after You have guided us and grant us from Yourself mercy. Indeed, You are the Bestower.",
            theme = "Protection of Faith & Guidance",
            spiritualBenefit = "Protects the heart from spiritual doubts, moral confusion, and apostasy after receiving guidance."
        ),
        RabbanaDua(
            id = 7,
            surahName = "Ali 'Imran",
            ayahCoordinate = "3:16",
            arabicText = "رَبَّنَا إِنَّنَا آمَنَّا فَاغْفِرْ لَنَا ذُنُوبَنَا وَقِنَا عَذَابَ النَّارِ",
            transliteration = "Rabbana innana amanna faghfir lana dhunoobana wa qina 'adhaban-nar",
            translation = "Our Lord, indeed we have believed, so forgive us our sins and protect us from the punishment of the Fire.",
            theme = "Intercession Through Faith",
            spiritualBenefit = "Invoking one's sincere faith as a means of obtaining divine pardon for hidden missteps."
        ),
        RabbanaDua(
            id = 8,
            surahName = "Ali 'Imran",
            ayahCoordinate = "3:191-192",
            arabicText = "رَبَّنَا مَا خَلَقْتَ هَٰذَا بَاطِلًا سُبْحَانَكَ فَقِنَا عَذَابَ النَّارِ ۝ رَبَّنَا إِنَّكَ مَن تُدْخِلِ النَّارَ فَقَدْ أَخْزَيْتَهُ ۖ وَمَا لِلظَّالِمِينَ مِنْ أَنصَارٍ",
            transliteration = "Rabbana ma khalaqta hadha batila, subhanaka faqina 'adhaban-nar. Rabbana innaka man tudkhilin-nara faqad akhzaytahu wa ma liz-zalimeena min ansar",
            translation = "Our Lord, You did not create this aimlessly; exalted are You [above such a thing]; then protect us from the punishment of the Fire. Our Lord, indeed whoever You admit to the Fire - You have disgraced him, and for the wrongdoers there are no helpers.",
            theme = "Contemplation of the Cosmos",
            spiritualBenefit = "Recited by those of deep intellect (Ulul-Albab) when contemplating the heavens and the earth."
        ),
        RabbanaDua(
            id = 9,
            surahName = "Al-A'raf",
            ayahCoordinate = "7:23",
            arabicText = "رَبَّنَا ظَلَمْنَا أَنفُسَنَا وَإِن لَّمْ تَغْفِرْ لَنَا وَتَرْحَمْنَا لَنَكُونَنَّ مِنَ الْخَاسِرِينَ",
            transliteration = "Rabbana zalamna anfusana wa in lam taghfir lana wa tarhamna lanakoonanna minal-khasireen",
            translation = "Our Lord, we have wronged ourselves, and if You do not forgive us and have mercy upon us, we will surely be among the losers.",
            theme = "The Repentance of Adam and Hawwa",
            spiritualBenefit = "The first supplication of human repentance taught by Allah to Adam; dissolves crushing guilt."
        ),
        RabbanaDua(
            id = 10,
            surahName = "Al-Furqan",
            ayahCoordinate = "25:74",
            arabicText = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
            transliteration = "Rabbana hab lana min azwajina wa dhurriyyatina qurrata a'yunin waj'alna lil-muttaqeena imama",
            translation = "Our Lord, grant us from among our wives and offspring comfort to our eyes and make us an example for the righteous.",
            theme = "Loving Family & Righteous Leadership",
            spiritualBenefit = "Supplication of the Servants of the Most Merciful ('Ibadur-Rahman) for harmonious marital peace and noble progeny."
        )
    )
}
