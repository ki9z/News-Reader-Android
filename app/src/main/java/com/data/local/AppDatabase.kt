import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import androidx.room.RoomDatabase
import com.example.myapplication.data.local.entity.*
import com.example.myapplication.data.local.dao.*
import android.content.Context
@TypeConverters(Converters::class)
@Database(
    entities = [
        Category::class,
        User::class,
        Article::class,
        Block::class,
        ArticleUser::class,
        CategoryUser::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
    abstract fun articleDao(): ArticleDao
    abstract fun blockDao(): BlockDao
    abstract fun articleUserDao(): ArticleUserDao
    abstract fun categoryUserDao(): CategoryUserDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "news_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
