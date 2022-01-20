package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.ContextTest
import com.github.fsbarata.functional.PossibilitiesTest
import kotlin.test.Test

interface InvariantLaws<F>: ContextTest<F>, PossibilitiesTest {
	override fun factory(possibility: Int): Invariant<F, Int>

	@Suppress("UNCHECKED_CAST")
	private fun eachPossibilityInvariant(block: (Invariant<F, Int>) -> Unit) {
		eachPossibility { block(it as Invariant<F, Int>) }
	}

	@Test
	fun `invmap identity`() {
		eachPossibilityInvariant { f1 ->
			assertEqualF(f1, f1.invmap(id(), id()))
		}
	}

	@Test
	fun `invmap composition`() {
		eachPossibilityInvariant { fa ->
			val f1 = { a: Int -> "world $a" }
			val f2 = { a: String -> "hello $a" }
			val g1 = { a: String -> a.hashCode() }
			val g2 = { a: String -> "$a world" }
			val r1 = fa.invmap(f2.compose(f1), g1.compose(g2))
			val r2 =
				{ fx: Invariant<F, String> -> fx.invmap(f2, g2) }
					.compose { fx: Invariant<F, Int> -> fx.invmap(f1, g1) }
					.invoke(fa)
			assertEqualF(r1, r2)
		}
	}
}