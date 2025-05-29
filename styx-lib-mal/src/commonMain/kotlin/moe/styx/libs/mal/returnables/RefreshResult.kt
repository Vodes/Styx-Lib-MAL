package moe.styx.libs.mal.returnables

import kotlinx.serialization.Serializable
import moe.styx.common.data.MyAnimeListData

@Serializable
data class RefreshResult(val returnCode: Int, val malData: MyAnimeListData?) {

    val isSuccess: Boolean
        get() = returnCode in 200..299 && malData != null
}