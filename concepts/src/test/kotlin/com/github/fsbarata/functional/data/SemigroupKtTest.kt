package com.github.fsbarata.functional.data

import org.junit.Assert.assertEquals
import org.junit.Test

class SemigroupKtTest {
	data class XInt(val i: Int): Semigroup<XInt> {
		override fun combineWith(other: XInt) = XInt(i + other.i)
	}

	@Test
	fun semigroup_default_times() {
		assertEquals(XInt(3), XInt(3).times(1))
		assertEquals(XInt(45), XInt(3).times(15))
	}
}
