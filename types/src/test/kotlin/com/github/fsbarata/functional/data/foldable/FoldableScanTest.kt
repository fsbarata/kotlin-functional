package com.github.fsbarata.functional.data.foldable

import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.string.StringF
import org.junit.Assert.assertEquals
import org.junit.Test

class FoldableScanTest {
	@Test
	fun scanL() {
		assertEquals(
			nelOf(2, 5, 10, 11),
			nelOf(3, 5, 1).scanL(2, Int::plus),
		)
	}

	@Test
	fun scan() {
		assertEquals(
			nelOf(StringF("0"), StringF("03"), StringF("035"), StringF("0351")),
			nelOf(StringF("3"), StringF("5"), StringF("1")).scan(StringF("0")),
		)
	}
}