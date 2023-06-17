import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.mmauro94.common.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
