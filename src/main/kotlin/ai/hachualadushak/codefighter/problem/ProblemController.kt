package ai.hachualadushak.codefighter.problem

import ai.hachualadushak.codefighter.toStartProblemDto
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/problems")
class ProblemController(
    val problemService: ProblemService
) {

    @GetMapping
    @Operation(summary = "Get all problems")
    fun findAll() = problemService.findAll().map { it.toStartProblemDto() }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create new problem")
    fun createProblem(@RequestBody problem: Problem) = problemService.save(problem)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete problem")
    fun deleteProblem(@PathVariable id: Int) = problemService.delete(id)

    @GetMapping("/{id}")
    @Operation(summary = "Get problem and start time")
    fun startProblem(@PathVariable id: Int) = problemService.findById(id).toStartProblemDto()

    @PostMapping("/{id}")
    @Operation(summary = "Check problem")
    fun checkProblem(@PathVariable id: Int, @RequestBody code: String) = problemService.checkById(id, code)

}
