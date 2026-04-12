object DatabaseInitializer {
    suspend fun init(context: Context, db: AppDatabase) {
        val repo = ArticleRepository(db)
        val jsonFiles = listOf(
            "news1.json",
            "news2.json",
            "news3.json",
            "news4.json"
        )
        jsonFiles.forEach { fileName ->
            val json = JsonReader.readFromAssets(context, fileName)
            repo.insertFromJson(json)
        }
    }
}
