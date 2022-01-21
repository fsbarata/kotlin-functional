package com.github.fsbarata.functional.data.either

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.maybe.toOptional
import kotlin.test.Test

class EitherComprehensionTest {
	@Test
	fun successful_both_rights() {
		val either1 = Either.right<Throwable, Int>(3)
		val either2 = Either.right<Throwable, String>("5")
		assertEquals(Either.Right(8), Either<Throwable, Int> {
			val a = either1.bind()
			val b = either2.flatMap { it.toIntOrNull().toOptional().toEither { Throwable() } }.bind()
			a + b
		})
	}

	@Test
	fun failure_any_left() {
		val either1 = Either.right<String, Int>(3)
		assertEquals(Either.Left("2a"), Either<String, Int> {
			val a = either1.bind()
			val b = Either.left<String, Int>("2a").bind()
			a + b
		})
		assertEquals(Either.Left("2a"), Either<String, Int> {
			val a = Either.left<String, Int>("2a").bind()
			val b = either1.bind()
			a + b
		})
	}
}