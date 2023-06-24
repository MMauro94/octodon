package io.github.mmauro94.common.utils

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

fun interface ComposableString {
    @Composable
    fun str(): String
}

fun StringResource.composable(): ComposableString {
    return ComposableString { stringResource(this@composable) }
}

fun StringResource.composable(vararg args: Any): ComposableString {
    return ComposableString { stringResource(this@composable, *args) }
}

fun PluralsResource.composable(quantity: Int): ComposableString {
    return ComposableString { stringResource(this@composable, quantity) }
}

fun PluralsResource.composable(quantity: Int, vararg args: Any): ComposableString {
    return ComposableString { stringResource(this@composable, quantity, *args) }
}
