package ai.hachualadushak.codefighter.security.service


import ai.hachualadushak.codefighter.security.SecurityConverter
import ai.hachualadushak.codefighter.user.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(
    private val userService: UserService
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userService.findByLogin(username)
        return SecurityConverter.toJwtUser(user)
    }

}
