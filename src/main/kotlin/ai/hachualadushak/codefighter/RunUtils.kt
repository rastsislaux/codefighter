package ai.hachualadushak.codefighter

import java.io.File
import java.io.IOException
import java.time.Duration
import java.util.concurrent.TimeUnit

fun String.runCommand(workingDir: File): String {
    try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        if (!proc.waitFor(5, TimeUnit.SECONDS)) {
            proc.destroyForcibly()
            throw TimeoutException("Your program is running too long.")
        }
        return proc.errorStream.bufferedReader().readText()
    } catch(e: IOException) {
        throw RuntimeException(e)
    }
}
