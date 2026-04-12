import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Delete
import com.example.myapplication.data.local.entity.*
import androidx.room.OnConflictStrategy
import com.example.myapplication.data.local.relation.UserWithCategories
@Dao
interface CategoryUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryUser: CategoryUser): Long

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithCategories(userId: Int): UserWithCategories?

    @Delete
    suspend fun delete(categoryUser: CategoryUser)
}
