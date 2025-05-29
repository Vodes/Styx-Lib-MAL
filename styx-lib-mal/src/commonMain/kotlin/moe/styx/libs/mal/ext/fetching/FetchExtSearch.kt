package moe.styx.libs.mal.ext.fetching

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonObject
import moe.styx.common.http.httpClient
import moe.styx.common.json
import moe.styx.common.util.Log
import moe.styx.libs.mal.AbstractMALApiClient
import moe.styx.libs.mal.RequestFields
import moe.styx.libs.mal.returnables.MALApiResponse
import moe.styx.libs.mal.types.MALMedia

suspend fun AbstractMALApiClient.searchMedia(
    query: String,
    limit: Int = 100,
    offset: Int = 0,
    idsIn: List<Int> = emptyList(),
    filterOutUnnecessary: Boolean = false,
    fields: String = RequestFields.MEDIA_DETAILS_FIELDS
): MALApiResponse<List<MALMedia>> {
    val response = doRequestWithRetry {
        val url = urlWithParameters("https://api.myanimelist.net/v2/anime") {
            append("limit", limit.toString())
            append("offset", offset.toString())
            append("fields", fields)
            append("q", query)
        }
        httpClient.get(url) {
            bearerAuth(it.accessToken)
            accept(ContentType.Application.Json)
        }
    }
    val body = response.bodyAsText()
    if (!response.status.isSuccess()) {
        Log.e(this::class.simpleName) { "Failed to search media! (${response.status.value})\nBody: $body" }
        return MALApiResponse(emptyList(), response.status)
    }
    val jsonObj = json.decodeFromString<JsonObject>(body)
    val extracted = extractMediaNodesFromJson(jsonObj)
    if (idsIn.isEmpty()) {
        return MALApiResponse(extracted, response.status)
    }
    val mutable = extracted.toMutableList()
    val missing = idsIn.filter { id -> extracted.find { it.id == id } == null }
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