import androidx.room.withTransaction
import android.util.Log
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.entity.*
import com.example.myapplication.data.local.dao.*
import com.example.myapplication.data.mapper.ArticleMapper
import com.example.myapplication.data.remote.JsonParser
import com.example.myapplication.data.remote.model.ArticleJson

import kotlinx.serialization.decodeFromString
class ArticleRepository(
    private val db: AppDatabase
) {

    suspend fun insertFromJson(jsonString: String) {
        if (jsonString.isBlank()) {
            Log.e("JSON_ERROR", "JSON STRING IS EMPTY")
            return
        } else {
            Log.d("JSON_OK", jsonString.take(200))
        }

        val articles = try {
            JsonParser.json.decodeFromString<List<ArticleJson>>(jsonString)
        } catch (e: Exception) {
            Log.e("JSON_PARSE_ERROR", e.message ?: "Unknown error")
            return
        }

        db.withTransaction {

            val categoryDao = db.categoryDao()
            val articleDao = db.articleDao()
            val blockDao = db.blockDao()
            val sourceDao = db.SourceDao()
            articles.forEach { articleJson ->

                val existing = categoryDao.findByName(articleJson.category)
                val existing2 = sourceDao.findByName(articleJson.source)
                val categoryId = existing?.id
                    ?: categoryDao.insert(
                        Category(name = articleJson.category)
                    )
                val sourceId = existing2?.id
                    ?: sourceDao.insert(
                        Source(name = articleJson.source)
                    )
                val articleId = articleDao.insert(
                    ArticleMapper.toArticleEntity(articleJson, categoryId,sourceId)
                )
                if (articleId == -1L) return@forEach
                val blocks = ArticleMapper.toBlockEntities(
                    articleJson.blocks,
                    articleId
                )

                blockDao.insertAll(blocks)
            }
        }
    }
}
