import kotlinx.coroutines.runBlocking
import moe.styx.common.data.MyAnimeListData
import moe.styx.libs.mal.AbstractMALApiClient
import moe.styx.libs.mal.ext.fetching.fetchCurrentUser
import moe.styx.libs.mal.returnables.RefreshResult
import kotlin.test.Test
import kotlin.test.assertTrue

class TestUser() {

    private val client by lazy {
        val token = System.getenv()["MAL_CLIENT_TOKEN"]!!
        val malData = MyAnimeListData(accessToken = token, Long.MAX_VALUE, "", Long.MAX_VALUE, "", -1)
        object : AbstractMALApiClient(malData) {
            override suspend fun refreshToken(): RefreshResult {
                TODO("Not yet implemented")
            }
        }
    }

    @Test
    fun testUserFetch() = runBlocking {
        val result = client.fetchCurrentUser()
        assertTrue { result.isSuccess }
        assertTrue { result.data?.name == "Vodes" }
    }
}