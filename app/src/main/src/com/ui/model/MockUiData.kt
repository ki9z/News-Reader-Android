package com.ui.model

object MockUiData {
    val categories = listOf("Top", "Local", "Following", "Entertainment", "Lifestyle", "U.S.")

    val sources = listOf("Top News", "The Guardian", "ESPN", "ABC News")

    val exploreTopics = listOf(
        ExploreTopicItem("us-politics", "U.S. Politics", "https://picsum.photos/seed/explore_1/220/220"),
        ExploreTopicItem("foreign-policy", "Foreign policy", "https://picsum.photos/seed/explore_2/220/220"),
        ExploreTopicItem("middle-east", "Middle East", "https://picsum.photos/seed/explore_3/220/220"),
        ExploreTopicItem("entertainment", "Entertainment", "https://picsum.photos/seed/explore_4/220/220"),
        ExploreTopicItem("movies", "Movies", "https://picsum.photos/seed/explore_5/220/220"),
        ExploreTopicItem("technology", "Technology", "https://picsum.photos/seed/explore_6/220/220"),
        ExploreTopicItem("science", "Science", "https://picsum.photos/seed/explore_7/220/220"),
        ExploreTopicItem("business", "Business", "https://picsum.photos/seed/explore_8/220/220"),
        ExploreTopicItem("health", "Health", "https://picsum.photos/seed/explore_9/220/220"),
        ExploreTopicItem("sports", "Sports", "https://picsum.photos/seed/explore_10/220/220")
    )

    val homeNews = listOf(
        NewsItem(
            id = "1",
            title = "Iran fires on targets across Mideast while Israel and US hit Tehran as tensions grow",
            source = "Top News",
            thumbnailUrl = "https://picsum.photos/seed/news_1/300/220",
            category = "Top"
        ),
        NewsItem(
            id = "2",
            title = "After crash, Tiger Woods told deputy he was talking to the president",
            source = "Politico",
            thumbnailUrl = "https://picsum.photos/seed/news_2/300/220",
            category = "Top"
        ),
        NewsItem(
            id = "3",
            title = "CNN host drops bombshell that led to political shake-up overnight",
            source = "The Mirror US Online",
            thumbnailUrl = "https://picsum.photos/seed/news_3/300/220",
            category = "U.S."
        ),
        NewsItem(
            id = "4",
            title = "The sentence that could sink Donald Trump's presidency this election season",
            source = "HuffPost",
            thumbnailUrl = "https://picsum.photos/seed/news_4/300/220",
            category = "U.S."
        ),
        NewsItem(
            id = "5",
            title = "Pundit explains reason for firing top general leaks in Washington",
            source = "The Daily Beast",
            thumbnailUrl = "https://picsum.photos/seed/news_5/300/220",
            category = "Local"
        ),
        NewsItem(
            id = "6",
            title = "Trump comments during event spark backlash as White House edits video",
            source = "Atlanta Black Star",
            thumbnailUrl = "https://picsum.photos/seed/news_6/300/220",
            category = "Entertainment"
        ),
        NewsItem(
            id = "7",
            title = "Global streaming show breaks records and dominates weekend charts",
            source = "The Guardian",
            thumbnailUrl = "https://picsum.photos/seed/news_7/300/220",
            category = "Lifestyle"
        ),
        NewsItem(
            id = "8",
            title = "NBA finals storyline shifts after dramatic overtime comeback",
            source = "ESPN",
            thumbnailUrl = "https://picsum.photos/seed/news_8/300/220",
            category = "Following"
        )
    )

    val trending = listOf(
        TrendingItem(1, "Artemis II", "NASA's crewed Orion spacecraft executed a key burn to depart Earth for the Moon.", "https://picsum.photos/seed/trend_1/200/140"),
        TrendingItem(2, "Pam Bondi Ousted", "President Trump fired Attorney General Pam Bondi and installed Deputy AG Todd Blanche.", "https://picsum.photos/seed/trend_2/200/140"),
        TrendingItem(3, "Trump Address", "Trump vowed harsher strikes on Iran while offering no firm end date for the conflict.", "https://picsum.photos/seed/trend_3/200/140"),
        TrendingItem(4, "SCOTUS hearing", "The Supreme Court weighed Trump's attempt to limit birthright citizenship.", "https://picsum.photos/seed/trend_4/200/140"),
        TrendingItem(5, "Tiger Woods", "Bodycam video shows Tiger Woods' DUI arrest after a rollover crash in Florida.", "https://picsum.photos/seed/trend_5/200/140"),
        TrendingItem(6, "SpaceX IPO", "SpaceX has confidentially filed for an IPO that could value the company at $1.75 trillion.", "https://picsum.photos/seed/trend_6/200/140"),
        TrendingItem(7, "Randy George", "Army Chief Gen. Randy George was forced to retire immediately.", "https://picsum.photos/seed/trend_7/200/140"),
        TrendingItem(8, "Blake Lively", "A federal judge dismissed most of Blake Lively's claims in high-profile lawsuit.", "https://picsum.photos/seed/trend_8/200/140")
    )
}

