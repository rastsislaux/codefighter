package ai.hachualadushak.codefighter.problem

import ai.hachualadushak.codefighter.CheckStatus
import ai.hachualadushak.codefighter.CheckTaskDto
import ai.hachualadushak.codefighter.runCommand
import ai.hachualadushak.codefighter.toCheckResult
import ai.hachualadushak.codefighter.user.UserService
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID.randomUUID
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ProblemService(
    private val problemRepository: ProblemRepository,
    private val solutionRepository: SolutionRepository,
    private val userService: UserService,

    @Value("\${workspace.path}")
    private val workspacePath: String,

    @Value("\${workspace.python}")
    private val pythonPath: String
) {

    fun findById(id: Int) = problemRepository.findById(id)

    fun findAll() = problemRepository.findAll()

    fun save(problem: Problem) = problemRepository.save(problem)

    fun delete(id: Int) = problemRepository.deleteById(id)

    private fun checkTime(workspacePath: String): Long {
        val start = LocalDateTime.now()
        for (i in 1..50) {
            "$pythonPath test.py".runCommand(File(workspacePath))
        }
        val finish = LocalDateTime.now()

        return Duration.between(start, finish).toMillis()
    }

    fun getBestSolutions(id: Long) = solutionRepository.getByProblem(id)

    fun checkById(id: Int, code: String): CheckTaskDto {
        val problem = findById(id)
        val codeToRun = problem.checkerCode.replace("\${userFunction}", code)

        val workspacePath = "$workspacePath/${randomUUID()}/"
        File(workspacePath).mkdir()
        File("$workspacePath/test.py").writeText(codeToRun)

        val result = "$pythonPath test.py".runCommand(File(workspacePath))

        var time: Long = Long.MAX_VALUE
        if (result.isNullOrBlank()) {
            time = checkTime(workspacePath)
            val currentUser = userService.getCurrent()
            solutionRepository.save(Solution(
                author = currentUser.fullName ?: "Unknown legend",
                time = time,
                problem = problem
            ))
        }
        File(workspacePath).deleteRecursively()

        return problem.toCheckResult(
            if (result.isNullOrBlank()) CheckStatus.SUCCESS else CheckStatus.FAIL,
            result,
            time
        )
    }

}
