package ai.hachualadushak.codefighter.user

import ai.hachualadushak.codefighter.toUserDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    val userService: UserService
) {

    @GetMapping("/me")
    fun getCurrent() = userService.getCurrent().toUserDto()

}
