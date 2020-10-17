package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.test.MonadTest

class ListFTest: MonadTest<ListF<*>>(ListF) {
	override fun Monad<ListF<*>, Int>.equalTo(other: Monad<ListF<*>, Int>) =
		asList == other.asList
}
