package com.github.fsbarata.functional.data

import kotlin.test.Test

interface FunctorLaws<F>: FunctorScopeLaws<F>, InvariantLaws<F> {
	@Suppress("UNCHECKED_CAST")
	private fun eachPossibilityFunctor(block: (Functor<F, Int>) -> Unit) {
		eachPossibility { block(it as Functor<F, Int>) }
	}

	@Test
	fun `map identity`() {
		eachPossibilityFunctor { fa -> assertEqualF(fa, fa.map(id())) }
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

	@Test
	fun `onEach retains original functor`() {
		val f = { a: Int -> }
		eachPossibilityFunctor { fa ->
			val r1 = fa.onEach(f)
			val r2 = liftOnEach(f).fmap(fa)
			assertEqualF(fa, r1)
			assertEqualF(r2, r1)
		}
	}
}
