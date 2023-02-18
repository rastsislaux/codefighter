package ai.hachualadushak.codefighter

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class EntityNotFoundException(msg: String): RuntimeException(msg)

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
class TimeoutException(msg: String): RuntimeException(msg)
