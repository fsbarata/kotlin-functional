package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.test.FoldableTest
import com.github.fsbarata.functional.control.test.MonadTest
import com.github.fsbarata.functional.control.test.MonadZipTest
import com.github.fsbarata.functional.data.Foldable

class ListFTest: MonadTest<ListF<*>>, MonadZipTest<ListF<*>>, FoldableTest {
	override val monadScope = ListF
	override fun Monad<ListF<*>, Int>.equalTo(other: Monad<ListF<*>, Int>) =
		asList == other.asList

	override fun createFoldable(item1: Int, item2: Int, item3: Int): Foldable<Int> =
		listOf(item1, item2, item3).f()
}
