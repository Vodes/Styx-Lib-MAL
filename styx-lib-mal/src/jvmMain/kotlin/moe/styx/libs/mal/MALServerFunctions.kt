package moe.styx.libs.mal

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import moe.styx.common.data.MyAnimeListData
import moe.styx.common.extension.currentUnixSeconds
import moe.styx.common.http.httpClient
import moe.styx.common.json
import moe.styx.libs.mal.returnables.MALApiResponse
import moe.styx.libs.mal.types.MALOAuthResult
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object MALServerFunctions {
    
    suspend fun refreshToken(refreshToken: String, clientID: String, clientSecret: String): MALApiResponse<MyAnimeListData?> {
        val response = httpClient.submitForm("https://myanimelist.net/v1/oauth2/token", formParameters = parameters {
            append("grant_type", "refresh_token")
            append("refresh_token", refreshToken)
        }) {
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
            basicAuth(clientID, clientSecret)
            method = HttpMethod.Post
        }
        if (!response.status.isSuccess()) {
            return MALApiResponse(null, response.status)
        }
        val oauthData = json.decodeFromString<MALOAuthResult>(response.bodyAsText())
        val refreshTokenDuration = (6 * 30).toDuration(DurationUnit.DAYS).inWholeSeconds
        val now = currentUnixSeconds()
        val newMalData = MyAnimeListData(
            oauthData.accessToken,
            now + oauthData.expiresIn,
            oauthData.refreshToken,
            now + refreshTokenDuration,
            "",
            -1
        )
        return MALApiResponse(newMalData, response.status)
    }
}