package moe.styx.libs.mal.ext.fetching

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import moe.styx.common.http.httpClient
import moe.styx.common.json
import moe.styx.common.util.Log
import moe.styx.libs.mal.AbstractMALApiClient
import moe.styx.libs.mal.RequestFields
import moe.styx.libs.mal.returnables.MALApiResponse
import moe.styx.libs.mal.types.MALMedia

suspend fun AbstractMALApiClient.fetchMediaDetails(id: Int, fields: String = RequestFields.MEDIA_DETAILS_FIELDS): MALApiResponse<MALMedia?> {
    val response = doRequestWithRetry {
        val url = urlWithParameters("https://api.myanimelist.net/v2/anime/$id") {
            append("fields", fields)
        }
        httpClient.get(url) {
            bearerAuth(it.accessToken)
            accept(ContentType.Application.Json)
        }
    }
    val body = response.bodyAsText()
    if (!response.status.isSuccess()) {
        Log.e(this::class.simpleName) { "Failed to fetch media details! (${response.status.value})\nBody: $body" }
        return MALApiResponse(null, response.status)
    }
    return MALApiResponse(json.decodeFromString(body), response.status)
}