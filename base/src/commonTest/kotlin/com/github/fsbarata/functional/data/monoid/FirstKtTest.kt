package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.MonoidLaws
import com.github.fsbarata.functional.data.SemigroupLaws
import kotlin.test.Test

class FirstTest: SemigroupLaws<First<String>> {
	override val possibilities: Int = 10

	override fun factory(possibility: Int) = First(possibility)

	@Test
	fun combineWith() {
		assertEquals(First(3), First(3).concatWith(First(1)).concatWith(First(5)))
		assertEquals(First("1f"), First("1f").concatWith(First("1fg")).concatWith(First("")))
	}
}

class FirstNotNullTest: MonoidLaws<FirstNotNull<String?>>(
	FirstNotNull.monoid()
) {
	override val possibilities: Int = 10

	override fun factory(possibility: Int) = FirstNotNull(possibility.takeIf { it > 0 })

	@Test
	fun combineWith() {
		assertEquals(FirstNotNull(3),
			FirstNotNull<Int?>(3)
				.concatWith(FirstNotNull(1))
				.concatWith(FirstNotNull(5)))
		assertEquals(FirstNotNull(1),
			FirstNotNull<Int?>(null)
				.concatWith(FirstNotNull(null))
				.concatWith(FirstNotNull(1))
				.concatWith(FirstNotNull(null))
				.concatWith(FirstNotNull(5)))
	}
}
