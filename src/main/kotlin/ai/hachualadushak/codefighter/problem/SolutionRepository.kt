package ai.hachualadushak.codefighter.problem

import ai.hachualadushak.codefighter.EntityNotFoundException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import java.io.File
import java.io.FileNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository

@Repository
class SolutionRepository(
    val mapper: ObjectMapper
) {

    @Value("\${storage.solutions}")
    private lateinit var storagePath: String

    @PostConstruct
    fun init() {
        val directory = File(storagePath)

        if (!directory.exists()) {
            directory.mkdir()
        }
    }

    fun findAll(): List<Solution> {
        return File(storagePath).listFiles()
            ?.map { it.let { mapper.readValue(it, Solution::class.java) } }
            ?.sortedBy { it.time }
            ?: throw EntityNotFoundException("Failed to get profiles.")
    }

    fun findById(id: Int): Solution {
        val profileFile = File("$storagePath/$id.json")
        try {
            return profileFile.let { mapper.readValue(it, Solution::class.java) }
        } catch (e: FileNotFoundException) {
            throw EntityNotFoundException("Profile not found.")
        }
    }

    fun getByProblem(problemId: Long): List<Solution> {
        return findAll()
            .filter { it.problem.id == problemId }
            .sortedBy { it.time }
    }

    fun save(profile: Solution): Solution {
        val profileFile = File("$storagePath/${profile.author}-${profile.problem.id}.json")
        profileFile.writer().let { mapper.writeValue(it, profile) }
        return profile
    }

    fun deleteById(id: Int) {
        File("$storagePath/$id.json").delete()
    }


}
