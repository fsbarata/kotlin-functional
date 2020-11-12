package com.github.fsbarata.functional.data.test

import com.github.fsbarata.functional.data.Monoid
import org.junit.Test

abstract class MonoidLaws<A>(
	private val monoid: Monoid<A>,
): SemigroupLaws<A>(monoid) {
	abstract fun nonEmpty(): A

	final override fun factory() = nonEmpty()

	@Test
	fun leftIdentity() {
		val nonEmpty = nonEmpty()
		assertEquals(
			nonEmpty,
			monoid.combine(monoid.empty, nonEmpty)
		)
	}

	@Test
	fun rightIdentity() {
		val nonEmpty = nonEmpty()
		assertEquals(
			nonEmpty,
			monoid.combine(nonEmpty, monoid.empty)
		)
	}
}