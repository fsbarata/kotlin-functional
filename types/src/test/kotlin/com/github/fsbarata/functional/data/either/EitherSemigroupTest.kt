package com.github.fsbarata.functional.data.either

import com.github.fsbarata.functional.data.test.SemigroupLaws
import org.junit.Test

class EitherSemigroupTest: SemigroupLaws<Either<String, Int>>(
	Either.semigroup()
) {
	override val possibilities: Int = 2
	override fun factory(possibility: Int): Either<String, Int> =
		if (possibility > 0) Either.Left("a")
		else Either.Right(5)

	@Test
	fun `chooses first right of eithers`() {
		assertEquals(
			Either.Left("b"),
			Either.semigroup<String, Int>()
				.combine(Either.Left("a"), Either.Left("b"))
		)

		assertEquals(
			Either.Right(3),
			Either.semigroup<String, Int>()
				.combine(Either.Left("a"), Either.Right(3))
		)

		assertEquals(
			Either.Right(5),
			Either.semigroup<String, Int>()
				.combine(Either.Right(5), Either.Right(3))
		)
	}
}