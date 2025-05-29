package moe.styx.libs.mal.ext.update

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import moe.styx.common.http.httpClient
import moe.styx.common.json
import moe.styx.libs.mal.AbstractMALApiClient
import moe.styx.libs.mal.returnables.MALApiResponse
import moe.styx.libs.mal.types.MALListStatus

suspend fun AbstractMALApiClient.saveMediaListEntry(
    id: Int,
    status: String,
    episodes: Int,
    score: Int? = null,
    isRewatching: Boolean = false
): MALApiResponse<MALListStatus?> {
    val response = doRequestWithRetry { auth ->
        httpClient.submitForm("https://api.myanimelist.net/v2/anime/$id/my_list_status", formParameters = parameters {
            append("status", status)
            append("num_watched_episodes", episodes.toString())
            score?.let { append("score", it.toString()) }
            append("is_rewatching", isRewatching.toString())
        }) {
            bearerAuth(auth.accessToken)
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
            method = HttpMethod.Put
        }
    }
    val body = response.bodyAsText()
    if (!response.status.isSuccess()) {
        return MALApiResponse(null, response.status)
    }
    return MALApiResponse(json.decodeFromString(body), response.status)
}

suspend fun AbstractMALApiClient.deleteMediaListEntry(id: Int): Boolean {
    val response = doRequestWithRetry {
        httpClient.delete("https://api.myanimelist.net/v2/anime/$id/my_list_status") {
            bearerAuth(it.accessToken)
        }
    }
    return response.status.isSuccess()
}