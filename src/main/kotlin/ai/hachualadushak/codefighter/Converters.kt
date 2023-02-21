package ai.hachualadushak.codefighter

import ai.hachualadushak.codefighter.problem.Problem
import ai.hachualadushak.codefighter.user.User
import ai.hachualadushak.codefighter.user.UserDto

fun Problem.toStartProblemDto() = StartProblemDto(
    id = this.id,
    name = this.name,
    description = this.description,
    template = this.template
)

fun Problem.toCheckResult(status: CheckStatus, stderr: String) = CheckTaskDto(
    id = this.id,
    name = this.name,
    description = this.description,
    template = this.template,
    status = status,
    stderr = stderr,
)

fun User.toUserDto() = UserDto(
    login = this.login!!,
    fullName = this.fullName!!,
    role = this.role!!
)
