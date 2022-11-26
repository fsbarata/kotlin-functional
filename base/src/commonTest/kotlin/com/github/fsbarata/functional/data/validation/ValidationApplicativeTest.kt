package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.ApplicativeScopeLaws
import com.github.fsbarata.functional.data.monoid.sumIntMonoid
import com.github.fsbarata.functional.data.validation.Validation.Failure
import com.github.fsbarata.functional.data.validation.Validation.Success
import kotlin.test.Test
import kotlin.test.fail

class ValidationApplicativeTest: ApplicativeScopeLaws<ValidationContext<Int>> {
	override val applicativeScope = Validation.applicative(sumIntMonoid())

	override val possibilities: Int = 10

	override fun factory(possibility: Int) =
		if (possibility < 3) Validation.success(possibility)
		else Failure(possibility)

	@Test
	fun ap() {
		assertEquals(
			Success("31"),
			applicativeScope.ap(Validation.success("3"), Validation.success { a: String -> a + 1 })
		)
		assertEquals(
			Failure(3),
			applicativeScope.ap<String, String>(Failure(3), Validation.success { a: String -> a + 1 })
		)
		assertEquals(
			Failure(5),
			applicativeScope.ap<Nothing, Nothing>(Failure(3), Failure(2))
		)
		assertEquals(
			Failure(2),
			applicativeScope.ap<String, String>(Validation.success("3"), Failure(2))
		)
	}

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
			Failure(3),
			applicativeScope.lift2(
				Validation.success("3"),
				Failure(3)
			) { _, _ -> fail() }
		)
		assertEquals(
			Failure(5),
			applicativeScope.lift2(
				Failure(2),
				Failure(3),
			) { _, _ -> fail() }
		)
		assertEquals(
			Failure(2),
			applicativeScope.lift2(
				Failure(2),
				Validation.success(1),
			) { _, _ -> fail() }
		)
	}
}