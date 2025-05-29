package moe.styx.libs.mal.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MALUser(
    val id: Int,
    val name: String,
    val gender: String? = null,
    val birthday: String? = null,
    val location: String? = null,
    @SerialName("joined_at")
    val joinedAt: String,
    val picture: String? = null,
    @SerialName("anime_statistics")
    val animeStatistics: MALAnimeStatistics? = null,
)

@Serializable
data class MALAnimeStatistics(
    @SerialName("num_items_watching")
    val numItemsWatching: Int,
    @SerialName("num_items_completed")
    val numItemsCompleted: Int,
    @SerialName("num_items_on_hold")
    val numItemsOnHold: Int,
    @SerialName("num_items_dropped")
    val numItemsDropped: Int,
    @SerialName("num_items_plan_to_watch")
    val numItemsPlanToWatch: Int,
    @SerialName("num_items")
    val numItems: Int,
    @SerialName("num_days_watched")
    val numDaysWatched: Double,
    @SerialName("num_days_watching")
    val numDaysWatching: Double,
    @SerialName("num_days_completed")
    val numDaysCompleted: Double,
    @SerialName("num_days_on_hold")
    val numDaysOnHold: Double,
    @SerialName("num_days_dropped")
    val numDaysDropped: Double,
    @SerialName("num_days")
    val numDays: Double,
    @SerialName("num_episodes")
    val numEpisodes: Int,
    @SerialName("num_times_rewatched")
    val numTimesRewatched: Int,
    @SerialName("mean_score")
    val meanScore: Double
)