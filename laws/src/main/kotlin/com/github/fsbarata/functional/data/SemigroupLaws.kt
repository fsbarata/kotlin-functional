package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.PossibilitiesTest
import org.junit.Test

interface SemigroupLaws<A: Semigroup<A>>: PossibilitiesTest<A> {
	fun equals(a1: A, a2: A): Boolean = a1 == a2

	fun assertEqual(a1: A, a2: A) {
		assert(equals(a1, a2)) { "$a1 should be equal to $a2" }
	}

	@Test
	fun `combineWith associativity`() {
		eachPossibility { val1 ->
			eachPossibility { val2 ->
				eachPossibility { val3 ->
					assertEqual(
						val1.combineWith(val2.combineWith(val3)),
						val1.combineWith(val2).combineWith(val3),
					)
				}
			}
		}
	}
}
