package moe.styx.libs.mal.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MALOAuthResult(
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String
)