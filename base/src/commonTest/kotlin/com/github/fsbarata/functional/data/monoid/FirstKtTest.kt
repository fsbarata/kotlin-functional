package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.*
import kotlin.test.Test

class FirstSemigroupScopeTest: SemigroupScopeLaws<String?> {
	override val semigroupScope: Semigroup.Scope<String?> = firstSemigroupScope()

	override val possibilities: Int = 10

	override fun factory(possibility: Int) = possibility.takeIf { it > 0 }?.toString()

	@Test
	fun concat() {
		with(firstSemigroupScope<Int>()) {
			assertEquals(3, 3.concatWith(1).concatWith(5))
			assertEquals(1, 1.concatWith(3))
		}
	}
}

class FirstNotNullSemigroupTest: SemigroupLaws<FirstNotNull<String>> {
	override val possibilities: Int = 10

	override fun factory(possibility: Int) = FirstNotNull(possibility.takeIf { it > 0 }?.toString())

	@Test
	fun concatWith() {
		assertEquals(FirstNotNull(3),
			FirstNotNull(3)
				.concatWith(FirstNotNull(1))
				.concatWith(FirstNotNull(5)))
		assertEquals(FirstNotNull(1),
			FirstNotNull<Int>(null)
				.concatWith(FirstNotNull(null))
				.concatWith(FirstNotNull(1))
				.concatWith(FirstNotNull(null))
				.concatWith(FirstNotNull(5)))
	}
}

class FirstNotNullMonoidTest: MonoidLaws<String?> {
	override val monoid: Monoid<String?> = firstNotNullMonoid()

	override val possibilities: Int = 10

	override fun factory(possibility: Int) = possibility.takeIf { it > 0 }?.toString()

	@Test
	fun concat() {
		with(firstNotNullMonoid<Int>()) {
			assertEquals(3, 3.concatWith(1).concatWith(5))
			assertEquals(1, null.concatWith(1).concatWith(null).concatWith(5).concatWith(null))
		}
	}
}
