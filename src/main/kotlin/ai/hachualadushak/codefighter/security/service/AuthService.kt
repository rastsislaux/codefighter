package ai.hachualadushak.codefighter.security.service

import java.time.LocalDateTime

interface AuthService {

    fun signIn(
        username: String,
        password: String
    ): Pair<Pair<String, LocalDateTime>, Pair<String, LocalDateTime>>

    fun refreshAuth(refreshToken: String?): Pair<Pair<String, LocalDateTime>, Pair<String, LocalDateTime>>

}
