package com.github.fsbarata.functional.data.foldable

import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.monoid.sumIntMonoid
import org.junit.Assert.assertEquals
import org.junit.Test

class FoldableScanTest {
	@Test
	fun scanL() {
		assertEquals(
			nelOf(2, 5, 10, 11),
			nelOf(3, 5, 1).scanL(2, Int::plus),
		)
	}

	@Test
	fun scan() {
		assertEquals(
			nelOf(0, 3, 8, 9),
			nelOf(3, 5, 1).scan(sumIntMonoid()),
		)
		assertEquals(
			nelOf(2, 5, 10, 11),
			nelOf(3, 5, 1).scan(2, Int::plus),
		)
	}
}