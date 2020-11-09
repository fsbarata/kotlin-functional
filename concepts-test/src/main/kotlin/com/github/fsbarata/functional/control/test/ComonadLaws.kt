package com.github.fsbarata.functional.control.test

import com.github.fsbarata.functional.control.Comonad
import com.github.fsbarata.functional.data.compose
import com.github.fsbarata.functional.data.id
import org.junit.Assert.assertEquals
import org.junit.Test

interface ComonadLaws<W>: FunctorLaws<W> {
	override fun <A> createFunctor(a: A): Comonad<W, A>

	@Test
	fun `comonad identity`() {
		val r1 = createFunctor(3)
		val r2 = r1.extend { it.extract() }
		assertEqual(r1, r2)
	}

	@Test
	fun `comonad extend inverts extract`() {
		val v = createFunctor(1)
		val f = { wa: Comonad<W, Int> -> wa.extract() + 5 }
		val r1 = f(v)
		val r2 = v.extend(f).extract()
		assertEquals(r1, r2)
	}

	@Test
	fun `comonad composition`() {
		val v = createFunctor(1)
		val f = { wa: Comonad<W, Int> -> (wa.extract() * 2).toString() }
		val g = { wa: Comonad<W, Int> -> wa.extract() + 5 }
		val r1 = v.extend(g).extend(f)
		val r2 = v.extend(f compose { wa: Comonad<W, Int> -> wa.extend(g) })
		assertEqual(r1, r2)
	}

	@Test
	fun `duplicate = extend id`() {
		val v = createFunctor(3)
		val r1 = v.duplicate()
		val r2 = v.extend(id())
		assertEqual(r1, r2)
	}

	@Test
	fun `extend = duplicate map`() {
		val v = createFunctor(3)
		val f = { wa: Comonad<W, Int> -> (wa.extract() * 2).toString() }
		val r1 = v.duplicate().map(f)
		val r2 = v.extend(f)
		assertEqual(r1, r2)
	}
}
