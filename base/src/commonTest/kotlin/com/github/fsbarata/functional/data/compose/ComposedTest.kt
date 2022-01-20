package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.FunctorLaws
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.NonEmptyContext
import com.github.fsbarata.functional.data.list.createNel
import com.github.fsbarata.functional.data.maybe.None
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.Some
import com.github.fsbarata.functional.data.validation.Validation
import com.github.fsbarata.functional.data.validation.ValidationContext
import kotlin.test.Test

class ComposedTest: FunctorLaws<ComposeContext<NonEmptyContext, ValidationContext<Nothing>>> {
	@Suppress("UNCHECKED_CAST")
	override fun factory(possibility: Int) =
		Composed(
			createNel(possibility)
				.map {
					if (it > 0) Validation.Success(it)
					else Validation.Failure(Exception()) as Validation<Nothing, Int>
				}
		)

	@Test
	fun `Composed maps underlying functor`() {
		assertEquals(
			listOf(Some(3), None, None, Some(5), Some(7)),
			ListF.of(Some(6), None, None, Some(11), Some(14))
				.compose()
				.map { it / 2 }
				.decompose<Optional<Int>>()
		)
	}
}
