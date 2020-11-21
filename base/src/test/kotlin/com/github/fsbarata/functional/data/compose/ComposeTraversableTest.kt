package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.list.*
import com.github.fsbarata.functional.data.TraversableLaws

class ComposeTraversableTest: TraversableLaws<ComposeContext<ListContext, NonEmptyContext>> {
	override val traversableScope = ComposeTraversable.Scope<ListContext, NonEmptyContext>()
	override fun <A> createTraversable(vararg items: A) =
		ComposeTraversable(
			if (items.isEmpty()) ListF.empty()
			else listOfNotNull(
				NonEmptyList.just(items[0]),
				items.drop(1).toNel()
			).f()
		)

	override val possibilities: Int = 10
	override fun factory(possibility: Int): Functor<ComposeContext<ListContext, NonEmptyContext>, Int> =
		ComposeApplicative(
			createList(possibility)
				.map { createNel(it) },
			NonEmptyList
		)
}
