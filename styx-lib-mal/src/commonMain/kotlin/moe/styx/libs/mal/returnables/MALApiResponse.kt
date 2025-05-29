package moe.styx.libs.mal.returnables

import io.ktor.http.*

data class MALApiResponse<T>(val data: T, val returnCode: HttpStatusCode) {
    val isSuccess: Boolean
        get() = returnCode.isSuccess()
    val readableMessage: String
        get() =
            when (returnCode) {
                HttpStatusCode.Forbidden, HttpStatusCode.Unauthorized -> "Could not authorize your MAL access. Please report this to the admin."
                HttpStatusCode.NotFound -> "Could not find requested resource!"
                HttpStatusCode.TooManyRequests -> "You have been rate-limited. Please wait a few minutes before retrying."
                else -> ""
            }
}