package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.MonoidLaws
import kotlin.test.Test

class ListMonoidTest: MonoidLaws<ListF<Int>>(ListF.monoid()) {
	override val possibilities: Int = 10
	override fun factory(possibility: Int) = createList(possibility)

	@Test
	fun concats() {
		assertEquals(
			listOf("6", "5", "1", "1L", "ajfg"),
			ListF.of("6", "5")
				.concatWith(ListF.of("1", "1L", "ajfg"))

		)
	}
}
