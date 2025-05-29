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
import moe.styx.libs.mal.types.MALUser

suspend fun AbstractMALApiClient.fetchCurrentUser(): MALApiResponse<MALUser?> {
    val url = urlWithParameters("https://api.myanimelist.net/v2/users/@me") {
        append("fields", RequestFields.USER_FIELDS)
    }
    val response = httpClient.get(url) {
        bearerAuth(malData.accessToken)
        accept(ContentType.Application.Json)
    }
    val body = response.bodyAsText()
    if (!response.status.isSuccess()) {
        Log.e(this::class.simpleName) { "Failed to fetch authenticated user! (${response.status.value})\nBody: $body" }
        return MALApiResponse(null, response.status)
    }
    val user = json.decodeFromString<MALUser>(body)
    return MALApiResponse(user, response.status)
}