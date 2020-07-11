package com.fsbarata.fp.concepts

import org.junit.Assert.assertEquals
import org.junit.Test

class SemigroupKtTest {
	data class SInt(val value: Int) : Semigroup<SInt> {
		override fun combine(a: SInt) = SInt(value + a.value)
	}

	@Test
	fun semigroup_default_times() {
		assertEquals(SInt(3), SInt(3) * 1)
		assertEquals(SInt(45), SInt(3) * 15)
	}
}
