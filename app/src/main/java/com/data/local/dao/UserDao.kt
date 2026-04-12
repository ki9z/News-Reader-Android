import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.myapplication.data.local.entity.*
@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User) :Long

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Int): User?

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)
}
