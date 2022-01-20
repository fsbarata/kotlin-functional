package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.ApplicativeScopeLaws
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.validation.Validation.Failure
import com.github.fsbarata.functional.data.validation.Validation.Success
import kotlin.test.Test
import kotlin.test.fail

class ValidationApplicativeTest: ApplicativeScopeLaws<ValidationContext<ValidationApplicativeTest.IntSemigroup>> {
	data class IntSemigroup(val i: Int): Semigroup<IntSemigroup> {
		override fun combineWith(other: IntSemigroup) = IntSemigroup(i + other.i)
	}

	override val applicativeScope = ValidationApplicativeScope<IntSemigroup>()

	override val possibilities: Int = 10

	override fun factory(possibility: Int) =
		if (possibility < 3) Validation.success(possibility)
		else Failure(IntSemigroup(possibility))

	@Test
	fun ap() {
		assertEquals(
			Success("31"),
			applicativeScope.ap(Validation.success("3"), Validation.success { a: String -> a + 1 })
		)
		assertEquals(
			Failure(IntSemigroup(3)),
			applicativeScope.ap<String, String>(Failure(IntSemigroup(3)), Validation.success { a: String -> a + 1 })
		)
		assertEquals(
			Failure(IntSemigroup(5)),
			applicativeScope.ap<Nothing, Nothing>(Failure(IntSemigroup(3)), Failure(IntSemigroup(2)))
		)
		assertEquals(
			Failure(IntSemigroup(2)),
			applicativeScope.ap<String, String>(Validation.success("3"), Failure(IntSemigroup(2)))
		)
	}

	@Suppress("UNREACHABLE_CODE")
	@Test
	fun lift2() {
		assertEquals(
			Success("35"),
			applicativeScope.lift2(
				Validation.success("3"),
				Validation.success(5)
			) { a, b -> a + b }
		)
		assertEquals(
			Failure(IntSemigroup(3)),
			applicativeScope.lift2(
				Validation.success("3"),
				Failure(IntSemigroup(3))
			) { _, _ -> fail() }
		)
		assertEquals(
			Failure(IntSemigroup(5)),
			applicativeScope.lift2(
				Failure(IntSemigroup(2)),
				Failure(IntSemigroup(3)),
			) { _, _ -> fail() }
		)
		assertEquals(
			Failure(IntSemigroup(2)),
			applicativeScope.lift2(
				Failure(IntSemigroup(2)),
				Validation.success(1),
			) { _, _ -> fail() }
		)
	}
}