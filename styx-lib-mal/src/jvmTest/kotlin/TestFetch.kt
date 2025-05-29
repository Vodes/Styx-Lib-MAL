import kotlinx.coroutines.runBlocking
import moe.styx.common.data.MyAnimeListData
import moe.styx.libs.mal.AbstractMALApiClient
import moe.styx.libs.mal.ext.fetching.fetchMediaDetails
import moe.styx.libs.mal.ext.fetching.searchMedia
import moe.styx.libs.mal.returnables.RefreshResult
import kotlin.test.Test
import kotlin.test.assertTrue

class TestFetch() {

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
    fun testSearch() = runBlocking {
        val result = client.searchMedia("One")
        assertTrue { result.isSuccess }
        assertTrue { result.data.find { it.title == "One Piece" } != null }
    }

    @Test
    fun testSearchWithUnrelatedID() = runBlocking {
        val result = client.searchMedia("One Piece", limit = 5, idsIn = listOf(21, 37430))
        assertTrue { result.isSuccess }
        assertTrue { result.data.find { it.title == "Tensei shitara Slime Datta Ken" } != null }
    }

    @Test
    fun testSingleFetch() = runBlocking {
        val result = client.fetchMediaDetails(1)
        assertTrue { result.isSuccess }
        assertTrue { result.data?.title == "Cowboy Bebop" }
    }
}