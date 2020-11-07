package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.control.test.MonadTest
import com.github.fsbarata.functional.control.test.MonadZipTest
import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.test.FoldableTest

class ListFTest: MonadTest<ListF<*>>, MonadZipTest<ListF<*>>, FoldableTest {
	override val monadScope = ListF
	override fun <A> Functor<ListF<*>, A>.equalTo(other: Functor<ListF<*>, A>) =
		asList == other.asList

	override fun <A> createFoldable(vararg items: A): Foldable<A> =
		items.toList().f()
}
