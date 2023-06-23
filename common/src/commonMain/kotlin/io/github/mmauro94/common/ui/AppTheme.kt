package io.github.mmauro94.common.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object AppTheme {

    val shapes = Shapes(
        extraSmall = RoundedCornerShape(1.dp),
        small = RoundedCornerShape(2.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(8.dp),
        extraLarge = RoundedCornerShape(12.dp),
    )
}

private const val LOWLIGHTED_ALPHA = .6f

val ColorScheme.onSurfaceLowlighted: Color
    get() = this.onSurface.copy(alpha = LOWLIGHTED_ALPHA)

val ColorScheme.onSurfaceVariantLowlighted: Color
    get() = this.onSurfaceVariant.copy(alpha = LOWLIGHTED_ALPHA)
