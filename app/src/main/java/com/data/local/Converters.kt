import com.example.myapplication.data.local.entity.*
import androidx.room.TypeConverter
import com.example.myapplication.data.domain.*
class Converters {
    @TypeConverter
    fun fromStatus(status: ArticleStatus): String {
        return status.name.lowercase()
    }

    @TypeConverter
    fun toStatus(value: String): ArticleStatus {
        return ArticleStatus.valueOf(value.uppercase())
    }
}
