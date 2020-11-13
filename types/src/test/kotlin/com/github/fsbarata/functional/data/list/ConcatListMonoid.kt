package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.data.test.MonoidLaws
import org.junit.Assert
import org.junit.Test

class ConcatListMonoid: MonoidLaws<List<Int>>(
	ListF.concatMonoid(),
) {
	override val possibilities: Int = 25
	override fun nonEmpty(possibility: Int) =
		(0..possibility).map { it % 13 }

	@Test
	fun concats() {
		Assert.assertEquals(
			listOf("6", "5", "1", "1L", "ajfg"),
			ListF.concatMonoid<String>()
				.combine(listOf("6", "5"), listOf("1", "1L", "ajfg"))

		)
	}
}
