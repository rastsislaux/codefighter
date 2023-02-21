package ai.hachualadushak.codefighter

import java.time.LocalDateTime
import org.springframework.http.HttpStatus

data class StartProblemDto(
    val id: Long,
    val name: String,
    val description: String,
    val template: String,
    val startTime: LocalDateTime = LocalDateTime.now()
)

data class CheckTaskDto(
    val id: Long,
    val name: String,
    val description: String,
    val template: String,
    val status: CheckStatus,
    val stderr: String,
    val checkTime: LocalDateTime = LocalDateTime.now()
)

data class ErrorDto(
    val timestamp: LocalDateTime,
    val errorMessage: String,
    val responseStatus: HttpStatus
)
