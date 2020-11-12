package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.data.test.SemigroupLaws
import org.junit.Test

class ConcatNelSemigroupTest: SemigroupLaws<NonEmptyList<Int>>(
	NonEmptyList.concatSemigroup(),
) {
	override val possibilities = 100

	override fun factory(possibility: Int) =
		(0..possibility).toNel()!!
			.map { it % 13 }

	@Test
	fun combine() {
		val nel1 = factory(3)
		val nel2 = factory(5)
		assertEquals(
			nel1 + nel2,
			NonEmptyList.concatSemigroup<Int>().combine(nel1, nel2)
		)
	}
}
