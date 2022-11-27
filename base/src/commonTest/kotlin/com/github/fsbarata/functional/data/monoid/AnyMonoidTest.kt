package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.MonoidLaws
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnyMonoidTest: MonoidLaws<Boolean>(anyMonoid()) {
	override val possibilities: Int = 2

	override fun factory(possibility: Int) = possibility > 0

	@Test
	fun allEmpty() {
		assertEquals(false, anyMonoid().empty)
		assertEquals(emptyList<Boolean>().any { it }, anyMonoid().empty)
	}

	@Test
	fun anyTrue() {
		with(anyMonoid()) {
			assertTrue(false.concatWith(true))
			assertTrue(concat(true, false))
			assertTrue(true.concatWith(true))
			assertTrue(false.concatWith(true).concatWith(false).concatWith(false))
		}
	}

	@Test
	fun allFalses() {
		with(anyMonoid()) {
			assertFalse(false.concatWith(false))
			assertFalse(false.concatWith(false).concatWith(false).concatWith(false))
		}
	}
}