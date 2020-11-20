package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import org.junit.Assert.assertEquals
import org.junit.Test

class NelExtensionsKtTest {
	private val nel1 = NonEmptyList.just(9)
	private val nel2 = nelOf(5, 1, 3)
	private val nel3 = NonEmptyList.of(2, nelOf(4, 2, 5))

	@Test
	fun plusNel() {
		assertEquals(nelOf(9, 3), listOf(9).plusElementNel(3))
		assertEquals(nelOf(5, 1, 3, 3), listOf(5, 1, 3).plusElementNel(3))
		assertEquals(nelOf(5, 1, 3, 3), listOf(5, 1).plusNel(nelOf(3, 3)))
		assertEquals(nelOf(5), emptyList<Int>().plusElementNel(5))
		assertEquals(nelOf(5), emptyList<Int>().plusNel(NonEmptyList.just(5)))
	}

	@Test
	fun scanNel() {
		assertEquals(nelOf(-4, 3), nel1.scanNel(-4) { acc, i -> acc + i - 2 })
		assertEquals(nelOf(-4, -1, -2, -1), nel2.scanNel(-4) { acc, i -> acc + i - 2 })
		assertEquals(nelOf(-4, -4, -2, -2, 1), nel3.scanNel(-4) { acc, i -> acc + i - 2 })
	}
}