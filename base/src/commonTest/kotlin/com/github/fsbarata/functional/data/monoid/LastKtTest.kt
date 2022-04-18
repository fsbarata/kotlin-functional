package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.MonoidLaws
import com.github.fsbarata.functional.data.SemigroupLaws
import kotlin.test.Test

class LastTest: SemigroupLaws<Last<String>> {
	override val possibilities: Int = 10

	override fun factory(possibility: Int) = Last(possibility)

	@Test
	fun combineWith() {
		assertEquals(Last(5), Last(3).concatWith(Last(1)).concatWith(Last(5)))
		assertEquals(Last("z"), Last("1f").concatWith(Last("")).concatWith(Last("z")))
	}
}

class LastNotNullTest: MonoidLaws<LastNotNull<String?>>(
	LastNotNull.monoid()
) {
	override val possibilities: Int = 10

	override fun factory(possibility: Int) = LastNotNull(possibility.takeIf { it > 0 })

	@Test
	fun combineWith() {
		assertEquals(LastNotNull(5),
			LastNotNull<Int?>(3)
				.concatWith(LastNotNull(1))
				.concatWith(LastNotNull(5)))
		assertEquals(LastNotNull(1),
			LastNotNull<Int?>(5)
				.concatWith(LastNotNull(null))
				.concatWith(LastNotNull(1))
				.concatWith(LastNotNull(null))
				.concatWith(LastNotNull(null)))
	}
}
