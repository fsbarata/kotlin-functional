package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.PossibilitiesTest
import kotlin.test.Test
import kotlin.test.asserter

interface SemigroupScopeLaws<A>: PossibilitiesTest {
	val semigroupScope: Semigroup.Scope<A>

	override fun factory(possibility: Int): A

	fun equals(a1: A, a2: A): Boolean = a1 == a2

	fun assertEqual(a1: A, a2: A) {
		asserter.assertTrue({ "$a1 should be equal to $a2" }, equals(a1, a2))
	}

	@Suppress("UNCHECKED_CAST")
	private fun eachPossibility(block: (A) -> Unit) {
		super.eachPossibility { block(it as A) }
	}

	@Test
	fun `concat associativity`() {
		eachPossibility { val1 ->
			eachPossibility { val2 ->
				eachPossibility { val3 ->
					assertEqual(
						semigroupScope.concat(val1, semigroupScope.concat(val2, val3)),
						semigroupScope.concat(semigroupScope.concat(val1, val2), val3),
					)
				}
			}
		}
	}
}
