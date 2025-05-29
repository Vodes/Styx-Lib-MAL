package moe.styx.libs.mal.ext.fetching

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import moe.styx.common.http.httpClient
import moe.styx.common.json
import moe.styx.libs.mal.AbstractMALApiClient
import moe.styx.libs.mal.RequestFields
import moe.styx.libs.mal.returnables.MALApiResponse
import moe.styx.libs.mal.types.MALMedia

suspend fun AbstractMALApiClient.searchMedia(
    query: String,
    limit: Int = 100,
    offset: Int = 0,
    idsIn: List<Int> = emptyList(),
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
        return MALApiResponse(emptyList(), response.status)
    }
    val jsonObj = json.decodeFromString<JsonObject>(body)
    val extracted = extractMediaNodesFromJson(jsonObj)
    if (idsIn.isEmpty()) {
        return MALApiResponse(extracted, response.status)
    }
    val mutable = extracted.toMutableList()
    idsIn.filter { id -> extracted.find { it.id == id } == null }.forEachIndexed { i, id ->
        if (idsIn.size > i + 1) {
            delay(750)
        }
        val result = fetchMediaDetails(id)
        if (result.isSuccess && result.data != null)
            mutable.add(result.data)
    }
    return MALApiResponse(mutable.toList(), response.status)
}

fun extractMediaNodesFromJson(obj: JsonObject): List<MALMedia> {
    return runCatching {
        val dataObj = obj["data"]!!.jsonArray
        val nodes = dataObj.map { it.jsonObject }
        nodes.map { json.decodeFromJsonElement<MALMedia>(it["node"]!!.jsonObject) }
    }.getOrNull() ?: emptyList()
}