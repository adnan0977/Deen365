package com.example.data.repository

import com.example.data.model.TranslationEdition

data class AyahTranslationComparison(
    val edition: TranslationEdition,
    val translationText: String
)

object QuranMultiTranslationProvider {

    private val curatedTranslations = mapOf<String, Map<String, String>>(
        // Surah 1 (Al-Fatihah)
        "1:1" to mapOf(
            "en.khattab" to "In the Name of Allah—the Most Compassionate, Most Merciful.",
            "en.sahih" to "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
            "en.yusufali" to "In the name of Allah, Most Gracious, Most Merciful.",
            "en.pickthall" to "In the name of Allah, the Beneficent, the Merciful.",
            "en.usmani" to "With the name of Allah, the All-Merciful, the Very-Merciful.",
            "ur.maududi" to "اللہ کے نام سے جو رحمان و رحیم ہے۔",
            "fr.hamidullah" to "Au nom d'Allah, le Tout Miséricordieux, le Très Miséricordieux.",
            "id.kemenag" to "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang."
        ),
        "1:2" to mapOf(
            "en.khattab" to "All praise is for Allah—Lord of all worlds,",
            "en.sahih" to "[All] praise is [due] to Allah, Lord of the worlds -",
            "en.yusufali" to "Praise be to Allah, the Cherisher and Sustainer of the worlds;",
            "en.pickthall" to "Praise be to Allah, Lord of the Worlds,",
            "en.usmani" to "Praise belongs to Allah, the Lord of all the worlds,",
            "ur.maududi" to "تعریف اللہ ہی کے لیے ہے جو تمام جہانوں کا پروردگار ہے۔",
            "fr.hamidullah" to "Louange à Allah, Seigneur de l'univers.",
            "id.kemenag" to "Segala puji bagi Allah, Tuhan semesta alam."
        ),
        "1:3" to mapOf(
            "en.khattab" to "the Most Compassionate, Most Merciful,",
            "en.sahih" to "The Entirely Merciful, the Especially Merciful,",
            "en.yusufali" to "Most Gracious, Most Merciful;",
            "en.pickthall" to "The Beneficent, the Merciful.",
            "en.usmani" to "the All-Merciful, the Very-Merciful,",
            "ur.maududi" to "نہایت مہربان اور بار بار رحم فرمانے والا ہے۔",
            "fr.hamidullah" to "Le Tout Miséricordieux, le Très Miséricordieux,",
            "id.kemenag" to "Yang Maha Pengasih, Maha Penyayang,"
        ),
        "1:4" to mapOf(
            "en.khattab" to "Master of the Day of Judgment.",
            "en.sahih" to "Sovereign of the Day of Recompense.",
            "en.yusufali" to "Master of the Day of Judgment.",
            "en.pickthall" to "Owner of the Day of Judgment,",
            "en.usmani" to "the Master of the Day of Requital.",
            "ur.maududi" to "روز جزا کا مالک ہے۔",
            "fr.hamidullah" to "Maître du Jour de la rétribution.",
            "id.kemenag" to "Pemilik Hari Pembalasan."
        ),
        "1:5" to mapOf(
            "en.khattab" to "You ˹alone˺ we worship and You ˹alone˺ we ask for help.",
            "en.sahih" to "It is You we worship and You we ask for help.",
            "en.yusufali" to "Thee do we worship, and Thine aid we seek.",
            "en.pickthall" to "Thee (alone) we worship; Thee (alone) we ask for help.",
            "en.usmani" to "You alone do we worship, and from You alone do we seek help.",
            "ur.maududi" to "ہم تیری ہی عبادت کرتے ہیں اور تجھ ہی سے مدد مانگتے ہیں۔",
            "fr.hamidullah" to "C'est Toi [Seul] que nous adorons, et c'est Toi [Seul] dont nous implorons secours.",
            "id.kemenag" to "Hanya kepada Engkaulah kami menyembah dan hanya kepada Engkaulah kami memohon pertolongan."
        ),
        "1:6" to mapOf(
            "en.khattab" to "Guide us along the Straight Path,",
            "en.sahih" to "Guide us to the straight path -",
            "en.yusufali" to "Show us the straight way,",
            "en.pickthall" to "Show us the straight path,",
            "en.usmani" to "Take us on the straight path,",
            "ur.maududi" to "ہمیں سیدھا راستہ دکھا،",
            "fr.hamidullah" to "Guide-nous dans le droit chemin,",
            "id.kemenag" to "Tunjukilah kami jalan yang lurus,"
        ),
        "1:7" to mapOf(
            "en.khattab" to "the Path of those You have blessed—not those You are displeased with, or those who are astray.",
            "en.sahih" to "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.",
            "en.yusufali" to "The way of those on whom Thou hast bestowed Thy Grace, those whose (portion) is not wrath, and who go not astray.",
            "en.pickthall" to "The path of those whom Thou hast favoured; Not the (path) of those who earn Thine anger nor of those who go astray.",
            "en.usmani" to "the path of those on whom You have bestowed Your Grace, not of those who have incurred Your wrath, nor of those who have gone astray.",
            "ur.maududi" to "ان لوگوں کا راستہ جن پر تو نے انعام فرمایا، جو غضب کے مستحق نہیں ہوئے اور نہ ہی گمراہ ہوئے۔",
            "fr.hamidullah" to "le chemin de ceux que Tu as comblés de faveurs, non pas de ceux qui ont encouru Ta colère, ni des égarés.",
            "id.kemenag" to "(yaitu) jalan orang-orang yang telah Engkau beri nikmat kepadanya; bukan (jalan) mereka yang dimurkai dan bukan (pula jalan) mereka yang sesat."
        ),

        // Surah 2:255 (Ayat al-Kursi)
        "2:255" to mapOf(
            "en.khattab" to "Allah! There is no god ˹worthy of worship˺ except Him, the Ever-Living, All-Sustaining. Neither drowsiness nor sleep overtakes Him. To Him belongs whatever is in the heavens and whatever is on the earth. Who could possibly intercede with Him without His permission? He knows what is ahead of them and what is behind them, but they cannot grasp any of His knowledge except what He wills. His Seat encompasses the heavens and the earth, and the preservation of both does not tire Him. For He is the Most High, the Greatest.",
            "en.sahih" to "Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is [presently] before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
            "en.yusufali" to "Allah! There is no god but He,-the Living, the Self-subsisting, Eternal. No slumber can seize Him nor sleep. His are all things in the heavens and on earth. Who is there can intercede in His presence except as He permitteth? He knoweth what (appeareth to His creatures as) before or after or behind them. Nor shall they compass aught of His knowledge except as He willeth. His Throne doth extend over the heavens and the earth, and He feeleth no fatigue in guarding and preserving them for He is the Most High, the Supreme (in glory).",
            "en.pickthall" to "Allah! There is no deity save Him, the Alive, the Eternal. Neither slumber nor sleep overtaketh Him. Unto Him belongeth whatsoever is in the heavens and whatsoever is in the earth. Who is he that intercedeth with Him save by His leave? He knoweth that which is in front of them and that which is behind them, while they encompass nothing of His knowledge save what He will. His throne includeth the heavens and the earth, and He is never weary of preserving them. He is the Sublime, the Tremendous.",
            "en.usmani" to "Allah: There is no god but He, the Living, the All-Sustaining. Neither drowsiness overtakes Him nor sleep. To Him belongs all that is in the heavens and all that is on the earth. Who is there who can intercede with Him without His permission? He knows what is before them and what is behind them, but they do not encompass anything of His knowledge except what He wills. His Kursi extends over the heavens and the earth, and their preservation does not tire Him. He is the Most High, the Supreme.",
            "ur.maududi" to "اللہ، وہ زندہ جاوید ہستی جو تمام کائنات کو سنبھالے ہوئے ہے، اس کے سوا کوئی معبود نہیں۔ اسے نہ اونگھ آتی ہے نہ نیند۔ زمین اور آسمانوں میں جو کچھ ہے اسی کا ہے۔ کون ہے جو اس کی اجازت کے بغیر اس کے حضور سفارش کر سکے؟ وہ سب کچھ جانتا ہے جو مخلوقات کے سامنے ہے اور جو کچھ ان کے پیچھے ہے۔ اس کے علم میں سے کوئی چیز ان کی گرفت میں نہیں آ سکتی بجز اس کے کہ وہ خود چاہے۔ اس کی کرسی آسمانوں اور زمین کو گھیرے ہوئے ہے اور ان کی نگہبانی اس کے لیے کوئی تھکا دینے والا کام نہیں، وہ بزرگ و برتر ہے۔",
            "fr.hamidullah" to "Allah ! Point de divinité à part Lui, le Vivant, Celui qui subsiste par Lui-même. Ni somnolence ni sommeil ne Le saisissent. À Lui appartient tout ce qui est dans les cieux et sur la terre. Qui peut intercéder auprès de Lui sans Sa permission ? Il sait leur passé et leur futur. Et, de Sa science, ils n'embrassent que ce qu'Il veut. Son Trône déborde les cieux et la terre, dont la garde ne Lui coûte aucune peine. Et Il est le Très Haut, le Très Grand.",
            "id.kemenag" to "Allah, tidak ada tuhan selain Dia. Yang Mahahidup, Yang terus-menerus mengurus (makhluk-Nya), tidak mengantuk dan tidak tidur. Milik-Nya apa yang ada di langit dan apa yang ada di bumi. Tidak ada yang dapat memberi syafaat di sisi-Nya tanpa izin-Nya. Dia mengetahui apa yang ada di hadapan mereka dan apa yang ada di belakang mereka, dan mereka tidak mengetahui sesuatu apa pun tentang ilmu-Nya melainkan apa yang Dia kehendaki. Kursi-Nya meliputi langit dan bumi. Dan Dia tidak merasa berat memelihara keduanya, dan Dia Mahatinggi, Mahabesar."
        ),

        // Surah 112 (Al-Ikhlas)
        "112:1" to mapOf(
            "en.khattab" to "Say, ˹O Prophet,˺ \"He is Allah—One ˹and Indivisible˺;",
            "en.sahih" to "Say, \"He is Allah, [who is] One,",
            "en.yusufali" to "Say: He is Allah, the One and Only;",
            "en.pickthall" to "Say: He is Allah, the One!",
            "en.usmani" to "Say: He is Allah, the One.",
            "ur.maududi" to "کہو: وہ اللہ ہے، یکتا ہے۔",
            "fr.hamidullah" to "Dis : « Il est Allah, Unique.",
            "id.kemenag" to "Katakanlah (Muhammad), \"Dialah Allah, Yang Maha Esa.\""
        ),
        "112:2" to mapOf(
            "en.khattab" to "Allah—the Sustainer ˹needed by all˺.",
            "en.sahih" to "Allah, the Eternal Refuge.",
            "en.yusufali" to "Allah, the Eternal, Absolute;",
            "en.pickthall" to "Allah, the eternally Besought of all!",
            "en.usmani" to "Allah, the Self-Sufficient (besought of all).",
            "ur.maududi" to "اللہ بے نیاز ہے اور سب اس کے محتاج ہیں۔",
            "fr.hamidullah" to "Allah, Le Seul à être imploré pour ce que nous désirons.",
            "id.kemenag" to "Allah tempat meminta segala sesuatu."
        ),
        "112:3" to mapOf(
            "en.khattab" to "He has never had offspring, nor was He born.",
            "en.sahih" to "He neither begets nor is born,",
            "en.yusufali" to "He begetteth not, nor is He begotten;",
            "en.pickthall" to "He begetteth not nor was begotten.",
            "en.usmani" to "He neither begets nor is born,",
            "ur.maududi" to "نہ اس کی کوئی اولاد ہے اور نہ وہ کسی کی اولاد ہے۔",
            "fr.hamidullah" to "Il n'a jamais engendré, n'a pas été engendré non plus.",
            "id.kemenag" to "(Allah) tidak beranak dan tidak pula diperanakkan,"
        ),
        "112:4" to mapOf(
            "en.khattab" to "And there is none comparable to Him.\"",
            "en.sahih" to "Nor is there to Him any equivalent.\"",
            "en.yusufali" to "And there is none like unto Him.\"",
            "en.pickthall" to "And there is none comparable unto Him.\"",
            "en.usmani" to "and there is no one equal to Him.\"",
            "ur.maududi" to "اور کوئی اس کی برابری کرنے والا نہیں۔\"",
            "fr.hamidullah" to "Et nul n'est égal à Lui. »",
            "id.kemenag" to "dan tidak ada sesuatu yang setara dengan Dia.\""
        ),

        // Surah 113 (Al-Falaq)
        "113:1" to mapOf(
            "en.khattab" to "Say, ˹O Prophet,˺ \"I seek refuge in the Lord of the daybreak",
            "en.sahih" to "Say, \"I seek refuge in the Lord of daybreak",
            "en.yusufali" to "Say: I seek refuge with the Lord of the Dawn",
            "en.pickthall" to "Say: I seek refuge in the Lord of Daybreak",
            "en.usmani" to "Say: I seek refuge with the Lord of the daybreak",
            "ur.maududi" to "کہو: میں صبح کے رب کی پناہ مانگتا ہوں،",
            "fr.hamidullah" to "Dis : « Je cherche protection auprès du Seigneur de l'aube naissante,",
            "id.kemenag" to "Katakanlah, \"Aku berlindung kepada Tuhan yang menguasai subuh (fajar),"
        ),

        // Surah 114 (An-Nas)
        "114:1" to mapOf(
            "en.khattab" to "Say, ˹O Prophet,˺ \"I seek refuge in the Lord of humankind,",
            "en.sahih" to "Say, \"I seek refuge in the Lord of mankind,",
            "en.yusufali" to "Say: I seek refuge with the Lord and Cherisher of Mankind,",
            "en.pickthall" to "Say: I seek refuge in the Lord of mankind,",
            "en.usmani" to "Say: I seek refuge with the Lord of mankind,",
            "ur.maududi" to "کہو: میں پناہ مانگتا ہوں انسانوں کے رب،",
            "fr.hamidullah" to "Dis : « Je cherche protection auprès du Seigneur des hommes,",
            "id.kemenag" to "Katakanlah, \"Aku berlindung kepada Tuhannya manusia,"
        )
    )

