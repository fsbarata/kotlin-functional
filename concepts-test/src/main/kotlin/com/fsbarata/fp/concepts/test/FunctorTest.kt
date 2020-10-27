package com.fsbarata.fp.concepts.test

import com.fsbarata.fp.concepts.Functor
import org.junit.Test

interface FunctorTest<C> {
	fun createFunctor(a: Int): Functor<C, Int>
	fun Functor<C, Int>.equalTo(other: Functor<C, Int>): Boolean

	@Test
	fun `map identity`() {
		val f1 = createFunctor(5)
		assert(f1.equalTo(f1.map { it }))
	}
}