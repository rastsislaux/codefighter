package ai.hachualadushak.codefighter.security.service

import ai.hachualadushak.codefighter.EntityNotFoundException
import ai.hachualadushak.codefighter.security.JwtTokenType
import ai.hachualadushak.codefighter.security.JwtUser
import ai.hachualadushak.codefighter.security.SecurityConverter
import ai.hachualadushak.codefighter.user.User
import ai.hachualadushak.codefighter.user.UserService
import jakarta.security.auth.message.AuthException
import java.time.LocalDateTime
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthServiceImpl(
    private val jwtService: JwtService,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
) : AuthService {

    override fun signIn(
        username: String,
        password: String
    ): Pair<Pair<String, LocalDateTime>, Pair<String, LocalDateTime>> {
        val user: User
        try {
            user = userService.findByLogin(username)
        } catch (e: EntityNotFoundException) {
            throw AuthException(INVALID_CREDENTIALS_MESSAGE)
        }
        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw AuthException(INVALID_CREDENTIALS_MESSAGE)
        }
        val jwtUser = SecurityConverter.toJwtUser(user)
        return generateTokens(jwtUser)
    }

    override fun refreshAuth(refreshToken: String?): Pair<Pair<String, LocalDateTime>, Pair<String, LocalDateTime>> {
        jwtService.checkTokenForValidity(refreshToken!!, JwtTokenType.REFRESH)
        val username = jwtService.getUsernameFromToken(refreshToken)
        val jwtUser = SecurityConverter.toJwtUser(userService.findByLogin(username))
        return generateTokens(jwtUser)
    }

    private fun generateTokens(jwtUser: JwtUser): Pair<Pair<String, LocalDateTime>, Pair<String, LocalDateTime>> {
        val token = jwtService.generateToken(jwtUser, JwtTokenType.ACCESS)
        val newRefreshToken = jwtService.generateToken(jwtUser, JwtTokenType.REFRESH)
        val expiresAt = SecurityConverter.toLocalDateTime(jwtService.getExpirationDateFromToken(token))
        val refreshExpiresAt =
            SecurityConverter.toLocalDateTime(jwtService.getExpirationDateFromToken(newRefreshToken))
        return Pair(Pair(token, expiresAt), Pair(newRefreshToken, refreshExpiresAt))
    }

    companion object {
        private const val INVALID_CREDENTIALS_MESSAGE = "Invalid credentials"
    }

}
