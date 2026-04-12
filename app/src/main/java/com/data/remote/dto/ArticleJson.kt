import kotlinx.serialization.Serializable
@Serializable
data class ArticleJson(
    val category: String,
    val source: String,
    val author: String,
    val title: String,
    val time: String,
    val id: String,
    val url: String,
    val blocks: List<BlockJson> = emptyList()

)
