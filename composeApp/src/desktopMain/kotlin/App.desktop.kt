import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.DnDConstants
import java.awt.dnd.DragSource
import java.io.File
import java.util.concurrent.TimeUnit
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

actual suspend fun getModuleAndroidResourcePaths(dir: String): List<String> {
    println("getModules $dir")
    val r = Regex("[ \\w\\W]+?: \\[([\\w\\W/ ,]+?)]")
    return withContext(Dispatchers.IO) {
        val process = ProcessBuilder().command("sh", "gradlew", "sourceSets").directory(File(dir)).start()
        val processExited = process.waitFor(500, TimeUnit.MILLISECONDS)
        println(processExited)
        if (processExited && process.exitValue() == 0) {
            process.inputReader().readLines()
        } else {
            listOf("Android resources: [app/src/main/res]")
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
    return withContext(Dispatchers.IO) {
        FileSystem.SYSTEM.read(path) {
            org.jetbrains.skia.Image.makeFromEncoded(readByteArray()).toComposeImageBitmap()
        }
    }
}

@Composable
actual fun DraggableBox(data: () -> Any, block: @Composable () -> Unit) {
    Box {
        block()
        SwingPanel(factory = {
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                add(JLabel("Drag"))
                DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY) {
                    val transferable = object : Transferable {
                        override fun getTransferDataFlavors(): Array<DataFlavor> {
                            return arrayOf(DataFlavor.javaFileListFlavor)
                        }

                        override fun isDataFlavorSupported(flavor: DataFlavor?): Boolean {
                            return flavor == DataFlavor.javaFileListFlavor
                        }

                        override fun getTransferData(flavor: DataFlavor?): Any {
                            if (isDataFlavorSupported(flavor)) {
                                return data()
                            } else {
                                throw UnsupportedFlavorException(flavor)
                            }
                        }

                    }
                    it.startDrag(null, transferable)
                }
            }
        }, modifier = Modifier.matchParentSize())
    }
}

actual fun List<Path>.convertToFileList(): List<Any> = map {
    it.toFile()
}