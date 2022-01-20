package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.control.ApplicativeLaws
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.list.*

class ComposedApplicativeTest: ApplicativeLaws<ComposeContext<ListContext, NonEmptyContext>> {
	override val applicativeScope = ComposedApplicative.Scope(ListF, NonEmptyList)

	override val possibilities: Int = 10
	override fun factory(possibility: Int): Functor<ComposeContext<ListContext, NonEmptyContext>, Int> =
		ComposedApplicative(
			createList(possibility)
				.map { createNel(it) },
			ListF,
			NonEmptyList
		)
}
