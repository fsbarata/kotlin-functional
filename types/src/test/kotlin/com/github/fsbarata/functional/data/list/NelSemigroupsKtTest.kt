package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.data.sequence.nonEmptySequence
import com.github.fsbarata.functional.data.test.SemigroupTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class ConcatNelSemigroupTest: SemigroupTest<NonEmptyList<Int>>(
	concatNelSemigroup(),
	factory = {
		nonEmptySequence(Random.nextInt(1, 100)) {
			if (Random.nextBoolean()) it + Random.nextInt(-5, 5)
			else null
		}.toList()
	}
) {
	@Test
	fun combine() {
		val nel1 = factory()
		val nel2 = factory()
		assertEquals(
			nel1 + nel2,
			concatNelSemigroup<Int>().combine(nel1, nel2)
		)
	}
}
