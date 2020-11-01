package com.fsbarata.fp.concepts.test

import com.fsbarata.fp.data.Monoid
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