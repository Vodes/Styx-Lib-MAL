package moe.styx.libs.mal.types

import kotlinx.serialization.Serializable

@Serializable
data class MALMediaPicture(val medium: String, val large: String?)

@Serializable
data class MALMediaSeason(val year: Int, val season: String? = null)

@Serializable
data class MALGenre(val id: Int, val name: String)

@Serializable
data class MALAlternativeTitles(val synonyms: List<String> = emptyList(), val en: String? = null, val ja: String? = null)