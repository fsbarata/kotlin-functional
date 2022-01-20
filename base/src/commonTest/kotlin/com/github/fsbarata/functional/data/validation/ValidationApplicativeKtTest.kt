package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.validation.Validation.Failure
import com.github.fsbarata.functional.data.validation.Validation.Success
import kotlin.test.Test
import kotlin.test.fail

class ValidationApplicativeKtTest {
	data class IntSemigroup(val i: Int): Semigroup<IntSemigroup> {
		override fun combineWith(other: IntSemigroup) = IntSemigroup(i + other.i)
	}

	@Test
	fun ap() {
		assertEquals(
			Success("31"),
			Success("3").ap(Success { a: String -> a + 1 })
		)
		assertEquals(
			Failure(IntSemigroup(3)),
			Failure(IntSemigroup(3)).ap(Success { a: String -> a + 1 })
		)
		assertEquals(
			Failure(IntSemigroup(5)),
			Failure(IntSemigroup(3)).ap<IntSemigroup, String, Long>(Failure(IntSemigroup(2)))
		)
		assertEquals(
			Failure(IntSemigroup(2)),
			Success("3").ap<IntSemigroup, String, Long>(Failure(IntSemigroup(2)))
		)
	}

	@Suppress("UNREACHABLE_CODE")
	@Test
	fun lift2() {
		assertEquals(
			Success("35"),
			lift2(Success("3"), Success(5)) { a, b -> a + b }
		)
		assertEquals(
			Failure(IntSemigroup(3)),
			lift2(Success("3"), Failure(IntSemigroup(3))) { _, _ -> fail() }
		)
		assertEquals(
			Failure(IntSemigroup(5)),
			lift2(Failure(IntSemigroup(2)), Failure(IntSemigroup(3))) { _, _ -> fail() }
		)
		assertEquals(
			Failure(IntSemigroup(2)),
			lift2(Failure(IntSemigroup(2)), Success(1)) { _, _ -> fail() })
	}
}