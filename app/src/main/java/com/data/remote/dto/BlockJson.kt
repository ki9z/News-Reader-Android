import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
@Serializable
data class BlockJson(
    val type: String,
    val data: String? = null,
    val url: String? = null,
    val caption: String? = null
)
