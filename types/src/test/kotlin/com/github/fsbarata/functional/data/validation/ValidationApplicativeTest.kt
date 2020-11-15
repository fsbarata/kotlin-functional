package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.validation.Validation.Failure
import com.github.fsbarata.functional.data.validation.Validation.Success
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ValidationApplicativeTest {
	data class IntSemigroup(val i: Int): Semigroup<IntSemigroup> {
		override fun combineWith(other: IntSemigroup) = IntSemigroup(i + other.i)
	}

	@Test
	fun ap() {
		assertEquals(
			Success("31"),
			Success("3").toApplicative().ap(Success { a: String -> a + 1 }.toApplicative()).unwrap()
		)
		assertEquals(
			Failure(IntSemigroup(3)),
			Failure(IntSemigroup(3)).toApplicative().ap(Success { a: String -> a + 1 }.toApplicative()).unwrap()
		)
		assertEquals(
			Failure(IntSemigroup(5)),
			Failure(IntSemigroup(3)).toApplicative().ap<Long>(Failure(IntSemigroup(2)).toApplicative()).unwrap()
		)
		assertEquals(
			Failure(IntSemigroup(2)),
			Success("3").toApplicative<IntSemigroup, String>()
				.ap<Long>(Failure(IntSemigroup(2)).toApplicative()).unwrap()
		)
	}

	@Suppress("UNREACHABLE_CODE")
	@Test
	fun lift2() {
		assertEquals(
			Success("35"),
			Success("3").toApplicative()
				.lift2(Success(5).toApplicative()) { a, b -> a + b }
				.unwrap()
		)
		assertEquals(
			Failure(IntSemigroup(3)),
			Success("3").toApplicative<IntSemigroup, String>()
				.lift2(Failure(IntSemigroup(3)).toApplicative()) { a, b -> fail() }
				.unwrap()
		)
		assertEquals(
			Failure(IntSemigroup(5)),
			Failure(IntSemigroup(2)).toApplicative()
				.lift2(Failure(IntSemigroup(3)).toApplicative()) { a, b -> fail() }
				.unwrap()
		)
		assertEquals(
			Failure(IntSemigroup(2)),
			Failure(IntSemigroup(2)).toApplicative()
				.lift2(Success(1).toApplicative()) { a, b -> fail() }
				.unwrap()
		)
	}
}