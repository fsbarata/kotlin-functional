package com.github.fsbarata.functional.control.test

import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.data.compose
import org.junit.Test

interface FunctorTest<C> {
	fun <A> createFunctor(a: A): Functor<C, A>
	fun Functor<C, Int>.equalTo(other: Functor<C, Int>): Boolean

	@Test
	fun `map identity`() {
		val f1 = createFunctor(5)
		assert(f1.equalTo(f1.map { it }))
	}

	@Test
	fun `map composition`() {
		val fa = createFunctor("hello")
		val f = { a: String -> a.length }
		val g = { a: String -> a + "world" }
		val r1 = fa.map(f.compose(g))
		val r2 =
			{ fx: Functor<C, String> -> fx.map(f) }.compose { fx: Functor<C, String> -> fx.map(g) }
				.invoke(fa)
		assert(r1.equalTo(r2)) { "$r1 should be equal to $r2" }
	}
}