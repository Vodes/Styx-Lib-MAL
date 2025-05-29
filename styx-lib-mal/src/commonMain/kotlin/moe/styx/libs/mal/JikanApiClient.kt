package moe.styx.libs.mal

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.*
import moe.styx.common.http.httpClient
import moe.styx.common.json
import moe.styx.common.util.Log
import moe.styx.libs.mal.returnables.MALApiResponse
import moe.styx.libs.mal.types.*

object JikanApiClient {
    internal fun urlWithParameters(url: String, params: ParametersBuilder.() -> Unit): String {
        return URLBuilder(url).apply {
            parameters.apply(params)
        }.buildString()
    }

    internal fun jsonObjToMALMedia(obj: JsonObject): MALMedia? {
        return runCatching {
            val mediumImage = obj["images"]?.jsonObject["webp"]?.jsonObject["image_url"]?.jsonPrimitive?.content
                ?: obj["images"]?.jsonObject["jpg"]?.jsonObject["image_url"]?.jsonPrimitive?.content!!
            val largeImage = obj["images"]?.jsonObject["webp"]?.jsonObject["large_image_url"]?.jsonPrimitive?.content
                ?: obj["images"]?.jsonObject["jpg"]?.jsonObject["large_image_url"]?.jsonPrimitive?.content
            val picture = MALMediaPicture(mediumImage, largeImage)

            val titles = MALAlternativeTitles(
                obj["title_synonyms"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
                obj["title_english"]?.jsonPrimitive?.content,
                obj["title_japanese"]?.jsonPrimitive?.content,
            )
            val genres = obj["genres"]?.jsonArray?.map { genreObj ->
                val genreObj = genreObj.jsonObject
                MALGenre(genreObj["mal_id"]!!.jsonPrimitive.intOrNull!!, genreObj["name"]!!.jsonPrimitive.content)
            }

            return MALMedia(
                obj["mal_id"]!!.jsonPrimitive.intOrNull!!,
                obj["title"]!!.jsonPrimitive.content,
                picture,
                synopsis = obj["synopsis"]?.jsonPrimitive?.content,
                numEpisodes = obj["episodes"]?.jsonPrimitive?.intOrNull,
                source = obj["source"]?.jsonPrimitive?.content,
                mediaType = obj["type"]?.jsonPrimitive?.content,
                alternativeTitles = titles,
                background = obj["background"]?.jsonPrimitive?.content,
                status = obj["status"]?.jsonPrimitive?.content,
                genres = genres ?: emptyList(),
                startSeason = obj["season"]?.let { seasonElement ->
                    obj["year"]?.let { yearElement ->
                        MALMediaSeason(yearElement.jsonPrimitive.intOrNull ?: 1900, seasonElement.jsonPrimitive.content)
                    }
                }
            )
        }.onFailure {
            Log.e(this::class.simpleName, it) { "Failed to convert Jikan response to MALMedia!" }
        }.getOrNull()
    }

    suspend fun fetchMediaDetails(id: Int): MALApiResponse<MALMedia?> {
        val response = httpClient.get("https://api.jikan.moe/v4/anime/$id")
        val body = response.bodyAsText()
        if (!response.status.isSuccess()) {
            Log.e(this::class.simpleName) { "Failed to search media! (${response.status.value})\nBody: $body" }
            return MALApiResponse(null, response.status)
        }
        val jsonObj = json.decodeFromString<JsonObject>(body)
        val dataObj = jsonObj["data"]!!.jsonObject
        val data = jsonObjToMALMedia(dataObj)
        if (data == null) {
            return MALApiResponse(null, HttpStatusCode.InternalServerError)
        }
        return MALApiResponse(data, response.status)
    }

    suspend fun searchMedia(
        query: String, limit: Int = 25, page: Int = 1, idsIn: List<Int> = emptyList(),
        filterOutUnnecessary: Boolean = false,
    ): MALApiResponse<List<MALMedia>> {
        val url = urlWithParameters("https://api.jikan.moe/v4/anime") {
            append("q", query)
            append("limit", limit.toString())
            append("page", page.toString())
        }
        val response = httpClient.get(url)
        val body = response.bodyAsText()
        if (!response.status.isSuccess()) {
            Log.e(this::class.simpleName) { "Failed to search media! (${response.status.value})\nBody: $body" }
            return MALApiResponse(emptyList(), response.status)
        }
        val jsonObj = json.decodeFromString<JsonObject>(body)
        val list = jsonObj["data"]!!.jsonArray.mapNotNull { jsonObjToMALMedia(it.jsonObject) }

        if (idsIn.isEmpty()) {
            return MALApiResponse(list, response.status)
        }

        val mutable = list.toMutableList()
        val missing = idsIn.filter { id -> list.find { it.id == id } == null }
        if (missing.isNotEmpty())
            Log.d(this::class.simpleName) { "The following IDs are missing after search: ${missing.joinToString()}\n\tFetching individually now." }
        missing.forEachIndexed { i, id ->
            if (idsIn.size > i + 1) {
                delay(750)
            }
            val result = fetchMediaDetails(id)
            if (result.isSuccess && result.data != null)
                mutable.add(result.data)
        }
        return MALApiResponse(if (!filterOutUnnecessary) mutable.toList() else mutable.filter { it.id in idsIn }, response.status)
    }
}