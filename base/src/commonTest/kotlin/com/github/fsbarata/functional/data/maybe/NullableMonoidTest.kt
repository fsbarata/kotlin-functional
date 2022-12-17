package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.MonoidLaws
import com.github.fsbarata.functional.data.monoid.concatStringMonoid
import kotlin.test.Test

class NullableMonoidTest: MonoidLaws<String?>(
	nullableMonoid(concatStringMonoid()),
) {
	override val possibilities: Int = 3
	override fun factory(possibility: Int) =
		if (possibility == 0) null
		else "$possibility"

	@Test
	fun concats() {
		with(nullableMonoid(concatStringMonoid())) {
			assertEquals(null, concat(null, null))
			assertEquals("5", concat("5", null))
			assertEquals("2", concat(null, "2"))
			assertEquals("42", concat("4", "2"))
			assertEquals("42", "4".concatWith("2"))
		}
	}
}