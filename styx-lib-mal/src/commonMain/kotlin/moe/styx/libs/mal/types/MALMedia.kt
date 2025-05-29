package moe.styx.libs.mal.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MALMedia(
    val id: Int,
    val title: String,
    @SerialName("main_picture")
    val mainPicture: MALMediaPicture,
    @SerialName("alternative_titles")
    val alternativeTitles: MALAlternativeTitles? = null,
    @SerialName("media_type")
    val mediaType: String? = null,
    @SerialName("start_date")
    val startDate: String? = null,
    @SerialName("end_date")
    val endDate: String? = null,
    val synopsis: String? = null,
    @SerialName("num_episodes")
    val numEpisodes: Int? = null,
    @SerialName("start_season")
    val startSeason: MALMediaSeason? = null,
    val status: String? = null,
    val genres: List<MALGenre> = emptyList(),
    val source: String? = null,
    val background: String? = null,
    @SerialName("my_list_status")
    val listStatus: MALListStatus? = null,
    @SerialName("related_anime")
    val relatedMedia: List<MALRelatedNode> = emptyList(),
)

@Serializable
data class MALRelatedNode(
    @SerialName("node")
    val media: MALMedia,
    @SerialName("relation_type")
    val relationType: String,
    @SerialName("relation_type_formatted")
    val relationTypeFormatted: String? = null
)