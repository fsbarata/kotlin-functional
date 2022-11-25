package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.ContextTest
import com.github.fsbarata.functional.PossibilitiesTest
import kotlin.test.Test

interface FunctorScopeLaws<F>: ContextTest<F>, PossibilitiesTest {
	val functorScope: Functor.Scope<F>

	@Suppress("UNCHECKED_CAST")
	fun eachPossibility(block: (Context<F, Int>) -> Unit) {
		super.eachPossibility { block(it as Context<F, Int>) }
	}

	@Test
	fun `scope map identity`() {
		eachPossibility { fa -> assertEqualF(fa, functorScope.map(fa, id())) }
	}

	@Test
	fun `scope map composition`() {
		eachPossibility { fa ->
			val f = { a: String -> a.length }
			val g = { a: Int -> "world $a" }
			val r1 = functorScope.map(fa, f.compose(g))
			val r2 =
				{ fx: Context<F, String> -> functorScope.map(fx, f) }
					.compose { fx: Context<F, Int> -> functorScope.map(fx, g) }
					.invoke(fa)
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `scope onEach retains original functor`() {
		val f = { a: Int -> }
		eachPossibility { fa ->
			val r1 = functorScope.onEach(fa, f)
			val r2 = liftOnEach(f).fmap(functorScope, fa)
			assertEqualF(fa, r1)
			assertEqualF(r2, r1)
		}
	}
}
