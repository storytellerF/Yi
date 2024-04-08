import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showDirPicker by remember { mutableStateOf(false) }
        var selectedDir by remember {
            mutableStateOf<String?>(null)
        }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                TextField(selectedDir.orEmpty(), onValueChange = {}, modifier = Modifier.weight(1f))
                Button(onClick = {
                    showDirPicker = true
                }) {
                    Text("Click me!")
                }
            }

            val dir = selectedDir
            if (dir != null) {
                ResourcesPanel(dir)
            }
        }

        DirectoryPicker(showDirPicker) { path ->
            showDirPicker = false
            // do something with path
            selectedDir = path
        }
    }
}

expect suspend fun getModules(dir: String): List<String>

expect suspend fun getPngPreview(path: Path): ImageBitmap

@Composable
private fun ResourcesPanel(selectedDir: String) {
    var sourceSets by remember {
        mutableStateOf(emptyList<Path>())
    }
    var isLoading by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(selectedDir) {
        isLoading = true
        sourceSets = getModules(selectedDir).map {
            "$selectedDir/$it".toPath()
        }.filter {
            FileSystem.SYSTEM.exists(it)
        }.flatMap {
            FileSystem.SYSTEM.list(it).filter { p ->
                p.name.startsWith("drawable")
            }
        }
        isLoading = false
    }

    val list by remember {
        derivedStateOf {
            sourceSets.flatMap { sourceSet ->
                FileSystem.SYSTEM.list(sourceSet).filter {
                    it.name.endsWith("png")
                }.groupBy {
                    it.name.substringBefore(".")
                }.toList().sortedBy {
                    it.first
                }
            }

        }
    }
    Box {
        Column {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(sourceSets, {
                    it.toString()
                }) {
                    Text(it.toString())
                }
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(list, key = {
                    it.toString()
                }) {
                    Row {
                        Box(modifier = Modifier.width(100.dp).height(100.dp)) {
                            val first = it.second.first()
                            val bitmap by produceState<ImageBitmap?>(null, first) {
                                value = getPngPreview(it.second.first())
                            }
                            bitmap?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = "image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Column {
                            Text(it.first)
                            Text(it.second.size.toString())
                        }
                    }

                }
            }
        }

        if (isLoading)
            Text(if (isLoading) "Loading" else "", modifier = Modifier.fillMaxSize())
    }
}