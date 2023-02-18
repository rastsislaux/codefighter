package ai.hachualadushak.codefighter.security

import ai.hachualadushak.codefighter.security.dto.AuthRequestDto
import ai.hachualadushak.codefighter.security.dto.AuthResponseDto
import ai.hachualadushak.codefighter.security.service.AuthService
import ai.hachualadushak.codefighter.user.User
import ai.hachualadushak.codefighter.user.UserRole
import ai.hachualadushak.codefighter.user.UserService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    val authService: AuthService,
    val userService: UserService
) {

    @PostMapping
    @Operation(summary = "Sign in")
    fun signIn(@RequestBody @Validated authRequestDto: AuthRequestDto): ResponseEntity<AuthResponseDto> {
        val tokens = authService.signIn(
            authRequestDto.login,
            authRequestDto.password
        )
        return ResponseEntity.ok(SecurityConverter.toAuthResponseDto(tokens, authRequestDto.login))
    }

    @PostMapping("/register")
    @Operation(summary = "Sign up")
    fun signUp(@RequestBody @Validated user: User,
               @RequestParam("password") password: String): AuthResponseDto {
        userService.create(user, password, UserRole.USER)
        val tokens = authService.signIn(user.login!!, password)

        return SecurityConverter.toAuthResponseDto(tokens, user.login!!)
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token")
    fun refreshAuth(@RequestBody @NotBlank(message = "Refresh token must not be blank") refreshToken: String?): AuthResponseDto {
        val tokens = authService.refreshAuth(refreshToken)
        return SecurityConverter.toAuthResponseDto(tokens, "")
    }

}
