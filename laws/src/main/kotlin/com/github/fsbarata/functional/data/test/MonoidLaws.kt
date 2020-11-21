package com.github.fsbarata.functional.data.test

import com.github.fsbarata.functional.data.Monoid
import org.junit.Test
import kotlin.random.Random

abstract class MonoidLaws<A>(private val monoid: Monoid<A>) {
	open val possibilities: Int get() = 1
	abstract fun nonEmpty(possibility: Int): A

	open fun equals(a1: A, a2: A): Boolean = a1 == a2

	open fun assertEqual(a1: A, a2: A) =
		assert(equals(a1, a2)) { "$a1 should be equal to $a2" }

	@Test
	fun leftIdentity() {
		val nonEmpty = nonEmpty(possibility())
		assertEqual(
			nonEmpty,
			monoid.combine(monoid.empty, nonEmpty)
		)
	}

	@Test
	fun rightIdentity() {
		val nonEmpty = nonEmpty(possibility())
		assertEqual(
			nonEmpty,
			monoid.combine(nonEmpty, monoid.empty)
		)
	}
}

private fun MonoidLaws<*>.possibility() = Random.nextInt(possibilities)