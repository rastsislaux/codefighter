package ai.hachualadushak.codefighter.user

import ai.hachualadushak.codefighter.security.JwtUser
import ai.hachualadushak.codefighter.security.SecurityConverter
import org.springframework.security.core.context.SecurityContextHolder

class User(
    var id: String?,
    var login: String?,
    var passwordHash: String?,
    var fullName: String?,
    var role: UserRole?
)
