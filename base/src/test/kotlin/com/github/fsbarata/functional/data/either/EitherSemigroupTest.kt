package com.github.fsbarata.functional.data.either

import com.github.fsbarata.functional.data.SemigroupLaws
import org.junit.Assert.assertEquals
import org.junit.Test

class EitherSemigroupTest: SemigroupLaws<Either<String, Int>> {
	override val possibilities: Int = 2
	override fun factory(possibility: Int): Either<String, Int> =
		if (possibility > 0) Either.Left("a")
		else Either.Right(5)

	@Test
	fun `chooses first right of eithers`() {
		assertEquals(
			Either.Left("b"),
			Either.Left("a").combineWith(Either.Left("b"))
		)

		assertEquals(
			Either.Right(3),
			(Either.Left("a") as Either<String, Int>).combineWith(Either.Right(3))
		)

		assertEquals(
			Either.Right(5),
			Either.Right(5).combineWith(Either.Right(3))
		)
	}
}