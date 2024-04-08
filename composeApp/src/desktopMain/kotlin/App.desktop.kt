import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import java.io.File

actual suspend fun getModules(dir: String): List<String> {
    println("getModules $dir")
    val r = Regex("[ \\w\\W]+?: \\[([\\w\\W/ ,]+?)]")
    return withContext(Dispatchers.IO) {
        val process = ProcessBuilder().command("sh", "gradlew", "sourceSets").directory(File(dir)).start()
        val code = process.waitFor()
        println(code)
        if (code == 0) {
            process.inputReader().readLines()
        } else {
            emptyList()
        }
    }.mapNotNull {
        if (it.startsWith("Android resources")) {
            r.find(it)?.groups?.get(1)?.value
        } else {
            null
        }
    }.flatMap {
        it.split(", ")
    }.distinct()
}

actual suspend fun getPngPreview(path: Path): ImageBitmap {
    return FileSystem.SYSTEM.read(path) {
        org.jetbrains.skia.Image.makeFromEncoded(readByteArray()).toComposeImageBitmap()
    }
}