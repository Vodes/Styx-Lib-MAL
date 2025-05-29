import kotlinx.coroutines.runBlocking
import moe.styx.common.data.MyAnimeListData
import moe.styx.libs.mal.AbstractMALApiClient
import moe.styx.libs.mal.MALServerFunctions
import moe.styx.libs.mal.ext.fetching.searchMedia
import moe.styx.libs.mal.returnables.RefreshResult
import kotlin.test.Test
import kotlin.test.assertTrue


class TestRefresh() {

    private val client by lazy {
        val token = System.getenv()["MAL_REFRESH_TOKEN"]!!
        val clientID = System.getenv()["MAL_CLIENT_ID"]!!
        val clientSecret = System.getenv()["MAL_CLIENT_SECRET"]!!
        val malData = MyAnimeListData("TEST", Long.MAX_VALUE, token, Long.MAX_VALUE, "", -1)
        object : AbstractMALApiClient(malData) {
            override suspend fun refreshToken(): RefreshResult {
                val result = MALServerFunctions.refreshToken(malData.refreshToken, clientID, clientSecret)
                return RefreshResult(result.returnCode.value, result.data)
            }
        }
    }

    @Test
    fun testSearchWithRefresh() = runBlocking {
        val result = client.searchMedia("One")
        assertTrue { result.isSuccess }
        assertTrue { result.data.find { it.title == "One Piece" } != null }
    }
}