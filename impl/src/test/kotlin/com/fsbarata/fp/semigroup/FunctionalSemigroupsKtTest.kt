package com.fsbarata.fp.semigroup

import com.fsbarata.fp.concepts.test.SemigroupTest
import com.fsbarata.fp.types.NonEmptyList
import com.fsbarata.fp.types.nonEmptySequence
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
			with(concatNelSemigroup<Int>()) { nel1.combine(nel2) }
		)
	}
}
