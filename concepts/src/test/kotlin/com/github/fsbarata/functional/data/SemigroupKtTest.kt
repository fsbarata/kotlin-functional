package com.github.fsbarata.functional.data

import org.junit.Assert.assertEquals
import org.junit.Test

class SemigroupKtTest {
	data class XInt(val i: Int)

	private val XIntSG =
		Semigroup<XInt> { a1, a2 -> XInt(a1.i + a2.i) }

	@Test
	fun semigroup_default_times() {
		with(XIntSG) {
			assertEquals(XInt(3), times(1)(XInt(3)))
			assertEquals(XInt(45), times(15)(XInt(3)))
		}
	}
}