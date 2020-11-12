package com.github.fsbarata.functional.data.test

import com.github.fsbarata.functional.data.Monoid
import org.junit.Test

abstract class MonoidLaws<A>(
	private val monoid: Monoid<A>,
): SemigroupLaws<A>(monoid) {
	abstract fun nonEmpty(possibility: Int): A

	final override fun factory(possibility: Int) = nonEmpty(possibility)

	@Test
	fun leftIdentity() {
		val nonEmpty = nonEmpty(possibility())
		assertEquals(
			nonEmpty,
			monoid.combine(monoid.empty, nonEmpty)
		)
	}

	@Test
	fun rightIdentity() {
		val nonEmpty = nonEmpty(possibility())
		assertEquals(
			nonEmpty,
			monoid.combine(nonEmpty, monoid.empty)
		)
	}
}