package com.github.fsbarata.functional.data

import kotlin.test.Test
import kotlin.test.asserter

interface MonoidLaws<A>: SemigroupScopeLaws<A> {
	val monoid: Monoid<A>

	override val semigroupScope: Semigroup.Scope<A> get() = monoid

	@Suppress("UNCHECKED_CAST")
	private fun eachPossibility(block: (A) -> Unit) {
		super.eachPossibility { block(it as A) }
	}

	@Test
	fun leftIdentity() {
		eachPossibility {
			assertEqual(
				it,
				monoid.concat(monoid.empty, it)
			)
		}
	}

	@Test
	fun rightIdentity() {
		eachPossibility {
			assertEqual(
				it,
				monoid.concat(it, monoid.empty)
			)
		}
	}
}
