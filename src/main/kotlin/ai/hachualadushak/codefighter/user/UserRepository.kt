package ai.hachualadushak.codefighter.user

import ai.hachualadushak.codefighter.EntityNotFoundException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import java.io.File
import java.io.FileNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    val mapper: ObjectMapper
) {

    @Value("\${storage.users}")
    private lateinit var storagePath: String

    @PostConstruct
    fun init() {
        val directory = File(storagePath)

        if (!directory.exists()) {
            directory.mkdir()
        }
    }

    fun findAll(): List<User> {
        return File(storagePath).listFiles()
            ?.map { it.let { mapper.readValue(it, User::class.java) } }
            ?.sortedBy { it.id }
            ?: throw EntityNotFoundException("Failed to get user.")
    }

    fun findByLogin(login: String): User {
        val profileFile = File("$storagePath/$login.json")
        try {
            return profileFile.let { mapper.readValue(it, User::class.java) }
        } catch (e: FileNotFoundException) {
            throw EntityNotFoundException("User not found.")
        }
    }

    fun save(user: User): User {
        val profileFile = File("$storagePath/${user.login}.json")
        profileFile.writer().let { mapper.writeValue(it, user) }
        return user
    }

    fun deleteById(id: Int) {
        File("$storagePath/$id.json").delete()
    }


}
