package io.github.mmauro94.common.client

import kotlinx.serialization.Serializable

@Serializable
data class LemmyError(val error: String)
