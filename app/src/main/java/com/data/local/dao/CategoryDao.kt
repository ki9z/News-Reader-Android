import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.myapplication.data.local.entity.*
@Dao
interface CategoryDao {

    @Insert
    suspend fun insert(category: Category) : Long

    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<Category>

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): Category?

    @Delete
    suspend fun delete(category: Category)
}
