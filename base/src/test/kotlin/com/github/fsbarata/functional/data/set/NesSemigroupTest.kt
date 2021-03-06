package com.github.fsbarata.functional.data.set

import com.github.fsbarata.functional.data.SemigroupLaws
import com.github.fsbarata.functional.data.list.createNel
import org.junit.Assert.assertEquals
import org.junit.Test

class NesSemigroupTest: SemigroupLaws<NonEmptySet<Int>> {
	override val possibilities = 10

	override fun factory(possibility: Int): NonEmptySet<Int> = createNel(possibility).toSet()

	@Test
	fun combine() {
		val nes1 = factory(3)
		val nes2 = factory(5)
		assertEquals(
			nes1 + nes2,
			nes1.combineWith(nes2)
		)
	}
}
