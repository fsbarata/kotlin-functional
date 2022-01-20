package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.PossibilitiesTest
import kotlin.test.Test

interface FunctorLaws<F>: InvariantLaws<F>, PossibilitiesTest {
	@Suppress("UNCHECKED_CAST")
	private fun eachPossibilityFunctor(block: (Functor<F, Int>) -> Unit) {
		eachPossibility { block(it as Functor<F, Int>) }
	}

	@Test
	fun `map identity`() {
		eachPossibilityFunctor { f1 ->
			assertEqualF(f1, f1.map(id()))
		}
	}

	@Test
	fun `map composition`() {
		eachPossibilityFunctor { fa ->
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