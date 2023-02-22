package ai.hachualadushak.codefighter.security

import ai.hachualadushak.codefighter.security.dto.AuthResponseDto
import ai.hachualadushak.codefighter.user.User
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class SecurityConverter {
    companion object {

        fun toJwtUser(user: User): JwtUser {
            return JwtUser(
                id = user.id!!,
                login = user.login!!,
                fullName = user.fullName!!,
                role = user.role!!
            )
        }

        fun toLocalDateTime(date: Date): LocalDateTime {
            return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }

        fun toAuthResponseDto(tokens: Pair<Pair<String, LocalDateTime>, Pair<String, LocalDateTime>>, userId: String): AuthResponseDto {
            return AuthResponseDto(
                userId = userId,
                token = tokens.first.first,
                expiresAt = tokens.first.second,
                refreshToken = tokens.second.first,
                refreshTokenExpiresAt = tokens.second.second,
            )
        }

    }
}
