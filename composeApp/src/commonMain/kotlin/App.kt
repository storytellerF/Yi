import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.viewmodel.viewModel
import okio.FileSystem
import okio.Path
import okio.SYSTEM
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    PreComposeApp {
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
}

expect suspend fun getModuleAndroidResourcePaths(dir: String): List<String>

expect suspend fun getPngPreview(path: Path): ImageBitmap

@Composable
expect fun DraggableBox(data: () -> Any, block: @Composable () -> Unit)

expect fun List<Path>.convertToFileList(): List<Any>

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResourcesPanel(selectedDir: String) {
    val v = viewModel(ResourceViewModel::class, listOf(selectedDir)) {
        ResourceViewModel(selectedDir)
    }
    val isLoading by v.loadState.collectAsState()
    val sourceSets by v.paths.collectAsState()

    Box {
        var selected by remember {
            mutableStateOf<List<Path>>(emptyList())
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            FlowRow {
                sourceSets.forEach { p ->
                    Row {
                        Text(p.toString().substringAfter(selectedDir))
                        Checkbox(selected.any { s ->
                            p == s
                        }, onCheckedChange = { checked: Boolean ->
                            val removedIfNeed = selected.filter { s ->
                                p != s
                            }
                            selected = if (checked) {
                                removedIfNeed + p
                            } else {
                                removedIfNeed
                            }
                        })
                    }
                }
            }
            val navigator = rememberNavigator()
            val e by navigator.currentEntry.collectAsState(null)
            val routes = listOf("drawables", "mipmaps")
            LazyRow {
                items(routes, {
                    it
                }) {
                    Text(it, modifier = Modifier.clickable {
                        navigator.navigate("/$it")
                    }.background(if (it == e?.route?.route) Color.Blue else Color.Transparent))
                }
            }
            NavHost(
                navigator = navigator,
                navTransition = NavTransition(),
                initialRoute = "/drawables",
                modifier = Modifier.weight(1f)
            ) {
                // Define a scene to the navigation graph
                scene(
                    route = "/drawables",
                ) {
                    ResourceView(selected, "drawable")
                }
                scene(
                    route = "/mipmaps",
                ) {
                    ResourceView(selected, "mipmap")
                }
            }
        }

        if (isLoading)
            Text(if (isLoading) "Loading" else "", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
    }
}

@Composable
private fun ResourceView(sourceSets: List<Path>, s: String) {
    val v = viewModel(ResourceDataViewModel::class, listOf(sourceSets, s)) {
        ResourceDataViewModel(sourceSets, s)
    }
    val d by v.paths.collectAsState()
    val list by remember {
        derivedStateOf {
            d.flatMap { sourceSet ->
                FileSystem.SYSTEM.list(sourceSet).filter {
                    it.name.endsWith("png")
                }
            }.groupBy {
                it.name.substringBefore(".")
            }.toList().sortedByDescending {
                it.second.size
            }
        }
    }
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(list, key = {
            it.toString()
        }) {

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.size(100.dp)) {
                    val first = it.second.first()
                    val bitmap by produceState<ImageBitmap?>(null, first) {
                        value = getPngPreview(first)
                    }
                    bitmap?.let {
                        Image(
                            bitmap = it,
                            contentDescription = "image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(it.first)
                    Text(it.second.size.toString())
                }
                DraggableBox({
                    it.second.convertToFileList()
                }) {
                    Text("Drag", modifier = Modifier.size(100.dp))
                }
            }

        }
    }
}