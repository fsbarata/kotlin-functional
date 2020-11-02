package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.test.FoldableTest
import com.fsbarata.fp.concepts.test.MonadTest
import com.fsbarata.fp.concepts.test.MonadZipTest
import com.fsbarata.fp.data.Foldable

class ListFTest: MonadTest<ListF<*>>, MonadZipTest<ListF<*>>, FoldableTest {
	override val monadScope = ListF
	override fun Monad<ListF<*>, Int>.equalTo(other: Monad<ListF<*>, Int>) =
		asList == other.asList

	override fun createFoldable(item1: Int, item2: Int, item3: Int): Foldable<Int> =
		listOf(item1, item2, item3).f()
}
