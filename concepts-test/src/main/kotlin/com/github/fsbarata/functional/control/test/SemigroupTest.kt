package com.github.fsbarata.functional.control.test

import com.github.fsbarata.functional.data.Semigroup
import org.junit.Test

abstract class SemigroupTest<A>(
	private val semigroup: Semigroup<A>,
	protected val factory: () -> A,
) {
	open fun equals(a1: A, a2: A): Boolean = a1 == a2

	@Test
	fun associativity() {
		val val1 = factory()
		val val2 = factory()
		val val3 = factory()
		with(semigroup) {
			assert(equals(
				combine(combine(val1, val2), val3),
				combine(val1, combine(val2, val3))
			))
		}
	}
}
