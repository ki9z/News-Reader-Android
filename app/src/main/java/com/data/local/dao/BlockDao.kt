import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.myapplication.data.local.entity.*
@Dao
interface BlockDao {

    @Insert
    suspend fun insert(block: Block) : Long

    @Insert
    suspend fun insertAll(blocks: List<Block>) : List<Long>

    @Query("SELECT * FROM blocks WHERE articleId = :articleId ORDER BY position ASC")
    suspend fun getByArticle(articleId: Int): List<Block>
}
