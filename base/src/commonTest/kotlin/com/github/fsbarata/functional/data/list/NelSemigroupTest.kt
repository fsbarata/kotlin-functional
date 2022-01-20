package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.SemigroupLaws
import kotlin.test.Test

class NelSemigroupTest: SemigroupLaws<NonEmptyList<Int>> {
	override val possibilities = 10

	override fun factory(possibility: Int): NonEmptyList<Int> = createNel(possibility)

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
