package com.example.data.seed

data class SubjectResourceRef(
    val type: String, // "Quran", "Hadith", "Prophet", "Caliph"
    val citation: String,
    val title: String,
    val textExcerpt: String
)

data class AlimSubjectTopic(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val iconKey: String,
    val summary: String,
    val references: List<SubjectResourceRef>
)

object AlimSubjectIndexData {

    val TOPICS = listOf(
        AlimSubjectTopic(
            id = "tawhid_faith",
            title = "Tawhid & The Oneness of Allah",
            arabicTitle = "التوحيد والإيمان بالله",
            iconKey = "star",
            summary = "The singular foundation of Islam: recognizing Allah's exclusive sovereignty, divine names, and devotion free of all partnership.",
            references = listOf(
                SubjectResourceRef(
                    type = "Quran",
                    citation = "Surah Al-Ikhlas 112:1-4",
                    title = "Absolute Divine Uniqueness",
                    textExcerpt = "Say, 'He is Allah, [who is] One, Allah, the Eternal Refuge. He neither begets nor is born, nor is there to Him any equivalent.'"
                ),
                SubjectResourceRef(
                    type = "Quran",
                    citation = "Surah Al-Baqarah 2:255",
                    title = "Ayat al-Kursi (The Throne Verse)",
                    textExcerpt = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep."
                ),
                SubjectResourceRef(
                    type = "Hadith",
                    citation = "Sahih al-Bukhari 8 • Sahih Muslim 16",
                    title = "The Five Pillars of Islam",
                    textExcerpt = "Islam is built on five: To testify that there is no god but Allah and Muhammad is His Messenger, to establish prayer, give zakat, fast Ramadan, and make pilgrimage."
                ),
                SubjectResourceRef(
                    type = "Prophet",
                    citation = "Prophet Ibrahim (AS)",
                    title = "The Champion of Monotheism",
                    textExcerpt = "Prophet Ibrahim shattered the idols of his people and dedicated his entire life and lineage to the pure worship of the One Creator."
                )
            )
        ),
        AlimSubjectTopic(
            id = "akhlaq_character",
            title = "Noble Character, Truthfulness & Manners",
            arabicTitle = "مكارم الأخلاق والصدق والآداب",
            iconKey = "favorite",
            summary = "The moral excellence that forms the crown of faith, emphasizing gentleness, honesty, restraint of anger, and fulfilling trusts.",
            references = listOf(
                SubjectResourceRef(
                    type = "Quran",
                    citation = "Surah Al-Qalam 68:4",
                    title = "Exalted Standard of Character",
                    textExcerpt = "And indeed, you [O Muhammad] are of a great moral character."
                ),
                SubjectResourceRef(
                    type = "Quran",
                    citation = "Surah Ali 'Imran 3:134",
                    title = "Restraining Anger & Pardoning",
                    textExcerpt = "Who spend [in the cause of Allah] during ease and hardship and who restrain anger and who pardon the people - and Allah loves the doers of good."
                ),
                SubjectResourceRef(
                    type = "Hadith",
                    citation = "Al-Adab Al-Mufrad 273 • Sahih",
                    title = "Mission to Perfect Good Character",
                    textExcerpt = "The Messenger of Allah (pbuh) said: 'I was sent only to perfect noble character.'"
                ),
                SubjectResourceRef(
                    type = "Hadith",
                    citation = "Sahih al-Bukhari 6094",
                    title = "Truthfulness Leads to Righteousness",
                    textExcerpt = "Truthfulness leads to righteousness, and righteousness leads to Paradise."
                )
            )
        ),
        AlimSubjectTopic(
            id = "family_parents",
            title = "Family Harmony, Spouse & Respect for Parents",
            arabicTitle = "الأسرة والزوجين وبر الوالدين",
            iconKey = "home",
            summary = "Building tranquil households anchored in mutual affection, lifelong honor for parents, and righteous upbringing of children.",
            references = listOf(
                SubjectResourceRef(
                    type = "Quran",
                    citation = "Surah Ar-Rum 30:21",
                    title = "Affection & Mercy Between Spouses",
                    textExcerpt = "And of His signs is that He created for you from yourselves mates that you may find tranquility in them; and He placed between you affection and mercy."
                ),
                SubjectResourceRef(
                    type = "Quran",
                    citation = "Surah Al-Isra 17:23-24",
                    title = "Kindness to Aging Parents",
                    textExcerpt = "And your Lord has decreed that you not worship except Him, and to parents, good treatment. Whether one or both of them reach old age, do not say to them 'uff'."
                ),
                SubjectResourceRef(
                    type = "Hadith",
                    citation = "Sahih al-Bukhari 5971 • Sahih Muslim 2548",
                    title = "The Highest Right of Companionship: Mother",
                    textExcerpt = "A man asked: 'Who among people is most deserving of my fine companionship?' He replied: 'Your mother, then your mother, then your mother, then your father.'"
                )
            )
        ),
        AlimSubjectTopic(
            id = "social_justice_charity",
            title = "Social Justice, Charity & Community Welfare",
            arabicTitle = "العدل الاجتماعي والصدقة والتكافل",
            iconKey = "volunteer_activism",
            summary = "Eradicating poverty and oppression through institutional Zakat, voluntary Sadaqah, fair treatment of orphans, and equal justice under divine law.",
            references = listOf(
                SubjectResourceRef(
                    type = "Quran",
                    citation = "Surah An-Nisa 4:135",
                    title = "Standing Firmly for Unbiased Justice",
                    textExcerpt = "O you who have believed, be persistently standing firm in justice, witnesses for Allah, even if it be against yourselves or parents and relatives."
                ),
                SubjectResourceRef(
                    type = "Quran",
                    citation = "Surah Al-Baqarah 2:261",
                    title = "The Multiplied Harvest of Charity",
                    textExcerpt = "The example of those who spend their wealth in the way of Allah is like a seed of grain which grows seven spikes; in each spike is a hundred grains."
                ),
                SubjectResourceRef(
                    type = "Hadith",
                    citation = "Sahih al-Bukhari 6011 • Sahih Muslim 2586",
                    title = "The Global Ummah as One Body",
                    textExcerpt = "The believers in their mutual kindness, compassion, and sympathy are just like one body: when one limb suffers, the whole body responds with wakefulness and fever."
                ),
                SubjectResourceRef(
                    type = "Caliph",
                    citation = "Umar ibn al-Khattab (RA)",
                    title = "Universal Public Welfare System",
                    textExcerpt = "Caliph Umar instituted child stipends, pensions for elderly non-Muslim citizens, and granaries for drought relief across the entire state."
                )
            )
        ),
        AlimSubjectTopic(
            id = "commerce_business",
            title = "Commerce, Honest Trade & Financial Ethics",
            arabicTitle = "المعاملات والتجارة والنزاهة المالية",
            iconKey = "account_balance",
            summary = "Islamic commercial guidelines emphasizing transparency, prohibition of usury (Riba) and fraud, honoring contracts, and ethical investment.",
            references = listOf(
                SubjectResourceRef(
                    type = "Quran",
                    citation = "Surah Al-Baqarah 2:275",
                    title = "Permissibility of Trade & Prohibition of Riba",
                    textExcerpt = "Allah has permitted trade and has forbidden interest (usury)."
                ),
                SubjectResourceRef(
                    type = "Quran",
                    citation = "Surah Al-Mutaffifeen 83:1-3",
                    title = "Woe to Those Who Give Short Measure",
                    textExcerpt = "Woe to those who give less [than due], who, when they take a measure from people, take in full, but when they measure for them, they cause loss."
                ),
                SubjectResourceRef(
                    type = "Hadith",
                    citation = "Jami` at-Tirmidhi 1209 • Hasan",
                    title = "The Rank of the Honest Merchant",
                    textExcerpt = "The truthful, trustworthy merchant is with the Prophets, the truthful, and the martyrs on the Day of Resurrection."
                )
            )
        ),
        AlimSubjectTopic(
            id = "trials_patience_akhirah",
            title = "Patience in Trials, Grief & The Afterlife",
            arabicTitle = "الابتلاء والصبر واليوم الآخر",
            iconKey = "psychology",
            summary = "Navigating worldly loss, chronic sickness, and tragedy with spiritual fortitude (Sabr), knowing this life is a temporary test before the eternal realm.",
            references = listOf(
                SubjectResourceRef(
                    type = "Quran",
                    citation = "Surah Al-Baqarah 2:155-156",
                    title = "The Test of Fear, Hunger & Loss",
                    textExcerpt = "And We will surely test you with something of fear and hunger and a loss of wealth and lives and fruits, but give good tidings to the patient, who say: 'Indeed we belong to Allah, and indeed to Him we will return.'"
                ),
                SubjectResourceRef(
                    type = "Quran",
                    citation = "Surah Ash-Sharh 94:5-6",
                    title = "With Every Hardship Comes Ease",
                    textExcerpt = "For indeed, with hardship [will be] ease. Indeed, with hardship [will be] ease."
                ),
                SubjectResourceRef(
                    type = "Hadith",
                    citation = "Sahih Muslim 2999",
                    title = "The Extraordinary Affair of the Believer",
                    textExcerpt = "Wondrous is the affair of the believer! Everything is good for him: if prosperity comes, he is grateful, which is good; and if adversity strikes, he is patient, which is good."
                ),
                SubjectResourceRef(
                    type = "Prophet",
                    citation = "Prophet Ayyub (AS)",
                    title = "The Embodiment of Patient Fortitude",
                    textExcerpt = "Prophet Ayyub endured the loss of his health, wealth, and children for decades with unwavering gratitude until Allah restored all and multiplied it."
                )
            )
        )
    )
}
