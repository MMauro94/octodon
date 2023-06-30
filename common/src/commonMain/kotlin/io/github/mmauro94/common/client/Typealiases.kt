@file:Suppress("AnnotationOnSeparateLine")

package io.github.mmauro94.common.client

import io.github.mmauro94.common.serializers.InstantSerializer
import io.github.mmauro94.common.serializers.UrlSerializer
import io.ktor.http.Url
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

typealias ParsableInstant = @Serializable(InstantSerializer::class) Instant

typealias ParsableUrl = @Serializable(UrlSerializer::class) Url
