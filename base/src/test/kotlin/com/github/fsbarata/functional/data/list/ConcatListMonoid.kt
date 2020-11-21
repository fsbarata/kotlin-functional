package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.data.MonoidLaws
import org.junit.Assert
import org.junit.Test

class ConcatListMonoid: MonoidLaws<ListF<Int>>(ListF.monoid()) {
	override val possibilities: Int = 25
	override fun nonEmpty(possibility: Int) =
		(0..possibility).map { it % 13 }.f()

	@Test
	fun concats() {
		Assert.assertEquals(
			listOf("6", "5", "1", "1L", "ajfg"),
			ListF.of("6", "5")
				.combineWith(ListF.of("1", "1L", "ajfg"))

		)
	}
}
