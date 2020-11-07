package com.github.fsbarata.functional.control.test

import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.data.compose
import com.github.fsbarata.functional.data.id
import org.junit.Test

interface FunctorTest<C> {
	fun <A> createFunctor(a: A): Functor<C, A>
	fun <A> Functor<C, A>.equalTo(other: Functor<C, A>): Boolean

	fun <A> assertEquals(r1: Functor<C, A>, r2: Functor<C, A>) =
		assert(r1.equalTo(r2)) { "$r1 should be equal to $r2" }

	@Test
	fun `map identity`() {
		val f1 = createFunctor(5)
		assertEquals(f1, f1.map(id()))
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
		assertEquals(r1, r2)
	}
}