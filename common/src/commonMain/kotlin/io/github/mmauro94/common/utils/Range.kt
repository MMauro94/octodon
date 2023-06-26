package io.github.mmauro94.common.utils

typealias FloatRange = ClosedFloatingPointRange<Float>

fun FloatRange.progress(progress: Float) = start + progress * (endInclusive - start)
