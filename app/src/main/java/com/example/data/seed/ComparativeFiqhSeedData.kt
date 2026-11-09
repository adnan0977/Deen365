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
            coreQuestion = "Is maintaining sequential order and formulating intention an absolute pillar (Fard/Rukn) of Wudu?",
            consensusPoints = "All four schools agree that washing the face, arms to elbows, wiping the head, and washing the feet to ankles are explicitly commanded in Surah Al-Ma'idah (5:6).",
            quickSummaryTable = mapOf(
                Madhab.HANAFI to "Sunnah Mu'akkadah (Wudu valid without order)",
                Madhab.MALIKI to "Niyyah is Fard; Tartib is Sunnah",
                Madhab.SHAFII to "Both Niyyah & Tartib are Rukn (Pillar)",
                Madhab.HANBALI to "Both Niyyah & Tartib are Wajib conditions"
            ),
            practicalTakeaway = "Formulating an internal intention and following the sequential order ensures valid Wudu across all four schools of thought without controversy.",
            rulings = listOf(
                MadhabRuling(
                    madhab = Madhab.HANAFI,
                    rulingSummary = "Niyyah and Tartib are confirmed Sunnahs (Sunnah Mu'akkadah), not invalidating Fard conditions.",
                    legalGrade = "Sunnah Mu'akkadah",
                    detailedExplanation = "Water is inherently pure and purifying (Tahur). If one washes the 4 organs out of order or plunges into a river without specific intention, the state of minor ritual impurity (Hadath) is still lifted.",
                    primaryDalil = "Quran 5:6 mentions the organs with the conjunction 'Wa' (and), which in classical Arabic indicates combination without enforcing chronological sequence.",
                    scholarlyNote = "Intention remains highly rewarded; deliberately omitting it without excuse is disliked.",
                    classicalManualRef = "Al-Hidayah by Al-Marghinani, Vol 1, p. 14",
                    juristicPrinciple = "Water purifies by its intrinsic nature; intention is required for divine reward, not basic purification."
                ),
                MadhabRuling(
                    madhab = Madhab.MALIKI,
                    rulingSummary = "Niyyah is an obligatory pillar (Fard); Tartib is a Sunnah.",
                    legalGrade = "Fard (Intention) / Sunnah (Order)",
                    detailedExplanation = "Wudu is an act of devoted worship ('Ibadah) distinguished from customary cooling or personal hygiene exclusively through the conscious spiritual intention in the heart.",
                    primaryDalil = "Hadith: 'Actions are judged only by intentions, and for every person is only that which he intended' (Bukhari & Muslim).",
                    scholarlyNote = "Continuous washing without long interruption (Muwalah) is also mandatory in the Maliki school.",
                    classicalManualRef = "Mukhtasar Khalil / Al-Mudawwanah, Vol 1, p. 28",
                    juristicPrinciple = "Devotional acts must be differentiated from mundane habits via conscious intention."
                ),
                MadhabRuling(
                    madhab = Madhab.SHAFII,
                    rulingSummary = "Both Intention (Niyyah) and Sequential Order (Tartib) are integral pillars (Arkan).",
                    legalGrade = "Rukn (Integral Pillar)",
                    detailedExplanation = "The intention must accompany the first wash of the face. Skipping order or washing feet before arms invalidates the ablution.",
                    primaryDalil = "The Prophet ﷺ always performed Wudu sequentially and said: 'Begin with that which Allah began with' (Sahih Muslim).",
                    scholarlyNote = "Imam ash-Shafi'i emphasized that Allah listed wiping the head between the two washings (arms and feet) deliberately to enforce chronological order.",
                    classicalManualRef = "Minhaj at-Talibin by Imam an-Nawawi, p. 12",
                    juristicPrinciple = "Inserting a wiped organ between washed organs breaks grammatical pairing to denote mandatory sequence."
                ),
                MadhabRuling(
                    madhab = Madhab.HANBALI,
                    rulingSummary = "Both Niyyah and Tartib, along with continuity (Muwalah), are obligatory conditions.",
                    legalGrade = "Wajib / Shart",
                    detailedExplanation = "Ablution performed contrary to prophetic sequence or without prior intention does not permit one to pray.",
                    primaryDalil = "Authentic narrations in Sunan Abi Dawud where the Prophet ﷺ instructed a person who washed out of sequence to repeat his ablution.",
                    scholarlyNote = "Also considers reciting the Basmalah at the beginning to be Wajib if remembered.",
                    classicalManualRef = "Al-Mughni by Ibn Qudamah, Vol 1, p. 98",
                    juristicPrinciple = "Prophetic acts explaining divine commands carry obligatory status unless proof of flexibility exists."
                )
            )
        ),

        ComparativeFiqhTopic(
            topicId = "salah_hand_placement",
            title = "Placement of Hands in Standing Prayer (Qiyam)",
            category = "Salah (Prayer)",
            coreQuestion = "Where should the hands be placed during the standing recitation in prayer?",
            consensusPoints = "Placing the right hand over the left wrist/forearm is an authentic prophetic practice attested across numerous Sahaba.",
            quickSummaryTable = mapOf(
                Madhab.HANAFI to "Below navel for men, on chest for women",
                Madhab.MALIKI to "Sadl (sides) or Qabd (chest/navel) both valid",
                Madhab.SHAFII to "Above navel and below chest, towards heart",
                Madhab.HANBALI to "Below navel or on chest; both are Sunnah"
            ),
            practicalTakeaway = "All hand positions are authentically transmitted from the early generations of Sahaba and represent legitimate expressions of humble servitude (Khushu').",
            rulings = listOf(
                MadhabRuling(
                    madhab = Madhab.HANAFI,
                    rulingSummary = "Men place hands below the navel; women place hands upon the chest.",
                    legalGrade = "Sunnah",
                    detailedExplanation = "The right hand grasps the left wrist with thumb and little finger forming a ring below the navel. For women, placing hands on the chest provides maximum modesty (Satr).",
                    primaryDalil = "Report of Ali ibn Abi Talib (RA): 'From the Sunnah in prayer is placing the right hand over the left below the navel' (Musnad Ahmad & Abu Dawud).",
                    scholarlyNote = "Represents physical humility and reverence before the Creator.",
                    classicalManualRef = "Radd al-Muhtar (Ibn Abidin), Vol 1, p. 486",
                    juristicPrinciple = "Physical posture in prayer is patterned after the demeanor of humble servants standing before their king."
                ),
                MadhabRuling(
                    madhab = Madhab.MALIKI,
                    rulingSummary = "Leaving arms at the sides (Sadl) is well-known; placing hands above the navel (Qabd) is also permissible/preferred by many Maliki masters.",
                    legalGrade = "Mubah / Mustahabb",
                    detailedExplanation = "Imam Malik preferred Sadl (leaving hands at the sides) for obligatory prayers based on the continuous practice ('Amal) of the scholars of Madinah, while validating Qabd in voluntary prayers.",
                    primaryDalil = "The continuous preserved living practice of the early generations of Madinah ('Amal Ahl al-Madinah).",
                    scholarlyNote = "Later Maliki masters like Ibn Abd al-Barr and Al-Baji affirmed that both Sadl and Qabd are authentic Sunnahs.",
                    classicalManualRef = "Al-Mudawwanah / Al-Kafi by Ibn Abd al-Barr, p. 42",
                    juristicPrinciple = "The unbroken living practice of thousands of Madinan scholars takes precedence over isolated individual reports."
                ),
                MadhabRuling(
                    madhab = Madhab.SHAFII,
                    rulingSummary = "Hands placed above the navel and below the chest, inclined slightly towards the heart.",
                    legalGrade = "Sunnah",
                    detailedExplanation = "The right hand grasps the left forearm resting on the upper abdomen/chest area, oriented respectfully toward the heart.",
                    primaryDalil = "Hadith of Wa'il ibn Hujr (RA): 'I prayed with the Messenger of Allah ﷺ and he placed his right hand over his left hand upon his chest/upper abdomen' (Sahih Ibn Khuzaymah).",
                    scholarlyNote = "Symbolizes guarding the heart and maintaining undivided spiritual focus.",
                    classicalManualRef = "Al-Majmu' Sharh al-Muhadhdhab, Vol 3, p. 312",
                    juristicPrinciple = "Following explicit marfu' Hadith specifying the physical location of hand placement."
                ),
                MadhabRuling(
                    madhab = Madhab.HANBALI,
                    rulingSummary = "Hands placed below the navel or directly upon the chest; both options are validated in the school.",
                    legalGrade = "Sunnah",
                    detailedExplanation = "Imam Ahmad recorded multiple transmissions: placing the hands below the navel is the predominant position, with placing them on the chest considered equally permissible.",
                    primaryDalil = "Multiple reports recorded in the Musnad of Imam Ahmad demonstrating breadth in prophetic practice.",
                    scholarlyNote = "Imam Ahmad said: 'There is leeway in the matter; whether one places them below or above the navel, both are Sunnah.'",
                    classicalManualRef = "Al-Insaf by Al-Mardawi, Vol 2, p. 48",
                    juristicPrinciple = "Recognizing scholarly pluralism when multiple authentic Sahaba reports describe different postures."
                )
            )
        ),

        ComparativeFiqhTopic(
            topicId = "wudu_touching_skin",
            title = "Touching Non-Mahram Skin - Does It Break Wudu?",
            category = "Taharah & Wudu",
            coreQuestion = "Does skin-to-skin contact between a man and an adult non-mahram woman invalidate ritual purity?",
            consensusPoints = "All schools agree that sexual intercourse invalidates Wudu and necessitates Ghusl by divine consensus.",
            quickSummaryTable = mapOf(
                Madhab.HANAFI to "Does NOT break Wudu (unless sexual arousal)",
                Madhab.MALIKI to "Breaks Wudu only if touch carries desire",
                Madhab.SHAFII to "Breaks Wudu unconditionally upon skin touch",
                Madhab.HANBALI to "Breaks Wudu if touched with lust (Shahwah)"
            ),
            practicalTakeaway = "In crowded public places or during Tawaf, following the majority or Hanafi dispensation avoids constant disruption and hardship.",
            rulings = listOf(
                MadhabRuling(
                    madhab = Madhab.HANAFI,
                    rulingSummary = "Touching does NOT break Wudu unless accompanied by direct sexual arousal/desire.",
                    legalGrade = "Does Not Invalidate",
                    detailedExplanation = "Mere physical touch without passion does not emit bodily impurities and therefore does not breach ritual purity.",
                    primaryDalil = "Aisha (RA) narrated: 'The Prophet ﷺ kissed one of his wives, then went out to pray without performing ablution' (Abu Dawud, Tirmidhi, & Nasa'i).",
                    scholarlyNote = "Quran 4:43 ('or you touched women') is interpreted as a metaphor for sexual relations, consistent with Ibn Abbas's tafsir.",
                    classicalManualRef = "Al-Bahr ar-Ra'iq, Vol 1, p. 102",
                    juristicPrinciple = "Ritual purity is established by certainty and can only be undone by clear evidence of bodily discharge."
                ),
                MadhabRuling(
                    madhab = Madhab.MALIKI,
                    rulingSummary = "Breaks Wudu only if touch was accompanied by intentional desire or if physical pleasure was experienced.",
                    legalGrade = "Conditional Invalidator",
                    detailedExplanation = "If the touch occurred without lust (such as a handshake with an elder or accidental contact in a crowd), Wudu remains completely intact.",
                    primaryDalil = "Synthesis of Quran 4:43 with prophetic practice distinguishing between affectionate touch and casual contact.",
                    scholarlyNote = "Touch with a thin barrier through which body heat is felt with desire also breaks Wudu.",
                    classicalManualRef = "Ash-Sharh al-Kabir by Ad-Dardir, Vol 1, p. 118",
                    juristicPrinciple = "Evaluating legal rulings according to the underlying motive (Qasd) and actual physical sensation (Ladhdha)."
                ),
                MadhabRuling(
                    madhab = Madhab.SHAFII,
                    rulingSummary = "Breaks Wudu unconditionally upon direct skin-to-skin touch between non-mahram adults.",
                    legalGrade = "Absolute Invalidator",
                    detailedExplanation = "Any direct skin contact between a post-pubescent male and female who can lawfully marry invalidates the Wudu of both, regardless of intent or pleasure.",
                    primaryDalil = "Literal reading of Quran 4:43 ('Aw Lamastum an-Nisa'') where 'Lams' in the primary linguistic meaning denotes physical touch by hand.",
                    scholarlyNote = "Contact through clothing or hair/nails does not invalidate according to the relied-upon view.",
                    classicalManualRef = "Al-Majmu' by Imam an-Nawawi, Vol 2, p. 28",
                    juristicPrinciple = "Literal linguistic meaning takes priority over metaphorical interpretation in legal texts."
                ),
                MadhabRuling(
                    madhab = Madhab.HANBALI,
                    rulingSummary = "Breaks Wudu only if the touch occurred with physical lust/desire (Shahwah).",
                    legalGrade = "Invalidates only with Desire",
                    detailedExplanation = "Accidental brush in a crowded market or professional clinical contact without lust does not break Wudu.",
                    primaryDalil = "Aisha (RA) reported that the Prophet ﷺ touched her foot while prostrating at night without repeating Wudu (Sahih Muslim).",
                    scholarlyNote = "If one touches with lust, Wudu is broken even if the contact was over thin clothing in some narrations.",
                    classicalManualRef = "Kashshaf al-Qina' by Al-Buhuti, Vol 1, p. 132",
                    juristicPrinciple = "Harmonizing all sound narrations by conditioning invalidation upon the presence of desire."
                )
            )
        ),

        ComparativeFiqhTopic(
            topicId = "wudu_bleeding",
            title = "Bodily Bleeding & Wounds - Does Blood Invalidate Wudu?",
            category = "Taharah & Wudu",
            coreQuestion = "Does bleeding from an injury, nosebleed, or medical needle break one's state of Wudu?",
            consensusPoints = "All schools agree that blood exiting the two private passages (Sabilayn) breaks Wudu by unanimous consensus.",
            quickSummaryTable = mapOf(
                Madhab.HANAFI to "Breaks Wudu if blood flows beyond wound edge",
                Madhab.MALIKI to "Does NOT break Wudu regardless of amount",
                Madhab.SHAFII to "Does NOT break Wudu (confined to private exits)",
                Madhab.HANBALI to "Breaks Wudu only if blood is copious/large"
            ),
            practicalTakeaway = "Minor paper cuts and blood tests do not affect prayer according to the majority of jurists.",
            rulings = listOf(
                MadhabRuling(
                    madhab = Madhab.HANAFI,
                    rulingSummary = "Flowing blood that exits the body and trickles beyond the wound edge breaks Wudu.",
                    legalGrade = "Invalidator if Flowing",
                    detailedExplanation = "If blood remains stationary within the puncture or is wiped away without flowing, Wudu remains intact. If it runs down the skin, ablution is broken.",
                    primaryDalil = "Hadith: 'Ablution is due for every flowing drop of blood' (Sunan ad-Daraqutni).",
                    scholarlyNote = "Vomiting a mouthful or purulent discharge also breaks Wudu under the same principle.",
                    classicalManualRef = "Al-Ikhtiyar li Ta'lil al-Mukhtar, Vol 1, p. 11",
                    juristicPrinciple = "Impure substances flowing out of the human body compromise the state of external ritual purity."
                ),
                MadhabRuling(
                    madhab = Madhab.MALIKI,
                    rulingSummary = "Bleeding from a wound does not invalidate Wudu regardless of amount.",
                    legalGrade = "Does Not Invalidate",
                    detailedExplanation = "Only impurities exiting the natural two private passages (As-Sabilayn) invalidate ablution. External bleeding does not break the spiritual state of Taharah.",
                    primaryDalil = "Practice of the Companions who continued praying with bleeding wounds in battle without interrupting their Salah.",
                    scholarlyNote = "Cleaning blood off the body and clothing is required for prayer purity if the blood is substantial.",
                    classicalManualRef = "Mukhtasar Khalil / Jawahir al-Iklil, Vol 1, p. 24",
                    juristicPrinciple = "Causes of minor ritual impurity (Hadath) are strictly defined by primary texts to natural excretions."
                ),
                MadhabRuling(
                    madhab = Madhab.SHAFII,
                    rulingSummary = "Bleeding from anywhere other than the two private exits never invalidates Wudu.",
                    legalGrade = "Does Not Invalidate",
                    detailedExplanation = "Whether the bleed is a minor prick or major surgical wound, the internal state of Wudu remains fully intact.",
                    primaryDalil = "Companion Abbad ibn Bishr (RA) was struck by three arrows during night guard duty and continued his recitation and prayer while blood poured from his body (Sunan Abi Dawud).",
                    scholarlyNote = "Imam ash-Shafi'i noted that the Prophet ﷺ did not instruct Abbad to repeat his prayer or ablution.",
                    classicalManualRef = "Al-Umm by Imam ash-Shafi'i, Vol 1, p. 18",
                    juristicPrinciple = "Silence of the Lawgiver regarding bleeding warriors establishes continuity of ritual purity."
                ),
                MadhabRuling(
                    madhab = Madhab.HANBALI,
                    rulingSummary = "Substantial or copious flowing blood breaks Wudu; slight bleeding is excused.",
                    legalGrade = "Invalidates if Copious",
                    detailedExplanation = "A standard blood test needle prick or small paper cut is considered minor ('Yaseer') and does not break Wudu. Significant profuse bleeding invalidates.",
                    primaryDalil = "Hadith of Abu ad-Darda (RA) that the Prophet ﷺ vomited and performed ablution (Sunan at-Tirmidhi).",
                    scholarlyNote = "The definition of 'copious' is referenced to customary human perception (Urf).",
                    classicalManualRef = "Al-Mughni by Ibn Qudamah, Vol 1, p. 139",
                    juristicPrinciple = "Legal hardship (Haraj) is lifted for small unavoidable bodily discharges, while major flow restores strictness."
                )
            )
        ),

        ComparativeFiqhTopic(
            topicId = "qunut_fajr",
            title = "Recitation of Qunut in the Fajr Morning Prayer",
            category = "Salah (Prayer)",
            coreQuestion = "Is reciting Qunut in the second Rak'ah of Fajr a permanent daily Sunnah, or reserved for public calamities (Nazilah)?",
            consensusPoints = "All four schools agree that Qunut is prescribed during major trials and calamities (Qunut an-Nazilah) across daily prayers.",
            quickSummaryTable = mapOf(
                Madhab.HANAFI to "Disliked in daily Fajr; Wajib in Witr",
                Madhab.MALIKI to "Established Sunnah; recited silently before Ruku'",
                Madhab.SHAFII to "Emphasized Sunnah (Ab'ad); Sujud Sahw if omitted",
                Madhab.HANBALI to "Not Sunnah in regular Fajr; reserved for Nazilah"
            ),
            practicalTakeaway = "When praying behind an Imam of another school reciting Qunut in Fajr, one raises hands and says 'Ameen' to safeguard congregational unity.",
            rulings = listOf(
                MadhabRuling(
                    madhab = Madhab.HANAFI,
                    rulingSummary = "Qunut is obligatory (Wajib) in Witr; not prescribed in regular daily Fajr except during calamity (Nazilah).",
                    legalGrade = "Wajib in Witr / Disliked in routine Fajr",
                    detailedExplanation = "The Prophet ﷺ recited Qunut in Fajr for one month supplicating against the murderers of the Quran reciters at Bi'r Ma'unah, then ceased.",
                    primaryDalil = "Hadith of Abu Hurairah and Anas (RA) that the Prophet's Fajr Qunut was temporary for one month (Sahih Bukhari).",
                    scholarlyNote = "If a Hanafi prays behind a Shafi'i Imam reciting Qunut, the follower stays quiet and says 'Ameen'.",
                    classicalManualRef = "Al-Bahr ar-Ra'iq, Vol 2, p. 46",
                    juristicPrinciple = "Cessation of a temporary prophetic practice signifies abrogation of daily routine prescription."
                ),
                MadhabRuling(
                    madhab = Madhab.MALIKI,
                    rulingSummary = "Qunut in Fajr is an established Sunnah (Mustahabb), recited silently before bowing (Ruku').",
                    legalGrade = "Mustahabb (Recommended)",
                    detailedExplanation = "Preferably recited quietly in the second Rak'ah after Surah recitation before Ruku', though reciting after rising is permissible.",
                    primaryDalil = "Continuous practice of the early generations of Madinah from the era of the Rightly Guided Caliphs.",
                    scholarlyNote = "Malikis recite: 'Allahumma inna nasta'inuka wa nastaghfiruka wa nu'minu bika...'",
                    classicalManualRef = "Mukhtasar Khalil / Jawahir al-Iklil, Vol 1, p. 88",
                    juristicPrinciple = "Continuous practice of the inhabitants of the City of Revelation confirms enduring Sunnah."
                ),
                MadhabRuling(
                    madhab = Madhab.SHAFII,
                    rulingSummary = "Qunut in Fajr is an emphasized Sunnah (Sunnah Ab'ad); if omitted, Sujud as-Sahw is recommended.",
                    legalGrade = "Sunnah Ab'ad (Major Sunnah)",
                    detailedExplanation = "Recited aloud by the Imam in the second Rak'ah after rising from Ruku' with hands raised. Omission warrants prostration of forgetfulness.",
                    primaryDalil = "Hadith of Anas ibn Malik (RA): 'The Messenger of Allah ﷺ did not cease reciting Qunut in the morning prayer until he departed this world' (Musnad Ahmad & Daraqutni).",
                    scholarlyNote = "The congregation raises hands and answers 'Ameen' to the Imam's supplication.",
                    classicalManualRef = "Al-Majmu' by Imam an-Nawawi, Vol 3, p. 494",
                    juristicPrinciple = "Positive textual affirmation from an intimate Companion takes precedence over negative inferences."
                ),
                MadhabRuling(
                    madhab = Madhab.HANBALI,
                    rulingSummary = "Qunut in regular Fajr is not prescribed; reserved strictly for Witr and general crises (Qunut an-Nazilah).",
                    legalGrade = "Sunnah in Witr & Calamity / Disliked in routine Fajr",
                    detailedExplanation = "Similar to the Hanafi position, regular daily Fajr Qunut is considered non-standard, while highly encouraged when the Ummah undergoes oppression.",
                    primaryDalil = "Statements of senior Sahaba including Abdullah ibn Mas'ud and Ibn Umar that regular Fajr Qunut was not their habit.",
                    scholarlyNote = "Imam Ahmad said: 'If one prays behind an Imam who recites Qunut in Fajr, follow him and say Ameen to maintain community unity.'",
                    classicalManualRef = "Al-Mughni by Ibn Qudamah, Vol 2, p. 152",
                    juristicPrinciple = "Preserving congregational harmony and unity overrides minor juristic preferences."
                )
            )
        ),

        ComparativeFiqhTopic(
            topicId = "salah_fatihah_behind_imam",
            title = "Reciting Surah Al-Fatihah Behind the Imam in Prayer",
            category = "Salah (Prayer)",
            coreQuestion = "Must the follower recite Surah Al-Fatihah behind the Imam during audible congregational prayers (Fajr, Maghrib, Isha)?",
            consensusPoints = "All schools agree that an individual praying alone (Munfarid) must recite Surah Al-Fatihah in every Rak'ah.",
            quickSummaryTable = mapOf(
                Madhab.HANAFI to "Forbidden / Disliked (Imam's recitation suffices)",
                Madhab.MALIKI to "Recite in silent prayers; listen in loud prayers",
                Madhab.SHAFII to "Obligatory (Fard) for every person in all prayers",
                Madhab.HANBALI to "Recommended in silent pauses; listen in loud"
            ),
            practicalTakeaway = "All four positions are backed by prominent Sahaba interpretations, reflecting depth in understanding prophetic instructions.",
            rulings = listOf(
                MadhabRuling(
                    madhab = Madhab.HANAFI,
                    rulingSummary = "Forbidden / Makruh Tahrimi for the follower to recite behind the Imam; the Imam's recitation suffices.",
                    legalGrade = "Prohibited / Disliked",
                    detailedExplanation = "The follower remains completely silent in both silent and loud prayers, attentively listening or reflecting on the Imam's recitation.",
                    primaryDalil = "Quran 7:204 ('When the Quran is recited, listen attentively and be silent') & Hadith: 'Whoever has an Imam, the recitation of the Imam is recitation for him' (Ibn Majah).",
                    scholarlyNote = "Reciting behind the Imam is viewed as competing with the leader of the congregation.",
                    classicalManualRef = "Al-Hidayah by Al-Marghinani, Vol 1, p. 55",
                    juristicPrinciple = "The Imam represents the congregation; his recitation legally fulfills the requirement for all followers."
                ),
                MadhabRuling(
                    madhab = Madhab.MALIKI,
                    rulingSummary = "Mustahabb to recite in silent prayers; must remain silent listening to the Imam in audible prayers.",
                    legalGrade = "Mustahabb (Silent) / Prohibited (Audible)",
                    detailedExplanation = "In Fajr, Maghrib, and Isha, listening to the Imam is commanded by Quran 7:204. In Dhuhr and Asr, reciting Al-Fatihah softly is recommended.",
                    primaryDalil = "Hadith in Sahih Muslim: 'When the Imam recites, remain silent.'",
                    scholarlyNote = "Harmonizes the Quranic command of listening with the Hadith of Fatihah.",
                    classicalManualRef = "Al-Kafi by Ibn Abd al-Barr, p. 48",
                    juristicPrinciple = "Harmonizing seemingly conflicting texts by assigning each to its proper domain (audible vs silent)."
                ),
                MadhabRuling(
                    madhab = Madhab.SHAFII,
                    rulingSummary = "Recitation of Surah Al-Fatihah is an obligatory pillar (Rukn) for every worshipper in every prayer.",
                    legalGrade = "Rukn (Individual Obligation)",
                    detailedExplanation = "Whether the prayer is loud or silent, and whether one is an Imam, follower, or praying alone, Al-Fatihah must be recited personally.",
                    primaryDalil = "Hadith: 'There is no prayer for the one who does not recite the Opening of the Book (Fatihat al-Kitab)' (Sahih Bukhari & Muslim).",
                    scholarlyNote = "Followers recite Al-Fatihah quietly during the short pause after the Imam finishes reciting Al-Fatihah.",
                    classicalManualRef = "Al-Majmu' by Imam an-Nawawi, Vol 3, p. 365",
                    juristicPrinciple = "General negative particle ('La Salata' - There is no prayer) establishes total negation of legal validity without the pillar."
                ),
                MadhabRuling(
                    madhab = Madhab.HANBALI,
                    rulingSummary = "Recommended to recite during pauses of the Imam in loud prayers and throughout silent prayers.",
                    legalGrade = "Mustahabb",
                    detailedExplanation = "If the follower does not recite because he is listening to the Imam's recitation, his prayer is fully valid by consensus.",
                    primaryDalil = "Synthesis of the Hadith 'There is no prayer without Fatihah' with the Quranic injunction of listening attentively.",
                    scholarlyNote = "If the Imam does not pause, the follower focuses on listening.",
                    classicalManualRef = "Al-Mughni by Ibn Qudamah, Vol 2, p. 118",
                    juristicPrinciple = "Balancing the virtue of personal recitation with the duty of respectful audition."
                )
            )
        ),

        ComparativeFiqhTopic(
            topicId = "sawm_eating_forgetfully",
            title = "Eating or Drinking Due to Forgetfulness While Fasting",
            category = "Sawm (Fasting)",
            coreQuestion = "If a fasting person inadvertently consumes food or drink out of pure forgetfulness, is the fast spoiled?",
            consensusPoints = "All schools agree that deliberate intentional eating or drinking completely invalidates the fast.",
            quickSummaryTable = mapOf(
                Madhab.HANAFI to "Fast is 100% valid; no makeup (Qadha) required",
                Madhab.MALIKI to "Must make up day (Qadha) after Ramadan",
                Madhab.SHAFII to "Fast is completely valid; no makeup needed",
                Madhab.HANBALI to "Fast is completely valid; divine hospitality"
            ),
            practicalTakeaway = "Three of the four Sunni schools hold that forgetful eating does not spoil the fast in any way, viewing it as a divine gift.",
            rulings = listOf(
                MadhabRuling(
                    madhab = Madhab.HANAFI,
                    rulingSummary = "Fast remains completely valid; no Qadha (makeup) and no penalty required.",
                    legalGrade = "Valid Fast / Excused",
                    detailedExplanation = "As soon as the person remembers they are fasting, they must immediately spit out any food remaining in the mouth and continue the fast.",
                    primaryDalil = "Prophetic Hadith: 'Whoever forgets while fasting and eats or drinks, let him complete his fast, for it was Allah who fed him and gave him drink' (Bukhari & Muslim).",
                    scholarlyNote = "Applies equally to obligatory Ramadan fasts, missed makeup fasts, and voluntary Sunnah fasts.",
                    classicalManualRef = "Al-Hidayah, Vol 1, p. 120",
                    juristicPrinciple = "Divine attribution ('Allah fed him') removes human legal culpability and preserves the fast."
                ),
                MadhabRuling(
                    madhab = Madhab.MALIKI,
                    rulingSummary = "Must complete the day's fast, but makeup (Qadha) of that day is required after Ramadan.",
                    legalGrade = "Qadha Required (Without Kaffarah)",
                    detailedExplanation = "The Maliki school strictly analyzes the physical essence of the fast (Rukn of Imsak / Abstinence). Since the fast was physically breached, the day must be compensated later, though no sin or expiation applies.",
                    primaryDalil = "Legal analogy (Qiyas) with prayer: one who speaks forgetfully in prayer must repeat it or make Sujud Sahw depending on extent.",
                    scholarlyNote = "In voluntary fasts, some Maliki scholars excuse forgetful eating without requiring makeup.",
                    classicalManualRef = "Mukhtasar Khalil / Al-Mudawwanah, Vol 1, p. 198",
                    juristicPrinciple = "Physical breach of a devotional pillar necessitates remedial performance regardless of absence of sin."
                ),
                MadhabRuling(
                    madhab = Madhab.SHAFII,
                    rulingSummary = "Fast is 100% valid; no makeup and no expiation are required.",
                    legalGrade = "Completely Valid Fast",
                    detailedExplanation = "Forgetfulness completely lifts the pen of accountability. The food consumed is divine hospitality.",
                    primaryDalil = "Explicit Hadith: 'Whoever breaks his fast in Ramadan forgetfully, there is no makeup upon him nor any expiation' (Sunan ad-Daraqutni & Sahih Ibn Hibban).",
                    scholarlyNote = "If an observer sees someone eating forgetfully, it is obligatory to remind them immediately.",
                    classicalManualRef = "Minhaj at-Talibin by An-Nawawi, p. 84",
                    juristicPrinciple = "Explicit sound Hadith mentioning 'no makeup' overrules theoretical analogies."
                ),
                MadhabRuling(
                    madhab = Madhab.HANBALI,
                    rulingSummary = "Fast is valid without doubt; no makeup or penalty applies.",
                    legalGrade = "Valid Fast",
                    detailedExplanation = "Whether the amount eaten was small or large, forgetfulness suspends the invalidating effect.",
                    primaryDalil = "Bukhari and Muslim narrations declaring the act to be divine sustenance ('Allah fed him and gave him drink').",
                    scholarlyNote = "If someone reminds the person while food is in their throat, they must not swallow it.",
                    classicalManualRef = "Al-Mughni by Ibn Qudamah, Vol 3, p. 48",
                    juristicPrinciple = "Compassionate divine exceptions must be applied comprehensively as stated."
                )
            )
        ),

        ComparativeFiqhTopic(
            topicId = "zakat_women_jewelry",
            title = "Zakat on Women's Personal Gold & Silver Jewelry",
            category = "Zakat & Wealth",
            coreQuestion = "Is annual Zakat due on personal gold and silver jewelry worn by women for lawful adornment?",
            consensusPoints = "All schools agree that gold or silver acquired for commercial trade, business inventory, or financial investment is subject to 2.5% Zakat.",
            quickSummaryTable = mapOf(
                Madhab.HANAFI to "Obligatory (Wajib) on all gold above 85g",
                Madhab.MALIKI to "Exempt from Zakat (personal adornment)",
                Madhab.SHAFII to "Exempt from Zakat if customary in amount",
                Madhab.HANBALI to "Exempt from Zakat for lawful wear/lending"
            ),
            practicalTakeaway = "The majority of the four schools (Maliki, Shafi'i, Hanbali) exempt personal jewelry from Zakat, while Hanafi scholars uphold caution by calculating 2.5% on all gold.",
            rulings = listOf(
                MadhabRuling(
                    madhab = Madhab.HANAFI,
                    rulingSummary = "Zakat is strictly obligatory (Wajib) on all gold and silver reaching Nisab (85g gold), even if worn for personal adornment.",
                    legalGrade = "Fard / Wajib",
                    detailedExplanation = "Gold and silver are intrinsically monetary assets ('Athman Mutlaqah'). Their nature as stores of value never ceases, regardless of whether they are fashioned into bracelets or stored as bullion.",
                    primaryDalil = "Hadith of the woman who visited the Prophet ﷺ with two gold bangles on her daughter's wrists: 'Do you pay Zakat on this?' She said no. He said: 'Would you be pleased if Allah encircled you with two bracelets of fire?' (Abu Dawud & Nasa'i).",
                    scholarlyNote = "Calculates 2.5% on the pure gold/silver weight value annually.",
                    classicalManualRef = "Radd al-Muhtar, Vol 2, p. 298",
                    juristicPrinciple = "Gold and silver are absolute currency; conversion into jewelry does not extinguish their taxable nature."
                ),
                MadhabRuling(
                    madhab = Madhab.MALIKI,
                    rulingSummary = "Exempt from Zakat when acquired for lawful personal wearing, lending, or customary adornment.",
                    legalGrade = "Exempt from Zakat",
                    detailedExplanation = "Personal jewelry is categorized as personal-use property ('Urud al-Qiniyah), analogous to a person's house, riding mount, or clothing.",
                    primaryDalil = "Abdullah ibn Umar (RA) adorned his daughters and slave-girls with gold and did not pay Zakat on their jewelry (Al-Muwatta).",
                    scholarlyNote = "If jewelry is acquired with intention of wealth preservation or store of value, Zakat becomes due.",
                    classicalManualRef = "Al-Muwatta of Imam Malik / Al-Kafi, p. 112",
                    juristicPrinciple = "Assets designated for personal physical utility are exempt from growth-based ('Nama') taxation."
                ),
                MadhabRuling(
                    madhab = Madhab.SHAFII,
                    rulingSummary = "Exempt from Zakat as long as the quantity is within customary and reasonable feminine adornment.",
                    legalGrade = "Exempt from Zakat",
                    detailedExplanation = "Permissible personal adornment removes the quality of growth ('Nama''). Only extravagant amounts beyond standard societal norms become taxable.",
                    primaryDalil = "Narrated by Jabir ibn Abdullah (RA): 'There is no Zakat upon jewelry' (Sunan al-Bayhaqi).",
                    scholarlyNote = "Jewelry owned by men (other than a silver ring) is unlawful and therefore subject to Zakat.",
                    classicalManualRef = "Minhaj at-Talibin by An-Nawawi, p. 68",
                    juristicPrinciple = "Permissible personal utilization negates the taxable status of precious metals."
                ),
                MadhabRuling(
                    madhab = Madhab.HANBALI,
                    rulingSummary = "No Zakat on permissible jewelry kept for personal wearing, lending, or adornment.",
                    legalGrade = "Exempt from Zakat",
                    detailedExplanation = "Concurs with the Maliki and Shafi'i majority. The asset is consumed in lawful personal benefit rather than financial commercial growth.",
                    primaryDalil = "Practices of prominent Sahaba including Aisha, Ibn Umar, and Anas ibn Malik (RA) who did not assess Zakat on family jewelry.",
                    scholarlyNote = "If jewelry is broken and held without intention of repair, it reverts to bullion and becomes taxable.",
                    classicalManualRef = "Al-Mughni by Ibn Qudamah, Vol 2, p. 604",
                    juristicPrinciple = "Consensus of the major Companions takes precedence in defining exceptions to general rules."
                )
            )
        )
    )

    fun getTopic(topicId: String): ComparativeFiqhTopic? {
        return TOPICS.firstOrNull { it.topicId == topicId } ?: TOPICS.firstOrNull()
    }
}
