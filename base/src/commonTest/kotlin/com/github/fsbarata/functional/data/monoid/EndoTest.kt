package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.MonoidLaws
import kotlin.test.Test
import kotlin.test.assertEquals

class EndoTest: MonoidLaws<Endomorphism<Int>> {
	override val monoid: Monoid<Endomorphism<Int>> = endoMonoid()

	override val possibilities: Int = 5
	override fun factory(possibility: Int): Endomorphism<Int> = { possibility * it }

	override fun assertEqual(a1: Endomorphism<Int>, a2: Endomorphism<Int>) {
		assertEquals(a1(-3), a2(-3))
		assertEquals(a1(0), a2(0))
		assertEquals(a1(7), a2(7))
	}

	@Test
	fun concat() {
		assertEquals(7, endoMonoid<Int>().concat({ it + 1 }, { it * 2 })(3))
		assertEquals(7, endoSemigroup<Int>().concat({ it + 1 }, { it * 2 })(3))
	}

	@Test
	fun fapplyN() {
		assertEquals(129, fapplyN(3, n = 6) { it * 2 - 1 })
	}
}