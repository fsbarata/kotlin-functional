package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.test.MonadTest
import com.github.fsbarata.functional.control.test.MonadZipTest
import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.test.FoldableTest
import com.github.fsbarata.functional.data.test.TraversableTest

class ListFTest: MonadTest<ListF<*>>, MonadZipTest<ListF<*>>, FoldableTest, TraversableTest {
	override val monadScope = ListF
	override fun <A> Monad<ListF<*>, A>.equalTo(other: Monad<ListF<*>, A>) =
		asList == other.asList

	override fun createFoldable(item1: Int, item2: Int, item3: Int): Foldable<Int> =
		listOf(item1, item2, item3).f()
}
