package com.github.fsbarata.functional.data

import kotlin.test.Test
import kotlin.test.assertEquals

class FractionTest {
	@Test
	fun plus() {
		assertEquals(3 over 5, (1 over 5) + (2 over 5))
		assertEquals(67 over 483, (1 over 23) + (2 over 21))
		assertEquals(1 over 5, (1 over 15) + (2 over 15))
		assertEquals(1 over 6, (1 over 15) + (1 over 10))
		assertEquals(7 over 6, (1 over 6) + 1)
		assertEquals(5 over 6, (-1 over 6) + 1)
	}

	@Test
	fun minus() {
		assertEquals(-1 over 5, (1 over 5) - (2 over 5))
		assertEquals(-25 over 483, (1 over 23) - (2 over 21))
		assertEquals(1 over 15, (2 over 15) - (1 over 15))
		assertEquals(1 over 3, (4 over 10) - (1 over 15))
		assertEquals(-5 over 6, (1 over 6) - 1)
	}

	@Test
	fun times() {
		assertEquals(2 over 25, (1 over 5) * (2 over 5))
		assertEquals(-2 over 161, (3 over 23) * (-2 over 21))
		assertEquals(-13 over 225, (-13 over 15) * (1 over 15))
		assertEquals(1 over 10, (4 over 10) * (1 over 4))
	}

	@Test
	fun div() {
		assertEquals(3 over 2, (3 over 5) / (2 over 5))
		assertEquals(-63 over 46, (-3 over 23) / (2 over 21))
		assertEquals(-13 over 36, (13 over 15) / (-12 over 5))
		assertEquals(8 over 5, (4 over 10) / (1 over 4))
	}

	@Test
	fun components() {
		assertEquals(3L, (3 over 2).numerator)
		assertEquals(2L, (3 over 2).denominator)
		assertEquals(63L, (63 over 42).numerator)
		assertEquals(42L, (63 over 42).denominator)
	}

	@Test
	fun reduce() {
		assertEquals(3 over 2, (63 over 42).reduce())
		assertEquals(2L, (2 over 3).reduce().numerator)
		assertEquals(3L, (2 over 3).reduce().denominator)
		assertEquals(3L, (63 over 42).reduce().numerator)
		assertEquals(2L, (63 over 42).reduce().denominator)
		assertEquals(1L, (21 over 42).reduce().numerator)
		assertEquals(2L, (21 over 42).reduce().denominator)
	}

	@Test
	fun equals() {
		assertEquals(3 over 2, 3 over 2)
		assertEquals(1 over 3, 1 over 3)
		assertEquals(1 over 3, 3 over 9)
		assertEquals(-1 over 3, 3 over -9)
		assertEquals(1 over 3, -3 over -9)
	}

	@Test
	fun hashcode() {
		assertEquals((3 over 2).hashCode(), (3 over 2).hashCode())
		assertEquals((1 over 3).hashCode(), (1 over 3).hashCode())
		assertEquals((1 over 3).hashCode(), (3 over 9).hashCode())
		assertEquals((-1 over 3).hashCode(), (3 over -9).hashCode())
		assertEquals((1 over 3).hashCode(), (-3 over -9).hashCode())
	}
}
