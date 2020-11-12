package com.github.fsbarata.functional.data.test

import com.github.fsbarata.functional.data.Semigroup
import org.junit.Test
import kotlin.random.Random

abstract class SemigroupLaws<A>(
	private val semigroup: Semigroup<A>,
) {
	open val possibilities: Int = 1
	abstract fun factory(possibility: Int): A

	internal fun possibility() = Random.nextInt(possibilities)

	open fun equals(a1: A, a2: A): Boolean = a1 == a2

	fun assertEquals(a1: A, a2: A) =
		assert(equals(a1, a2)) { "$a1 should be equal to $a2" }

	@Test
	fun associativity() {
		val val1 = factory(possibility())
		val val2 = factory(possibility())
		val val3 = factory(possibility())
		with(semigroup) {
			assertEquals(
				combine(combine(val1, val2), val3),
				combine(val1, combine(val2, val3))
			)
		}
	}
}
