package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.ApplicativeLaws
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.list.*
import com.github.fsbarata.functional.data.maybe.None
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.Some
import kotlin.test.Test

class ComposedApplicativeTest: ApplicativeLaws<ComposedContext<ListContext, NonEmptyContext>> {
	override val applicativeScope = ComposedApplicative.Scope(ListF, NonEmptyList)

	override val possibilities: Int = 10
	override fun factory(possibility: Int): Functor<ComposedContext<ListContext, NonEmptyContext>, Int> =
		ComposedApplicative(
			createList(possibility)
				.map { createNel(it) },
			ListF,
			NonEmptyList
		)

	@Test
	fun `ComposedApplicative maps underlying functor`() {
		assertEquals(
			listOf(Some(3), None, None, Some(5), Some(7)),
			ListF.of(Some(6), None, None, Some(11), Some(14))
				.composed(Optional)
				.map { it / 2 }
				.underlying
		)
	}
}
