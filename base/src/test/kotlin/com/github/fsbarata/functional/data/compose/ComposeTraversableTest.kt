package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.data.list.*
import com.github.fsbarata.functional.data.test.TraversableLaws

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
}
