package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.MonoidLaws
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AllMonoidTest: MonoidLaws<Boolean>(allMonoid()) {
	override val possibilities: Int = 2

	override fun factory(possibility: Int) = possibility > 0

	@Test
	fun allEmpty() {
		assertEquals(true, allMonoid().empty)
		assertEquals(emptyList<Boolean>().all { it }, allMonoid().empty)
	}

	@Test
	fun allTrues() {
		with(allMonoid()) {
			assertTrue(concat(true, true))
			assertTrue(true.concatWith(true).concatWith(true).concatWith(true))
		}
	}

	@Test
	fun falses() {
		with(allMonoid()) {
			assertFalse(false.concatWith(true))
			assertFalse(true.concatWith(false))
			assertFalse(false.concatWith(true).concatWith(true).concatWith(true))
			assertFalse(true.concatWith(false).concatWith(false).concatWith(true))
		}
	}
}