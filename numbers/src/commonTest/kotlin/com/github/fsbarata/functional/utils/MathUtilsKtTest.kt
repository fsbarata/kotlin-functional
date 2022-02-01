package com.github.fsbarata.functional.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class MathUtilsKtTest {
	@Test
	fun testGreatestCommonDenominator() {
		assertEquals(15L, greatestCommonDenominator(15L, 30L))
		assertEquals(15L, greatestCommonDenominator(30L, 15L))
		assertEquals(5L, greatestCommonDenominator(25L, 10L))
		assertEquals(5L, greatestCommonDenominator(10L, 25L))
		assertEquals(1L, greatestCommonDenominator(12129, 2893))
		assertEquals(5L, greatestCommonDenominator(-10L, 25L))
		assertEquals(5L, greatestCommonDenominator(10L, -25L))
	}
}