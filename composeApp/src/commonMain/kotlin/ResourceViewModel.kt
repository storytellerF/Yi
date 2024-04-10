import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM

class ResourceViewModel(private val projectDir: String) : ViewModel() {
    val paths = MutableStateFlow<List<Path>>(emptyList())
    val loadState = MutableStateFlow(false)

    init {
        load()
    }

    private fun load() {
        if (loadState.value) return
        loadState.value = true
        viewModelScope.launch {
            paths.value = getModuleAndroidResourcePaths(projectDir).map {
                "$projectDir/$it".toPath()
            }.filter {
                FileSystem.SYSTEM.exists(it)
            }
            loadState.value = false
        }
    }
}

class ResourceDataViewModel(private val resources: List<Path>, val name: String) : ViewModel() {
    val paths = MutableStateFlow<List<Path>>(emptyList())
    private val loadState = MutableStateFlow(false)

    init {
        load()
    }

    private fun load() {
        if (loadState.value) return
        loadState.value = true
        viewModelScope.launch {
            paths.value = resources.flatMap {
                FileSystem.SYSTEM.list(it).filter { p ->
                    p.name.startsWith(name)
                }
            }
        }
    }
}