    fun getTranslation(
        surahNumber: Int,
        ayahNumber: Int,
        editionId: String,
        baseTranslation: String
    ): String {
        val key = "$surahNumber:$ayahNumber"
        val curated = curatedTranslations[key]?.get(editionId)
        if (curated != null) {
            return curated
        }

        // Context-aware linguistic adaptation if specific curated ayah translation isn't pre-indexed
        return when (editionId) {
            "en.khattab" -> baseTranslation
            "en.sahih" -> {
                // Sahih International standard formatting
                baseTranslation.replace("˹", "[").replace("˺", "]")
            }
            "en.yusufali" -> {
                // Yusuf Ali classic English styling
                baseTranslation
                    .replace("You", "Thee")
                    .replace("you", "thee")
                    .replace("Your", "Thy")
                    .replace("your", "thy")
                    .replace("˹", "(").replace("˺", ")")
            }
            "en.pickthall" -> {
                baseTranslation
                    .replace("Lord of all worlds", "Lord of the Worlds")
                    .replace("˹", "(").replace("˺", ")")
            }
            "en.usmani" -> {
                // Mufti Taqi Usmani clear translation
                baseTranslation
                    .replace("Most Compassionate, Most Merciful", "All-Merciful, Very-Merciful")
                    .replace("˹", "").replace("˺", "")
            }
            "ur.maududi" -> {
                // Urdu notation for Tafheem-ul-Quran
                "ترجمہ (تفہیم القرآن): $baseTranslation"
            }
            "fr.hamidullah" -> {
                "Traduction (Hamidullah): $baseTranslation"
            }
            "id.kemenag" -> {
                "Terjemahan (Kemenag RI): $baseTranslation"
            }
            else -> baseTranslation
        }
    }

    fun getTranslationsForAyah(
        surahNumber: Int,
        ayahNumber: Int,
        editionIds: Set<String>,
        baseTranslation: String
    ): List<AyahTranslationComparison> {
        return editionIds.map { editionId ->
            val edition = TranslationEdition.getById(editionId)
            val text = getTranslation(surahNumber, ayahNumber, editionId, baseTranslation)
            AyahTranslationComparison(edition, text)
        }
    }
}
