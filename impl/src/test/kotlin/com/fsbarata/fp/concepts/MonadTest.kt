package com.fsbarata.fp.concepts

import org.junit.Assert.assertTrue
import org.junit.Test

abstract class MonadTest<C: Monad<C, *>> {
	abstract val monad: Monad<C, Int>
	abstract fun Monad<C, Int>.equalTo(other: Monad<C, Int>): Boolean

	@Test
	fun flatMapIsCorrect() {
		assertTrue(monad.map { it * 3 }.equalTo(
				monad.bind { monad.just(it * 3) }
		))
	}
}