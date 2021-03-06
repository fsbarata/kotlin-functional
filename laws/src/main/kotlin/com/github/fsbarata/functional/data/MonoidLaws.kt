package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.PossibilitiesTest
import org.junit.Test

abstract class MonoidLaws<A>(private val monoid: Monoid<A>): PossibilitiesTest {
	open fun equals(a1: A, a2: A): Boolean = a1 == a2

	open fun assertEqual(a1: A, a2: A) =
		assert(equals(a1, a2)) { "$a1 should be equal to $a2" }

	@Suppress("UNCHECKED_CAST")
	private fun eachPossibility(block: (A) -> Unit) {
		super.eachPossibility { block(it as A) }
	}

	@Test
	fun leftIdentity() {
		eachPossibility {
			assertEqual(
				it,
				monoid.combine(monoid.empty, it)
			)
		}
	}

	@Test
	fun rightIdentity() {
		eachPossibility {
			assertEqual(
				it,
				monoid.combine(it, monoid.empty)
			)
		}
	}
}
