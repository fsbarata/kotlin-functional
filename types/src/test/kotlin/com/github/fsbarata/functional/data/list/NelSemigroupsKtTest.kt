package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.data.sequence.nonEmptySequence
import com.github.fsbarata.functional.data.test.SemigroupLaws
import org.junit.Test
import kotlin.random.Random

class ConcatNelSemigroupTest: SemigroupLaws<NonEmptyList<Int>>(
	NonEmptyList.concatSemigroup(),
) {
	override fun factory() =
		nonEmptySequence(Random.nextInt(1, 100)) {
			if (Random.nextBoolean()) it + Random.nextInt(-5, 5)
			else null
		}.toList()

	@Test
	fun combine() {
		val nel1 = factory()
		val nel2 = factory()
		assertEquals(
			nel1 + nel2,
			NonEmptyList.concatSemigroup<Int>().combine(nel1, nel2)
		)
	}
}
