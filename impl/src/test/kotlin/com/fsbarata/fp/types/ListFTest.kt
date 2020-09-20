package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.MonadTest

class ListFTest : MonadTest<ListF<*>>() {
	override val monad = ListF.just(5)

	override fun Monad<ListF<*>, Int>.equalTo(other: Monad<ListF<*>, Int>) =
			asList == other.asList

}