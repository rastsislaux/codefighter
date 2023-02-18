package ai.hachualadushak.codefighter

import ai.hachualadushak.codefighter.problem.Problem

fun Problem.toStartProblemDto() = StartProblemDto(
    id = this.id,
    name = this.name,
    description = this.description,
    template = this.template
)

fun Problem.toCheckResult(status: CheckStatus, stderr: String, duration: Long) = CheckTaskDto(
    id = this.id,
    name = this.name,
    description = this.description,
    template = this.template,
    status = status,
    stderr = stderr,
    duration = duration
)
