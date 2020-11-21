package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.data.SemigroupLaws
import org.junit.Assert.assertEquals
import org.junit.Test

class ConcatNelSemigroupTest: SemigroupLaws<NonEmptyList<Int>> {
	override val possibilities = 10

	override fun factory(possibility: Int): NonEmptyList<Int> = when (possibility) {
		0 -> NonEmptyList.just(1)
		1 -> nelOf(3, 2)
		else -> NonEmptyList.of(1, factory(possibility - 2))
	}

	@Test
	fun combine() {
		val nel1 = factory(3)
		val nel2 = factory(5)
		assertEquals(
			nel1 + nel2,
			nel1.combineWith(nel2)
		)
	}
}
