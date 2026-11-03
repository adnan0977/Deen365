package com.example.data.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight

enum class TajweedRuleType(
    val englishName: String,
    val arabicName: String,
    val color: Color,
    val description: String,
    val harakatDuration: String
) {
    MADD(
        englishName = "Madd (Prolongation)",
        arabicName = "المدّ",
        color = Color(0xFFD32F2F), // Red
        description = "Prolongation of vowel sounds (Alif, Waw, Ya) due to Hamzah, Sukun, or natural flow.",
        harakatDuration = "2, 4, 5, or 6 Harakat"
    ),
    GHUNNAH(
        englishName = "Ghunnah (Nasalization)",
        arabicName = "الغُنّة",
        color = Color(0xFFFF9800), // Amber/Orange
        description = "Resonance produced through the nasal cavity on Noon Mushaddadah (نّ) or Meem Mushaddadah (مّ).",
        harakatDuration = "2 Harakat"
    ),
    QALQALAH(
        englishName = "Qalqalah (Echo / Bounce)",
        arabicName = "القلقلة",
        color = Color(0xFF0288D1), // Cyan/Blue
        description = "Vibration or echo sound produced when pronouncing letters (ق ط ب ج د) in state of Sukun.",
        harakatDuration = "Instantaneous rebound"
    ),
    IKHFA(
        englishName = "Ikhfa (Hiding / Concealment)",
        arabicName = "الإخفاء",
        color = Color(0xFF00897B), // Teal
        description = "Concealing Noon Sakinah or Tanween with slight nasalization before its 15 specific letters.",
        harakatDuration = "2 Harakat"
    ),
    IDGHAM(
        englishName = "Idgham (Merging)",
        arabicName = "الإدغام",
        color = Color(0xFF607D8B), // Slate
        description = "Merging of Noon Sakinah or Tanween into the letters of (ي ر م ل و ن).",
        harakatDuration = "2 Harakat (with Ghunnah) / 0 (without Ghunnah)"
    ),
    IQLAB(
        englishName = "Iqlab (Conversion to Meem)",
        arabicName = "الإقلاب",
        color = Color(0xFF2E7D32), // Emerald
        description = "Converting Noon Sakinah or Tanween into a hidden Meem with Ghunnah before letter Ba (ب).",
        harakatDuration = "2 Harakat"
    )
}

data class TajweedRuleOccurrence(
    val rule: TajweedRuleType,
    val start: Int,
    val end: Int,
    val matchedText: String
)

object TajweedEngine {

    val QALQALAH_LETTERS = setOf('ق', 'ط', 'ب', 'ج', 'د')
    val IKHFA_LETTERS = setOf('ت', 'ث', 'ج', 'د', 'ذ', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ف', 'ق', 'ك')
    val IDGHAM_LETTERS = setOf('ي', 'ر', 'م', 'ل', 'و', 'ن')
    val MADD_SIGNS = setOf('~', 'آ', 'ْ', 'ٰ')

    /**
     * Parses Arabic text into an AnnotatedString with standard Quran.com Tajweed colors.
     */
    fun annotateTajweedText(text: String, isTajweedActive: Boolean = true): AnnotatedString {
        if (!isTajweedActive) {
            return AnnotatedString(text)
        }

        val occurrences = detectRules(text)
        return buildAnnotatedString {
            append(text)
            for (occ in occurrences) {
                if (occ.start in 0..text.length && occ.end in occ.start..text.length) {
                    addStyle(
                        style = SpanStyle(
                            color = occ.rule.color,
                            fontWeight = FontWeight.Bold
                        ),
                        start = occ.start,
                        end = occ.end
                    )
                }
            }
        }
    }

    /**
     * Detects specific Tajweed rule occurrences and character bounds for interactive inspection.
     */
    fun detectRules(text: String): List<TajweedRuleOccurrence> {
        val list = mutableListOf<TajweedRuleOccurrence>()
        var i = 0
        while (i < text.length) {
            val char = text[i]

            // 1. Madd indicators (Madd sign ~ or Alif Maddah آ or dagger alif ٰ followed by Hamzah)
            if (char == '~' || char == 'آ') {
                val end = (i + 2).coerceAtMost(text.length)
                list.add(TajweedRuleOccurrence(TajweedRuleType.MADD, i, end, text.substring(i, end)))
                i = end
                continue
            }

            // 2. Noon Mushaddadah or Meem Mushaddadah (Ghunnah)
            if ((char == 'ن' || char == 'م') && i + 1 < text.length && text[i + 1] == 'ّ') {
                val end = (i + 2).coerceAtMost(text.length)
                list.add(TajweedRuleOccurrence(TajweedRuleType.GHUNNAH, i, end, text.substring(i, end)))
                i = end
                continue
            }

            // 3. Iqlab (small Meem sign ۭ or Noon before Ba)
            if (char == 'ۭ' || (char == 'ن' && i + 1 < text.length && text[i + 1] == 'ب')) {
                val end = (i + 2).coerceAtMost(text.length)
                list.add(TajweedRuleOccurrence(TajweedRuleType.IQLAB, i, end, text.substring(i, end)))
                i = end
                continue
            }

            // 4. Qalqalah (ق ط ب ج د with Sukun or at word boundary)
            if (QALQALAH_LETTERS.contains(char)) {
                val isSukun = i + 1 < text.length && (text[i + 1] == 'ْ' || text[i + 1] == ' ')
                val isEnd = i == text.length - 1
                if (isSukun || isEnd) {
                    val end = (i + (if (isSukun) 2 else 1)).coerceAtMost(text.length)
                    list.add(TajweedRuleOccurrence(TajweedRuleType.QALQALAH, i, end, text.substring(i, end)))
                    i = end
                    continue
                }
            }

            // 5. Ikhfa (Noon Sakinah or Tanween followed by an Ikhfa letter)
            if (char == 'ن' && i + 2 < text.length && IKHFA_LETTERS.contains(text[i + 2])) {
                val end = (i + 3).coerceAtMost(text.length)
                list.add(TajweedRuleOccurrence(TajweedRuleType.IKHFA, i, end, text.substring(i, end)))
                i = end
                continue
            }

            // 6. Idgham (Noon Sakinah followed by an Idgham letter)
            if (char == 'ن' && i + 2 < text.length && IDGHAM_LETTERS.contains(text[i + 2])) {
                val end = (i + 3).coerceAtMost(text.length)
                list.add(TajweedRuleOccurrence(TajweedRuleType.IDGHAM, i, end, text.substring(i, end)))
                i = end
                continue
            }

            i++
        }
        return list
    }
}
