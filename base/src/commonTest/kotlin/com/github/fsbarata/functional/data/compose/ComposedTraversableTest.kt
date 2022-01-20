package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.TraversableLaws
import com.github.fsbarata.functional.data.list.*

class ComposedTraversableTest: TraversableLaws<ComposeContext<ListContext, NonEmptyContext>> {
	override val traversableScope = ComposedTraversable.Scope<ListContext, NonEmptyContext>()
	override fun <A> createTraversable(vararg items: A) =
		ComposedTraversable(
			if (items.isEmpty()) ListF.empty()
			else listOfNotNull(
				NonEmptyList.just(items[0]),
				items.drop(1).toNel()
			).f()
		)

	override val possibilities: Int = 10
	override fun factory(possibility: Int): Functor<ComposeContext<ListContext, NonEmptyContext>, Int> =
		ComposedApplicative(
			createList(possibility)
				.map { createNel(it) },
			ListF,
			NonEmptyList
		)
}
