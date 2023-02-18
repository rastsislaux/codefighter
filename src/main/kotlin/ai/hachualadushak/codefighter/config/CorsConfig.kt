package ai.hachualadushak.codefighter.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val configuration = CorsConfiguration()

        configuration.allowedOriginPatterns = listOf(WILDCARD)
        configuration.allowedMethods = ALLOWED_METHODS
        configuration.allowedHeaders = listOf(WILDCARD)
        configuration.allowCredentials = true

        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    companion object {
        private const val WILDCARD = "*"
        private val ALLOWED_METHODS = HttpMethod.values().map { it.name() }
    }

}
