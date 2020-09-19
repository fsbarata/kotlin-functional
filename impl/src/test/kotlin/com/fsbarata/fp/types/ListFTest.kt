package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.MonadTest

class ListFTest : MonadTest<List<*>>() {
	override val monad = ListF.just(5)

	override fun Monad<List<*>, Int>.equalTo(other: Monad<List<*>, Int>) =
			asList == other.asList

}