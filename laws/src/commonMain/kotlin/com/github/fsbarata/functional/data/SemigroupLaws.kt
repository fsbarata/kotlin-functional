package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.PossibilitiesTest
import kotlin.test.Test
import kotlin.test.asserter

interface SemigroupLaws<A: Semigroup<A>>: PossibilitiesTest {
	fun equals(a1: A, a2: A): Boolean = a1 == a2

	fun assertEqual(a1: A, a2: A) {
		asserter.assertTrue({ "$a1 should be equal to $a2" }, equals(a1, a2))
	}

	@Suppress("UNCHECKED_CAST")
	private fun eachPossibility(block: (A) -> Unit) {
		super.eachPossibility { block(it as A) }
	}

	@Test
	fun `combineWith associativity`() {
		eachPossibility { val1 ->
			eachPossibility { val2 ->
				eachPossibility { val3 ->
					assertEqual(
						val1.concatWith(val2.concatWith(val3)),
						val1.concatWith(val2).concatWith(val3),
					)
				}
			}
		}
	}
}
