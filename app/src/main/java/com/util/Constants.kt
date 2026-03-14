package com.util

object Constants {
    const val BASE_URL = "https://newsapi.org/"
    // Replace with your News API key from https://newsapi.org/
    const val API_KEY = "demo_key"

    const val DEFAULT_PAGE_SIZE = 20
    const val INITIAL_PAGE = 1

    const val CATEGORY_GENERAL = "general"
    const val CATEGORY_BUSINESS = "business"
    const val CATEGORY_ENTERTAINMENT = "entertainment"
    const val CATEGORY_HEALTH = "health"
    const val CATEGORY_SCIENCE = "science"
    const val CATEGORY_SPORTS = "sports"
    const val CATEGORY_TECHNOLOGY = "technology"

    val CATEGORIES = listOf(
        CATEGORY_GENERAL,
        CATEGORY_BUSINESS,
        CATEGORY_ENTERTAINMENT,
        CATEGORY_HEALTH,
        CATEGORY_SCIENCE,
        CATEGORY_SPORTS,
        CATEGORY_TECHNOLOGY
    )
}
