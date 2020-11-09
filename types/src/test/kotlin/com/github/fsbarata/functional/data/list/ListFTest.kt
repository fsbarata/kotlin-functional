package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.test.MonadLaws
import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.test.TraversableLaws

class ListFTest: MonadLaws<ListF<*>>,
	MonadZipLaws<ListF<*>>,
	TraversableLaws<ListF<*>> {
	override val traversableScope = ListF
	override val monadScope = ListF

	override fun <A> createFunctor(a: A) = ListF.just(a)

	override fun <A> createTraversable(vararg items: A) =
		items.toList().f()
}
