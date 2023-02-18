package ai.hachualadushak.codefighter.user

import ai.hachualadushak.codefighter.security.JwtUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    val passwordEncoder: PasswordEncoder,
    val userRepository: UserRepository
) {

    fun findByLogin(login: String) = userRepository.findByLogin(login)

    fun getCurrent(): User {
        val jwtUser = SecurityContextHolder.getContext().authentication.principal as JwtUser
        return userRepository.findByLogin(jwtUser.login)
    }

    fun create(user: User, password: String): User {
        return create(user, password, role = user.role!!)
    }

    fun create(user: User, password: String, role: UserRole): User {
        user.role = role
        user.passwordHash = passwordEncoder.encode(password)
        return userRepository.save(user)
    }

}
