package com.example.data.seed

import com.example.data.model.CommunityComment
import com.example.data.model.CommunityPost
import com.example.data.model.CommunityTopicCategory
import com.example.data.model.DuaRequestItem

object CommunitySeedData {
    val INITIAL_POSTS: List<CommunityPost> = listOf(
        CommunityPost(
            id = "seed_post_1",
            authorId = "system_scholar",
            authorName = "Ustadh Farhan",
            authorRole = "moderator",
            category = CommunityTopicCategory.QURAN_TADABBUR,
            title = "Contemplation on Surah Al-Baqarah (2:286): No Soul is Burdened Beyond Capacity",
            content = "Whatever emotional weight or life trials you are currently carrying, remember this absolute divine guarantee from Allah: 'Allah does not burden a soul beyond that it can bear'. If Allah permitted this challenge to enter your life, He also placed within you the spiritual endurance to overcome it. Turn your anxiety into Sujood tonight.",
            ayahReference = "Surah Al-Baqarah (2:286)",
            likesCount = 38,
            commentsCount = 6,
            isPinned = true,
            timestamp = System.currentTimeMillis() - 7200000L
        ),
        CommunityPost(
            id = "seed_post_2",
            authorId = "user_bilal_m",
            authorName = "Bilal Malik",
            authorRole = "member",
            category = CommunityTopicCategory.HADITH_STUDY,
            title = "The Golden Rule of Sincerity: The Halal is Clear and the Haram is Clear",
            content = "Studied Hadith 6 of An-Nawawi today with my local study circle. The Prophet ﷺ mentioned that between Halal and Haram lie ambiguous doubtful matters (Mushtabihat). Protecting our hearts means leaving the grey areas. In the heart there is a piece of flesh: if it is sound, the whole body is sound.",
            hadithReference = "Sahih al-Bukhari #52, Sahih Muslim #1599",
            likesCount = 24,
            commentsCount = 3,
            timestamp = System.currentTimeMillis() - 18000000L
        ),
        CommunityPost(
            id = "seed_post_3",
            authorId = "user_fatima_k",
            authorName = "Fatima Khan",
            authorRole = "member",
            category = CommunityTopicCategory.FIQH_QUESTIONS,
            title = "Tips for Maintaining Khushu (Presence of Mind) in Fajr & Isha",
            content = "SubhanAllah, after years of struggling with rushing through prayer, I started doing three things: 1) Performing Wudu deliberately with the authentic Du'a after it, 2) Spending 30 seconds sitting in silent Dhikr before saying the Takbeerat al-Ihram, 3) Pausing at each Ayah of Surah Al-Fatihah. It has completely transformed my Salah.",
            ayahReference = "Surah Al-Mu'minun (23:1-2)",
            likesCount = 52,
            commentsCount = 9,
            timestamp = System.currentTimeMillis() - 36000000L
        ),
        CommunityPost(
            id = "seed_post_4",
            authorId = "user_tariq_a",
            authorName = "Tariq Ansari",
            authorRole = "member",
            category = CommunityTopicCategory.SPIRITUAL_GROWTH,
            title = "Consistency Over Quantity: The Most Beloved Deeds to Allah",
            content = "A reminder to myself and my brothers and sisters: Don't feel overwhelmed trying to do 100 things in one day and burning out. The Messenger of Allah ﷺ said: 'The most beloved deeds to Allah are those that are consistent, even if they are few.' Even reading one page of Quran daily or praying 2 Rak'ahs of Duha every morning is heavier on the scale than an inconsistent burst.",
            hadithReference = "Sahih al-Bukhari #6464",
            likesCount = 41,
            commentsCount = 4,
            timestamp = System.currentTimeMillis() - 86400000L
        )
    )

    val INITIAL_DUA_REQUESTS: List<DuaRequestItem> = listOf(
        DuaRequestItem(
            id = "dua_req_1",
            requesterId = "user_amina_s",
            requesterName = "Sister Amina",
            requestTitle = "Du'a for Healing & Shifa for My Elderly Mother",
            requestDetails = "Assalamu Alaikum beloved community. My mother is undergoing emergency surgery tomorrow morning. Please keep her in your sincere prayers and Tahajjud supplications for complete Shifa and strength.",
            ameenCount = 89,
            timestamp = System.currentTimeMillis() - 14400000L
        ),
        DuaRequestItem(
            id = "dua_req_2",
            requesterId = "user_omar_z",
            requesterName = "Brother Omar",
            requestTitle = "Du'a for Barakah and Halal Sustenance in New Job",
            requestDetails = "I was unemployed for 5 months and just accepted an offer. Please pray that Allah places Barakah in my earnings and allows me to support my young family with righteousness and honesty.",
            ameenCount = 64,
            timestamp = System.currentTimeMillis() - 43200000L
        ),
        DuaRequestItem(
            id = "dua_req_3",
            requesterId = "user_zaynab_h",
            requesterName = "Sister Zaynab",
            requestTitle = "Du'a for Steadfastness in Wearing the Hijab",
            requestDetails = "Starting university this semester and facing peer pressure. Requesting your du'as that Allah grants me unwavering firmness (Thabat) and courage upon the Sunnah.",
            ameenCount = 112,
            timestamp = System.currentTimeMillis() - 86400000L
        )
    )

    val INITIAL_COMMENTS: Map<String, List<CommunityComment>> = mapOf(
        "seed_post_1" to listOf(
            CommunityComment(
                id = "comm_1_1",
                postId = "seed_post_1",
                authorName = "Zaid Qureshi",
                text = "JazakAllahu Khairan Ustadh. I really needed to read this exact verse today after a grueling week.",
                timestamp = System.currentTimeMillis() - 5400000L
            ),
            CommunityComment(
                id = "comm_1_2",
                postId = "seed_post_1",
                authorName = "Maryam Siddiqui",
                text = "SubhanAllah, reflection on this verse removes mountains of unnecessary stress. May Allah bless you.",
                timestamp = System.currentTimeMillis() - 3600000L
            )
        ),
        "seed_post_3" to listOf(
            CommunityComment(
                id = "comm_3_1",
                postId = "seed_post_3",
                authorName = "Dr. Asim",
                text = "The 30-second quiet pause before Takbeerat al-Ihram makes a night-and-day difference in slowing down the racing heart. Excellent advice!",
                timestamp = System.currentTimeMillis() - 18000000L
            )
        )
    )
}
