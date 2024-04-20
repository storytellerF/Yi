import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import okio.Path

actual suspend fun getModuleAndroidResourcePaths(dir: String): List<String> {
    TODO("Not yet implemented")
}

@Composable
actual fun DraggableBox(data: () -> Any, block: @Composable () -> Unit) {
}

actual fun List<Path>.convertToFileList(): List<Any> {
    TODO("Not yet implemented")
}

actual suspend fun getImagePreview(path: Path, size: Int): Result<ImageBitmap> {
    TODO("Not yet implemented")
}