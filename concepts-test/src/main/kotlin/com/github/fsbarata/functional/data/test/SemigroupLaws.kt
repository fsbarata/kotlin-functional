package com.github.fsbarata.functional.data.test

import com.github.fsbarata.functional.data.Semigroup
import org.junit.Test
import kotlin.random.Random

interface SemigroupLaws<A: Semigroup<A>> {
	val possibilities: Int get() = 1
	fun factory(possibility: Int): A

	fun equals(a1: A, a2: A): Boolean = a1 == a2

	fun assertEquals(a1: A, a2: A) =
		assert(equals(a1, a2)) { "$a1 should be equal to $a2" }

	@Test
	fun associativity() {
		val val1 = factory(possibility())
		val val2 = factory(possibility())
		val val3 = factory(possibility())
		assertEquals(
			val1.combineWith(val2.combineWith(val3)),
			val1.combineWith(val2).combineWith(val3),
		)
	}
}

private fun SemigroupLaws<*>.possibility() = Random.nextInt(possibilities)
