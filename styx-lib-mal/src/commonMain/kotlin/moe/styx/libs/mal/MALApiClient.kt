package moe.styx.libs.mal

import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import moe.styx.common.data.MyAnimeListData
import moe.styx.common.extension.currentUnixSeconds
import moe.styx.libs.mal.returnables.RefreshResult

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
}