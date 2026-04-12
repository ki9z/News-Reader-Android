import com.example.myapplication.data.local.entity.*
import androidx.room.*
data class UserWithCategories(
    @Embedded val user: User,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CategoryUser::class,
            parentColumn = "userId",
            entityColumn = "categoryId"
        )
    )
    val categories: List<Category>
)
