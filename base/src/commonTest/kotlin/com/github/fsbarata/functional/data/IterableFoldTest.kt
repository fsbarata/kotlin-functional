package com.github.fsbarata.functional.data

import kotlin.test.Test
import kotlin.test.assertEquals

class IterableFoldTest {
	@Test
	fun foldL() {
		assertEquals(
			33,
			listOf(10, 11)
				.map(::IntMinusSg)
				.foldL(IntMinusSg(54))
				.i
		)
	}
}

data class IntMinusSg(val i: Int): Semigroup<IntMinusSg> {
	override fun combineWith(other: IntMinusSg) = IntMinusSg(i - other.i)
}