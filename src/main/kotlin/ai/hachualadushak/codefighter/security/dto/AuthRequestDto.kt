package ai.hachualadushak.codefighter.security.dto

import jakarta.validation.constraints.NotNull

data class AuthRequestDto(

    @field:NotNull(message = "Username must not be null")
    val login: String,

    @field:NotNull(message = "Password must not be null")
    val password: String

)
