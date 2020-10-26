package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.test.MonadTest
import com.fsbarata.fp.concepts.test.MonadZipTest

class ListFTest: MonadTest<ListF<*>> {
	override val monadScope = ListF
	override fun Monad<ListF<*>, Int>.equalTo(other: Monad<ListF<*>, Int>) =
		asList == other.asList
}
