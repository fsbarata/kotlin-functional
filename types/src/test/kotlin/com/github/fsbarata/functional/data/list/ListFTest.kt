package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.control.test.MonadTest
import com.github.fsbarata.functional.control.test.MonadZipTest
import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.Traversable
import com.github.fsbarata.functional.data.test.TraversableTest

class ListFTest: MonadTest<ListF<*>>,
	MonadZipTest<ListF<*>>,
	TraversableTest<ListF<*>> {
	override val traversableScope = ListF
	override val monadScope = ListF
	override fun <A> Functor<ListF<*>, A>.equalTo(other: Functor<ListF<*>, A>) =
		asList == other.asList

	override fun <A> createFunctor(a: A) = ListF.just(a)

	override fun <A> createTraversable(vararg items: A) =
		items.toList().f()
}
