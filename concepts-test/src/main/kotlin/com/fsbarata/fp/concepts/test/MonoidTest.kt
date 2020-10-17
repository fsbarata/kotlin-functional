package com.fsbarata.fp.concepts.test

import com.fsbarata.fp.concepts.Monoid
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
			with(monoid) { empty.combine(nonEmpty) }
		))
	}

	@Test
	fun rightIdentity() {
		val nonEmpty = nonEmpty()
		assert(equals(
			nonEmpty,
			with(monoid) { nonEmpty.combine(empty) }
		))
	}
}