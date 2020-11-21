package com.github.fsbarata.functional.data.test

import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.compose
import com.github.fsbarata.functional.data.id
import org.junit.Test

interface FunctorLaws<F> {
	fun <A> createFunctor(a: A): Functor<F, A>
	fun <A> Functor<F, A>.equalTo(other: Functor<F, A>): Boolean = this == other
	fun <A> Functor<F, A>.describe() = toString()

	fun <A> assertEqualF(r1: Functor<F, A>, r2: Functor<F, A>) {
		assert(r1.equalTo(r2)) { "${r1.describe()} should be equal to ${r2.describe()}" }
	}

	@Test
	fun `map identity`() {
		val f1 = createFunctor(5)
		assertEqualF(f1, f1.map(id()))
	}

	@Test
	fun `map composition`() {
		val fa = createFunctor("hello")
		val f = { a: String -> a.length }
		val g = { a: String -> a + "world" }
		val r1 = fa.map(f.compose(g))
		val r2 =
			{ fx: Functor<F, String> -> fx.map(f) }.compose { fx: Functor<F, String> -> fx.map(g) }
				.invoke(fa)
		assertEqualF(r1, r2)
	}
}