package io.github.mmauro94.android

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import io.github.mmauro94.common.App
import io.github.mmauro94.common.navigation.BackNavigationContext
import io.github.mmauro94.common.navigation.LocalBackNavigationContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val backHandler = BackNavigationContext()
        onBackPressedDispatcher.addCallback(this) {
            if (!backHandler.back()) {
                finish()
            }
        }
        setContent {
            CompositionLocalProvider(
                LocalBackNavigationContext provides backHandler,
            ) {
                App()
            }
        }
    }
}
