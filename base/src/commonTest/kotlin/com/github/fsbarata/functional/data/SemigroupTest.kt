package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.monoid.dual
import kotlin.test.Test

class SemigroupTest {
	data class StringConcatSg(val str: String): Semigroup<StringConcatSg> {
		override fun concatWith(other: StringConcatSg) = StringConcatSg(str + other.str)
	}

	@Test
	fun stimes() {
		assertEquals(IntPlusSg(3), IntPlusSg(3).stimes(1))
		assertEquals(IntPlusSg(45), IntPlusSg(3).stimes(15))
		assertEquals(StringConcatSg("4a4a4a4a"), StringConcatSg("4a").stimes(4))
	}

	@Test
	fun dual() {
		assertEquals(StringConcatSg("5a3w"), StringConcatSg("3w").dual().concatWith(StringConcatSg("5a").dual()).get)
	}

	@Test
	fun sconcat() {
		assertEquals(
			StringF("5ag2"),
			nelOf(StringF("5a"), StringF("g"), StringF(""), StringF("2")).sconcat(),
		)
	}
}

data class IntPlusSg(val i: Int): Semigroup<IntPlusSg> {
	override fun concatWith(other: IntPlusSg) = IntPlusSg(i + other.i)
}
