package ai.hachualadushak.codefighter.security.config

import ai.hachualadushak.codefighter.ExceptionUtils
import ai.hachualadushak.codefighter.security.JwtTokenType
import ai.hachualadushak.codefighter.security.JwtUser
import ai.hachualadushak.codefighter.security.service.JwtService
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtException
import jakarta.security.auth.message.AuthException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    @Value("\${security.whitelist}")
    private val whitelist: Set<String>? = null

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            if (request.method == HttpMethod.OPTIONS.name()) {
                response.status = HttpServletResponse.SC_OK
                return
            }

            if (whitelist!!.stream().anyMatch { request.requestURI.contains(it!!) }) {
                filterChain.doFilter(request, response)
                return
            }

            SecurityContextHolder.getContext().authentication?.let {
                filterChain.doFilter(request, response)
                return
            }

            val requestTokenHeader = request.getHeader(AUTHORIZATION_HEADER)
            requireNotNull(requestTokenHeader) {
                throw AuthException(AUTH_TOKEN_REQUIRED_MESSAGE)
            }

            if (!requestTokenHeader.startsWith("Bearer ")) {
                throw AuthException(INVALID_TOKEN_MESSAGE)
            }

            val token = requestTokenHeader.substring(7)
            jwtService.checkTokenForValidity(token, JwtTokenType.ACCESS)
            val userDetails = userDetailsService.loadUserByUsername(jwtService.getUsernameFromToken(token)) as JwtUser
            val authenticationToken = UsernamePasswordAuthenticationToken(
                userDetails, null, listOf(SimpleGrantedAuthority(userDetails.role.toString()))
            )
            SecurityContextHolder.getContext().authentication = authenticationToken
            filterChain.doFilter(request, response)
        } catch (e: JwtException) {
            handleJwtException(e, response)
        } catch (e: AuthException) {
            handleAuthException(e, response)
        }
    }

    private fun handleJwtException(e: JwtException, response: ServletResponse) {
        val message = objectMapper.writeValueAsString(ExceptionUtils.makeErrorDto(e.message ?: "", HttpStatus.UNAUTHORIZED))
        sendUnauthorizedResponse(message, response)
    }

    private fun handleAuthException(e: AuthException, response: ServletResponse) {
        val message = objectMapper.writeValueAsString(ExceptionUtils.makeErrorDto(e.message ?: "", HttpStatus.UNAUTHORIZED))
        sendUnauthorizedResponse(message, response)
    }

    private fun sendUnauthorizedResponse(message: String, response: ServletResponse) {
        val unauthorizedResponse = response as HttpServletResponse
        unauthorizedResponse.status = HttpStatus.UNAUTHORIZED.value()
        unauthorizedResponse.setHeader("Content-Type", "application/json")
        response.getWriter().write(message)
        response.getWriter().flush()
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val AUTH_TOKEN_REQUIRED_MESSAGE = "Authorization token is required"
        private const val INVALID_TOKEN_MESSAGE = "Provided token is invalid"
    }
}

