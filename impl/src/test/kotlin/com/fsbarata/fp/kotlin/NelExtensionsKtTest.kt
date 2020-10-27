package com.fsbarata.fp.kotlin

import com.fsbarata.fp.types.NonEmptyList
import org.junit.Assert.*
import org.junit.Test

class NelExtensionsKtTest {
	val nel1 = NonEmptyList.just(9)
	val nel2 = NonEmptyList.of(5, 1, 3)
	val nel3 = NonEmptyList.of(2, NonEmptyList.of(4, 2, 5))


	@Test
	fun plusNel() {
		assertEquals(NonEmptyList.of(9, 3), listOf(9).plusNel(3))
		assertEquals(NonEmptyList.of(5, 1, 3, 3), listOf(5, 1, 3).plusNel(3))
		assertEquals(NonEmptyList.of(5, 1, 3, 3), listOf(5, 1).plusNel(NonEmptyList.of(3, 3)))
		assertEquals(NonEmptyList.just(5), emptyList<Int>().plusNel(5))
		assertEquals(NonEmptyList.just(5), emptyList<Int>().plusNel(NonEmptyList.just(5)))
	}

	@Test
	fun scanNel() {
		assertEquals(NonEmptyList.of(-4, 3), nel1.scanNel(-4) { acc, i -> acc + i - 2 })
		assertEquals(NonEmptyList.of(-4, -1, -2, -1), nel2.scanNel(-4) { acc, i -> acc + i - 2 })
		assertEquals(NonEmptyList.of(-4, -4, -2, -2, 1), nel3.scanNel(-4) { acc, i -> acc + i - 2 })
	}
}