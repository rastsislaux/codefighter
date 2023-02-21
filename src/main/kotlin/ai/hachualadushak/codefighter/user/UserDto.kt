package ai.hachualadushak.codefighter.user

data class UserDto(
    val fullName: String,
    val login: String,
    val role: UserRole
)
