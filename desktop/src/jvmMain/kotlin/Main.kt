import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.mmauro94.common.App

fun main() = application {
    Window(
        state = rememberWindowState(size = DpSize(720.dp, 1280.dp)),
        onCloseRequest = ::exitApplication,
    ) {
        App()
    }
}
