import com.example.myapplication.data.remote.dto.ArticleJson
import com.example.myapplication.data.remote.dto.BlockJson
import com.example.myapplication.data.local.entity.Article
import com.example.myapplication.data.local.entity.Block
import com.example.myapplication.data.utils.toTimestamp
import com.example.myapplication.data.domain.*
object ArticleMapper {

    fun toArticleEntity(
        json: ArticleJson,
        categoryId: Long
    ): Article {
        return Article(
            remoteId = json.id.trim(),
            title = json.title.trim(),
            author = json.author?.trim().orEmpty(),
            source = json.source?.trim().orEmpty(),
            categoryId = categoryId,
            publishAt = json.time.toTimestamp(),
            createdAt = System.currentTimeMillis(),
            view = 0,
            status = ArticleStatus.PUBLISHED,
            url = json.url?.trim().orEmpty()
        )
    }

    fun toBlockEntities(
        blocks: List<BlockJson>,
        articleId: Long
    ): List<Block> {
        return blocks.mapIndexed { index, b ->
            Block(
                articleId = articleId,
                type = b.type,
                content = b.data?: "",
                imageUrl = b.url,
                caption = b.caption,
                position = index.toLong()
            )
        }
    }
}
