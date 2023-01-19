package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.MonoidLaws
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ConcatStringMonoidTest: MonoidLaws<String> {
	override val monoid: Monoid<String> = concatStringMonoid()

	override val possibilities: Int = 25
	override fun factory(possibility: Int) = "$possibility"

	@Test
	fun concats() {
		assertEquals("a2bb", concatStringMonoid().concat("a2", "bb"))
	}
}

class ConcatArrayMonoidTest: MonoidLaws<Array<Any>> {
	override val monoid: Monoid<Array<Any>> = concatArrayMonoid()

	override val possibilities: Int = 30
	override fun factory(possibility: Int): Array<Any> =
		(0..(possibility / 2)).map { possibility - it }
			.toTypedArray()

	override fun equals(a1: Array<Any>, a2: Array<Any>): Boolean = a1.contentEquals(a2)

	@Test
	fun concats() {
		assertContentEquals(
			arrayOf("6", "5", "1", "1L", "ajfg"),
			concatArrayMonoid<String>().concat(arrayOf("6", "5"), arrayOf("1", "1L", "ajfg"))
		)
	}
}

