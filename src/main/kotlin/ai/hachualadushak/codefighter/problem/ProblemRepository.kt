package ai.hachualadushak.codefighter.problem

import ai.hachualadushak.codefighter.EntityNotFoundException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import java.io.File
import java.io.FileNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository

@Repository
class ProblemRepository(
    val mapper: ObjectMapper
) {

    @Value("\${storage.problems}")
    private lateinit var storagePath: String

    @PostConstruct
    fun init() {
        val directory = File(storagePath)

        if (!directory.exists()) {
            directory.mkdir()
        }
    }

    fun findAll(): List<Problem> {
        return File(storagePath).listFiles()
            ?.map { it.let { mapper.readValue(it, Problem::class.java) } }
            ?.sortedBy { it.id }
            ?: throw EntityNotFoundException("Failed to get profiles.")
    }

    fun findById(id: Int): Problem {
        val profileFile = File("$storagePath/$id.json")
        try {
            return profileFile.let { mapper.readValue(it, Problem::class.java) }
        } catch (e: FileNotFoundException) {
            throw EntityNotFoundException("Profile not found.")
        }
    }

    fun save(profile: Problem): Problem {
        val profileFile = File("$storagePath/${profile.id}.json")
        profileFile.writer().let { mapper.writeValue(it, profile) }
        return profile
    }

    fun deleteById(id: Int) {
        File("$storagePath/$id.json").delete()
    }


}
