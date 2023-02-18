package ai.hachualadushak.codefighter.security.dto

import java.time.LocalDateTime

data class AuthResponseDto(
    val userId: String,
    val token: String,
    val expiresAt: LocalDateTime,
    val refreshToken: String,
    val refreshTokenExpiresAt: LocalDateTime
)
