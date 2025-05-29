package moe.styx.libs.mal.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MALListStatus(
    val status: String,
    val score: Int? = null,
    @SerialName("num_episodes_watched")
    val watchedEpisodes: Int? = null,
    @SerialName("is_rewatching")
    val isRewatching: Boolean = false,
    @SerialName("updated_at")
    val updatedAt: String? = null
)