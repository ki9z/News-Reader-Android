import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import com.example.myapplication.data.local.relation.UserWithArticles
import com.example.myapplication.data.local.entity.*
@Dao
interface ArticleUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticle(articleUser: ArticleUser): Long

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithArticles(userId: Int): UserWithArticles?

    @Delete
    suspend fun delete(articleUser: ArticleUser)
}
