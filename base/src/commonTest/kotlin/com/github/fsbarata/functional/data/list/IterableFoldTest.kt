package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.data.Semigroup
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

	@Test
	fun listFoldR() {
		assertEquals(
			53,
			listOf(10, 11)
				.map(::IntMinusSg)
				.foldR(IntMinusSg(54))
				.i
		)
	}

	@Test
	fun iterableFoldR() {
		assertEquals(
			53,
			listOf(10, 11)
				.map(::IntMinusSg)
				.asIterable()
				.foldR(IntMinusSg(54))
				.i
		)
	}
}

data class IntMinusSg(val i: Int): Semigroup<IntMinusSg> {
	override fun combineWith(other: IntMinusSg) = IntMinusSg(i - other.i)
}