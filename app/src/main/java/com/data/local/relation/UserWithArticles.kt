import androidx.room.*
import com.example.myapplication.data.local.entity.*

data class UserWithArticles(
    @Embedded val user: User,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ArticleUser::class,
            parentColumn = "userId",
            entityColumn = "articleId"
        )
    )
    val articles: List<Article>
)
