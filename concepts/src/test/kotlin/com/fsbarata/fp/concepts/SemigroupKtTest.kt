package com.fsbarata.fp.concepts

import org.junit.Assert.assertEquals
import org.junit.Test

class SemigroupKtTest {
	data class XInt(val i: Int)

	private val XIntSG = object : Semigroup<XInt> {
		override fun XInt.combine(other: XInt) = XInt(i + other.i)
	}

	@Test
	fun semigroup_default_times() {
		with(XIntSG) {
			assertEquals(XInt(3), times(1)(XInt(3)))
			assertEquals(XInt(45), times(15)(XInt(3)))
		}
	}
}
