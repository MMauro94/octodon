package io.github.mmauro94.common.client.entities

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class CommentPathTest : FunSpec(
    {

        context("constructor fails in the expected cases") {
            test("empty ids") {
                shouldThrow<IllegalArgumentException> {
                    CommentPath("", emptyList())
                }
            }

            test("not starting at root") {
                shouldThrow<IllegalArgumentException> {
                    CommentPath("123.456", listOf(123, 456))
                }
            }

            test("mismatching value and ids") {
                shouldThrow<IllegalArgumentException> {
                    CommentPath("0.123.456", listOf(0, 456, 789))
                }
            }
        }

        context("parsing works as expected") {

            test("empty string fails") {
                shouldThrow<IllegalArgumentException> {
                    CommentPath.parse("")
                }
            }

            test("string not starting with 0 fails") {
                shouldThrow<IllegalArgumentException> {
                    CommentPath.parse("123.456")
                }
            }

            test("string not containing number fails") {
                shouldThrow<NumberFormatException> {
                    CommentPath.parse("abc.xyz")
                }
            }

            test("root") {
                CommentPath.parse("0") shouldBe CommentPath("0", listOf(0))
            }

            test("normal string") {
                CommentPath.parse("0.123.456") shouldBe CommentPath("0.123.456", listOf(0, 123, 456))
            }
        }

        context("depth works") {
            withData(
                "0" to 0,
                "0.123" to 1,
                "0.123.456" to 2,
            ) { (path, expectedDepth) ->
                CommentPath.parse(path).depth shouldBe expectedDepth
            }
        }
    },
)
