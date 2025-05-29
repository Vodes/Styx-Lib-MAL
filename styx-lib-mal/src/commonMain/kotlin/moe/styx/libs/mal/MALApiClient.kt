package moe.styx.libs.mal

import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import moe.styx.common.data.MyAnimeListData
import moe.styx.common.extension.currentUnixSeconds
import moe.styx.common.json
import moe.styx.common.util.Log
import moe.styx.libs.mal.returnables.RefreshResult
import moe.styx.libs.mal.types.MALMedia

abstract class AbstractMALApiClient(var malData: MyAnimeListData, val maxAttempts: Int = 3) {
    abstract suspend fun refreshToken(): RefreshResult

    private suspend fun doRefresh(): RefreshResult {
        val result = refreshToken()
        if (result.isSuccess) {
            malData = result.malData!!
        }
        return RefreshResult(result.returnCode, malData)
    }

    internal suspend fun doRequestWithRetry(request: suspend (MyAnimeListData) -> HttpResponse): HttpResponse {
        var attempt = 0
        if (malData.accessTokenExpiry < currentUnixSeconds()) {
            doRefresh()
        }
        var response: HttpResponse = request(malData)
        if (response.status.isSuccess()) {
            return response
        }
        while (attempt++ < maxAttempts) {
            if (response.status in arrayOf(HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden)) {
                if (!doRefresh().isSuccess)
                    break
            } else if (response.status == HttpStatusCode.TooManyRequests) {
                delay(1500)
            }
            response = request(malData)
            if (response.status.isSuccess())
                break
        }
        return response
    }

    internal fun urlWithParameters(url: String, params: ParametersBuilder.() -> Unit): String {
        return URLBuilder(url).apply {
            parameters.apply(params)
        }.buildString()
    }

    internal fun extractMediaNodesFromJson(obj: JsonObject): List<MALMedia> {
        return runCatching {
            val dataObj = obj["data"]!!.jsonArray
            val nodes = dataObj.map { it.jsonObject }
            nodes.map { json.decodeFromJsonElement<MALMedia>(it["node"]!!.jsonObject) }
        }.onFailure {
            Log.e(this::class.simpleName, it) { "Failed to extract media nodes from this json:\n$obj" }
        }.getOrNull() ?: emptyList()
    }
}