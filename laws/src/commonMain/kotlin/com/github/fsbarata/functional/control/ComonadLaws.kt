package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.FunctorLaws
import com.github.fsbarata.functional.data.compose
import com.github.fsbarata.functional.data.id
import kotlin.test.assertEquals
import kotlin.test.Test

interface ComonadLaws<W>: FunctorLaws<W> {
	@Suppress("UNCHECKED_CAST")
	private fun <T> eachPossibilityComonad(block: (Comonad<W, Int>) -> T): List<T> {
		return eachPossibility { block(it as Comonad<W, Int>) }
	}

	@Test
	fun `comonad identity`() {
		eachPossibilityComonad { r1 ->
			val r2 = r1.extend { it.extract() }
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `comonad extend inverts extract`() {
		eachPossibilityComonad { v ->
			val f = { wa: Comonad<W, Int> -> wa.extract() + 5 }
			val r1 = f(v)
			val r2 = v.extend(f).extract()
			assertEquals(r1, r2)
		}
	}

	@Test
	fun `comonad composition`() {
		eachPossibilityComonad { v ->
			val f = { wa: Comonad<W, Int> -> (wa.extract() * 2).toString() }
			val g = { wa: Comonad<W, Int> -> wa.extract() + 5 }
			val r1 = v.extend(g).extend(f)
			val r2 = v.extend(f compose { wa: Comonad<W, Int> -> wa.extend(g) })
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `duplicate = extend id`() {
		eachPossibilityComonad { v ->
			val r1 = v.duplicate()
			val r2 = v.extend(id())
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `extend = duplicate map`() {
		eachPossibilityComonad { v ->
			val f = { wa: Comonad<W, Int> -> (wa.extract() * 2).toString() }
			val r1 = v.duplicate().map(f)
			val r2 = v.extend(f)
			assertEqualF(r1, r2)
		}
	}
}
