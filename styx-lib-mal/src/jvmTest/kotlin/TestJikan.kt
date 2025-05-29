import kotlinx.coroutines.runBlocking
import moe.styx.common.util.Log
import moe.styx.libs.mal.JikanApiClient
import kotlin.test.Test
import kotlin.test.assertTrue

class TestJikan() {
    init {
        Log.debugEnabled = true
    }

    @Test
    fun testSearch() = runBlocking {
        val result = JikanApiClient.searchMedia("One")
        assertTrue { result.isSuccess }
        assertTrue { result.data.find { it.title == "One Piece" } != null }
    }

    @Test
    fun testSingleFetch() = runBlocking {
        val result = JikanApiClient.fetchMediaDetails(1)
        assertTrue { result.isSuccess }
        assertTrue { result.data?.title == "Cowboy Bebop" }
    }

    @Test
    fun testSearchWithUnrelatedID() = runBlocking {
        val result = JikanApiClient.searchMedia("One Piece", limit = 5, idsIn = listOf(21, 37430))
        assertTrue { result.isSuccess }
        assertTrue { result.data.find { it.title == "Tensei shitara Slime Datta Ken" } != null }
    }
}