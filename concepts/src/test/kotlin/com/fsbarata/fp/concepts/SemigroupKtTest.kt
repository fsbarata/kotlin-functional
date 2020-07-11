package com.fsbarata.fp.concepts

import org.junit.Assert.assertEquals
import org.junit.Test

class SemigroupKtTest {
	data class XInt(val i: Int)

	object XIntSG : Semigroup<XInt> {
		override fun XInt.combine(a: XInt) = XInt(i + a.i)
	}

	@Test
	fun semigroup_default_times() {
		with(XIntSG) {
			assertEquals(XInt(3), XInt(3) * 1)
			assertEquals(XInt(45), XInt(3) * 15)
		}
	}
}
