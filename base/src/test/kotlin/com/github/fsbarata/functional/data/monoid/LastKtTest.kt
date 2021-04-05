package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.MonoidLaws
import com.github.fsbarata.functional.data.SemigroupLaws
import org.junit.Assert.assertEquals
import org.junit.Test

class LastTest: SemigroupLaws<Last<String>> {
	override val possibilities: Int = 10

	override fun factory(possibility: Int) = Last(possibility)

	@Test
	fun combineWith() {
		assertEquals(Last(5), Last(3).combineWith(Last(1)).combineWith(Last(5)))
		assertEquals(Last("z"), Last("1f").combineWith(Last("")).combineWith(Last("z")))
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
				.combineWith(LastNotNull(1))
				.combineWith(LastNotNull(5)))
		assertEquals(LastNotNull(1),
			LastNotNull<Int?>(5)
				.combineWith(LastNotNull(null))
				.combineWith(LastNotNull(1))
				.combineWith(LastNotNull(null))
				.combineWith(LastNotNull(null)))
	}
}
