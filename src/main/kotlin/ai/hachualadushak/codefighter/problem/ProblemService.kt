package ai.hachualadushak.codefighter.problem

import ai.hachualadushak.codefighter.CheckStatus
import ai.hachualadushak.codefighter.CheckTaskDto
import ai.hachualadushak.codefighter.runCommand
import ai.hachualadushak.codefighter.toCheckResult
import ai.hachualadushak.codefighter.user.UserService
import java.io.File
import java.util.UUID.randomUUID
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ProblemService(
    private val problemRepository: ProblemRepository,
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

    fun checkById(id: Int, code: String): CheckTaskDto {
        val problem = findById(id)
        val codeToRun = problem.checkerCode.replace("\${userFunction}", code)

        val workspacePath = "$workspacePath/${randomUUID()}/"
        File(workspacePath).mkdir()
        File("$workspacePath/test.py").writeText(codeToRun)

        val result = "$pythonPath test.py".runCommand(File(workspacePath))
        File(workspacePath).deleteRecursively()

        if (result.isNullOrBlank()) {
            problem.best = userService.getCurrent().login!!
            problemRepository.save(problem)
        }

        return problem.toCheckResult(
            if (result.isNullOrBlank()) CheckStatus.SUCCESS else CheckStatus.FAIL,
            result
        )
    }

}
