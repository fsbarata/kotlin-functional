package com.github.fsbarata.functional.data.test

import com.github.fsbarata.functional.data.Monoid
import org.junit.Test

abstract class MonoidTest<A>(
	private val monoid: Monoid<A>,
	private val nonEmpty: () -> A,
): SemigroupTest<A>(monoid, nonEmpty) {
	@Test
	fun leftIdentity() {
		val nonEmpty = nonEmpty()
		assert(equals(
			nonEmpty,
			monoid.combine(monoid.empty, nonEmpty)
		))
	}

	@Test
	fun rightIdentity() {
		val nonEmpty = nonEmpty()
		assert(equals(
			nonEmpty,
			monoid.combine(nonEmpty, monoid.empty)
		))
	}
}