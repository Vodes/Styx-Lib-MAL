import kotlinx.coroutines.runBlocking
import moe.styx.common.data.MyAnimeListData
import moe.styx.libs.mal.AbstractMALApiClient
import moe.styx.libs.mal.ext.update.deleteMediaListEntry
import moe.styx.libs.mal.ext.update.saveMediaListEntry
import moe.styx.libs.mal.returnables.RefreshResult
import kotlin.test.Test
import kotlin.test.assertTrue

class TestMediaListUpdate() {

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
    fun testMediaListUpdate() = runBlocking {
        val update = client.saveMediaListEntry(41488, "watching", 2)
        assertTrue { update.isSuccess }
        assertTrue { update.data?.status == "watching" }
        assertTrue { update.data?.watchedEpisodes == 2 }

        val delete = client.deleteMediaListEntry(41488)
        assertTrue { delete }
    }
}