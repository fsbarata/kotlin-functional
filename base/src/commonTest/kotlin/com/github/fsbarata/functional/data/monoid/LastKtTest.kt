package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.*
import kotlin.test.Test

class LastSemigroupScopeTest: SemigroupScopeLaws<String?> {
	override val semigroupScope: Semigroup.Scope<String?> = lastSemigroupScope()

	override val possibilities: Int = 10

	override fun factory(possibility: Int) = possibility.takeIf { it > 0 }?.toString()

	@Test
	fun concat() {
		with(lastSemigroupScope<Int>()) {
			assertEquals(5, 3.concatWith(1).concatWith(5))
			assertEquals(-1, 3.concatWith(-1))
			assertEquals(3, 1.concatWith(3))
		}
	}
}

class LastNotNullTest: SemigroupLaws<LastNotNull<String>> {
	override val possibilities: Int = 10

	override fun factory(possibility: Int) = LastNotNull(possibility.takeIf { it > 0 }?.toString())

	@Test
	fun concatWith() {
		assertEquals(
			LastNotNull(5),
			LastNotNull(3)
				.concatWith(LastNotNull(1))
				.concatWith(LastNotNull(5)),
		)
		assertEquals(
			LastNotNull(1),
			LastNotNull(5)
				.concatWith(LastNotNull(null))
				.concatWith(LastNotNull(1))
				.concatWith(LastNotNull(null))
				.concatWith(LastNotNull(null)),
		)
	}
}

class LastNotNullMonoidTest: MonoidLaws<String?> {
	override val monoid: Monoid<String?> = lastNotNullMonoid()

	override val possibilities: Int = 10

	override fun factory(possibility: Int) = possibility.takeIf { it > 0 }?.toString()

	@Test
	fun concat() {
		with(lastNotNullMonoid<Int>()) {
			assertEquals(5, 3.concatWith(1).concatWith(5))
			assertEquals(5, null.concatWith(1).concatWith(null).concatWith(5).concatWith(null))
		}
	}
}
