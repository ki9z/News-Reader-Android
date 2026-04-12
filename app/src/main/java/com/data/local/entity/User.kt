import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val fullname: String,
    val username: String,
    val password: String,
    val role: String,
    val createdAt: Long,
    val phone: String?
)
