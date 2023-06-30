package io.github.mmauro94.common.client.api

import io.github.mmauro94.common.utils.Result
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.types.shouldBeInstanceOf

class GetSiteTest : FunSpec(
    {

        context("getPosts is able to parse a sample response") {
            withData(
                "get_site_1",
                "get_site_2",
                "get_site_3",
            ) { fileName ->
                getMockedLemmyClient(fileName).getSite().shouldBeInstanceOf<Result.Success<*>>()
            }
        }
    },
)
