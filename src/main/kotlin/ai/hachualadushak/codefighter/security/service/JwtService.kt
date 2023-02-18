package ai.hachualadushak.codefighter.security.service

import ai.hachualadushak.codefighter.security.JwtTokenType
import ai.hachualadushak.codefighter.security.JwtUser
import ai.hachualadushak.codefighter.user.UserRole
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import java.security.Key
import java.security.SignatureException
import java.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class JwtService {
    @Value("\${security.jwt.secret}")
    private val jwtTokenSecretString: String? = null

    @Value("\${security.jwt.lifetime}")
    private val jwtTokenLifetime: Long = 0
    private var jwtTokenSecret: Key? = null
    private var jwtParser: JwtParser? = null

    @PostConstruct
    fun init() {
        jwtTokenSecret = Keys.hmacShaKeyFor(jwtTokenSecretString!!.toByteArray())
        jwtParser = Jwts.parserBuilder()
            .setSigningKey(jwtTokenSecret)
            .build()
    }

    fun getUsernameFromToken(token: String): String {
        val username: String? = getClaims(token).subject

        requireNotNull(username) {
            throw JwtException("Username is not present")
        }

        return username
    }

    fun getExpirationDateFromToken(token: String): Date {
        val expirationDate: Date? = getClaims(token).expiration

        requireNotNull(expirationDate) {
            throw JwtException("Expiration date is not present")
        }

        return expirationDate
    }

    fun getTypeFromToken(token: String): JwtTokenType {
        val type: String? = getClaims(token).get(TYPE_KEY, String::class.java)

        requireNotNull(type) {
            throw JwtException("Token type is not present")
        }

        return JwtTokenType.valueOf(type)
    }

    fun getRoleFromToken(token: String): UserRole {
        val role: String? = getClaims(token).get(ROLE_KEY, String::class.java)

        requireNotNull(role) {
            throw JwtException("Role is not present")
        }

        return UserRole.valueOf(role)
    }

    fun checkTokenForValidity(token: String, type: JwtTokenType) {
        getUsernameFromToken(token)
        getExpirationDateFromToken(token)
        if (type != getTypeFromToken(token)) {
            throw JwtException("Wrong token type")
        }
    }

    fun generateToken(jwtUser: JwtUser, type: JwtTokenType?): String {
        return Jwts.builder()
            .setClaims(HashMap<String, Any>())
            .setSubject(jwtUser.username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + jwtTokenLifetime * MILLISECONDS_IN_SECOND))
            .claim(TYPE_KEY, type)
            .claim(ROLE_KEY, jwtUser.role)
            .signWith(jwtTokenSecret)
            .compact()
    }

    private fun getClaims(token: String): Claims {
        return try {
            jwtParser!!.parseClaimsJws(token).body
        } catch (e: MalformedJwtException) {
            throw JwtException("Malformed JWT")
        } catch (e: ExpiredJwtException) {
            throw JwtException("Expired JWT")
        } catch (e: SignatureException) {
            throw JwtException("Invalid JWT signature")
        }
    }

    companion object {
        private const val TYPE_KEY = "typ"
        private const val ROLE_KEY = "rol"
        private const val MILLISECONDS_IN_SECOND = 1000L
    }
}
