package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.PossibilitiesTest
import org.junit.Test

interface FunctorLaws<F>: PossibilitiesTest<Functor<F, Int>> {
	override fun factory(possibility: Int): Functor<F, Int>

	fun <A> Functor<F, A>.equalTo(other: Functor<F, A>): Boolean = this == other
	fun <A> Functor<F, A>.describe() = toString()

	fun <A> assertEqualF(r1: Functor<F, A>, r2: Functor<F, A>) {
		assert(r1.equalTo(r2)) { "${r1.describe()} should be equal to ${r2.describe()}" }
	}

	@Test
	fun `map identity`() {
		eachPossibility { f1 ->
			assertEqualF(f1, f1.map(id()))
		}
	}

	@Test
	fun `map composition`() {
		eachPossibility { fa ->
			val f = { a: String -> a.length }
			val g = { a: Int -> "world $a" }
			val r1 = fa.map(f.compose(g))
			val r2 =
				{ fx: Functor<F, String> -> fx.map(f) }.compose { fx: Functor<F, Int> -> fx.map(g) }
					.invoke(fa)
			assertEqualF(r1, r2)
		}
	}
}