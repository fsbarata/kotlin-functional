package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.control.ApplicativeLaws
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.list.*

class ComposeApplicativeTest: ApplicativeLaws<ComposeContext<ListContext, NonEmptyContext>> {
	override val applicativeScope = ComposeApplicative.Scope(ListF, NonEmptyList)

	override val possibilities: Int = 10
	override fun factory(possibility: Int): Functor<ComposeContext<ListContext, NonEmptyContext>, Int> =
		ComposeApplicative(
			createList(possibility)
				.map { createNel(it) },
			NonEmptyList
		)
}
