import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import com.example.myapplication.data.local.entity.*

@Dao
interface ArticleDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(article: Article) : Long

    @Query("SELECT * FROM articles WHERE remoteId = :remoteId LIMIT 1")
    suspend fun findByRemoteId(remoteId: String): Article?

    @Query("SELECT * FROM articles WHERE categoryId = :categoryId")
    suspend fun getByCategory(categoryId: Long): List<Article>

    @Query("SELECT * FROM articles")
    suspend fun getAll(): List<Article>

    @Delete
    suspend fun delete(article: Article)

    @Update
    suspend fun update(article: Article)
}
