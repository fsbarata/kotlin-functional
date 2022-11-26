package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.monoid.concatStringMonoid
import com.github.fsbarata.functional.data.monoid.dual
import com.github.fsbarata.functional.data.monoid.productLongMonoid
import com.github.fsbarata.functional.data.monoid.sumIntMonoid
import kotlin.test.Test

class SemigroupTest {
	data class StringConcatSg(val str: String): Semigroup<StringConcatSg> {
		override fun concatWith(other: StringConcatSg) = StringConcatSg(str + other.str)
	}

	@Test
	fun stimes() {
		assertEquals(3, sumIntMonoid().stimes(3, 1))
		assertEquals(45, sumIntMonoid().stimes(3, 15))
		assertEquals(243L, productLongMonoid().stimes(3, 5)) // = 3^5
		assertEquals(StringConcatSg("4a4a4a4a"), StringConcatSg("4a").stimes(4))
		assertEquals("4a4a4a4a", concatStringMonoid().stimes("4a", 4))
	}

	@Test
	fun dual() {
		assertEquals(StringConcatSg("5a3w"), StringConcatSg("3w").dual().concatWith(StringConcatSg("5a").dual()).get)
	}

	@Test
	fun sconcat() {
		assertEquals(
			"5ag2",
			nelOf("5a", "g", "", "2").sconcat(concatStringMonoid()),
		)
	}
}
