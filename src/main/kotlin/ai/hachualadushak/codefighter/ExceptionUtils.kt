package ai.hachualadushak.codefighter

import java.time.LocalDateTime
import org.springframework.http.HttpStatus

object ExceptionUtils {
    fun makeErrorDto(message: String, status: HttpStatus): ErrorDto {
        return ErrorDto(
            timestamp = LocalDateTime.now(),
            responseStatus = status,
            errorMessage = message
        )
    }
}
