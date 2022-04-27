package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.set.nesOf
import kotlin.test.Test

class IterablesKtTest {
	private val nel1 = NonEmptyList.just(9)
	private val nel2 = nelOf(5, 1, 3)
	private val nel3 = NonEmptyList.of(2, nelOf(4, 2, 5))

	@Test
	fun plusElementNel() {
		assertEquals(nelOf(9, 3), listOf(9).plusElementNel(3))
		assertEquals(nelOf(5, 1, 3, 3), listOf(5, 1, 3).plusElementNel(3))
		assertEquals(nelOf(5), emptyList<Int>().plusElementNel(5))
	}

	@Test
	fun plusNel() {
		assertEquals(nelOf(5, 1, 3, 3), listOf(5, 1).plusNel(nelOf(3, 3)))
		assertEquals(nelOf(5), emptyList<Int>().plusNel(NonEmptyList.just(5)))
	}

	@Test
	fun plusElementNes() {
		assertEquals(nesOf(9, 3), setOf(9).plusElementNes(3))
		assertEquals(nesOf(5, 1, 3, 3), setOf(5, 1, 3).plusElementNes(3))
		assertEquals(nesOf(5), emptySet<Int>().plusElementNes(5))
	}

	@Test
	fun plusNes() {
		assertEquals(nesOf(5, 1, 3, 3), setOf(5, 1).plusNes(nesOf(3, 3)))
		assertEquals(nesOf(5), emptySet<Int>().plusNes(NonEmptyList.just(5)))
	}

	@Test
	fun groupByNel() {
		assertEquals(emptyMap<String, Int>(), emptySet<Int>().groupByNel { "$it" })
		assertEquals(
			emptyMap<String, Long>(),
			emptySet<Int>().groupByNel(keySelector = { "$it" }, valueTransform = { 1L + it })
		)

		assertEquals(
			mapOf(true to nelOf(5), false to nelOf(2, 1)),
			listOf(5, 2, 1).groupByNel { it > 4 }
		)
		assertEquals(
			mapOf(true to nelOf("6"), false to nelOf("3", "2")),
			listOf(5, 2, 1).groupByNel(keySelector = { it > 4 }, valueTransform = { "${it + 1}" })
		)
	}

	@Test
	fun windowedNel() {
		assertEquals(emptyList<Int>(), emptyList<Int>().windowedNel(1))
		assertEquals(emptyList<Int>(), emptyList<Int>().windowedNel(1, partialWindows = true))

		assertEquals(
			listOf(nelOf(6, 3), nelOf(1, 2)),
			listOf(6, 3, 1, 2, 5).windowedNel(2, step = 2, partialWindows = false)
		)
		assertEquals(
			listOf(nelOf(6, 3), nelOf(1, 2), nelOf(5)),
			listOf(6, 3, 1, 2, 5).chunkedNel(2)
		)
		assertEquals(
			listOf(nelOf(6), nelOf(2)),
			listOf(6, 3, 1, 2, 5).windowedNel(1, step = 3, partialWindows = true)
		)
	}

	@Test
	fun scanNel() {
		assertEquals(nelOf(-2), emptyList<Nothing>().scanNel(-2) { acc, _ -> acc })
		assertEquals(nelOf(-4, 3), listOf(9).scanNel(-4) { acc, i -> acc + i - 2 })
		assertEquals(nelOf(-4, -4, -2, -2, 1), listOf(2, 4, 2, 5).scanNel(-4) { acc, i -> acc + i - 2 })
	}
}