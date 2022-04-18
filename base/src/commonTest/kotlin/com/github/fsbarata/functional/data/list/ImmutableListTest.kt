package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.assertEquals
import kotlin.test.Test

internal abstract class ImmutableListTest {
	abstract fun empty(): ImmutableList<Int>?
	abstract fun of(item1: Int, vararg items: Int): ImmutableList<Int>


	@Test
	fun drop() {
		assertEquals(ListF.of(1, 2, 3, 5), of(1, 2, 3, 5).drop(0))
		assertEquals(ListF.of(3, 5), of(1, 2, 3, 5).drop(2))
		assertEquals(ListF.empty<Int>(), of(1, 2, 3, 5).drop(4))
		assertEquals(ListF.empty<Int>(), of(1, 2, 3, 5).drop(6))

		assertEquals(ListF.empty<Int>(), empty()?.drop(0) ?: ListF.empty<Int>())
		assertEquals(ListF.empty<Int>(), empty()?.drop(2) ?: ListF.empty<Int>())
	}

	@Test
	fun takeLast() {
		assertEquals(ListF.of(1, 2, 3, 5), of(1, 2, 3, 5).takeLast(6))
		assertEquals(ListF.of(1, 2, 3, 5), of(1, 2, 3, 5).takeLast(4))
		assertEquals(ListF.of(3, 5), of(1, 2, 3, 5).takeLast(2))
		assertEquals(ListF.empty<Int>(), of(1, 2, 3, 5).takeLast(0))

		assertEquals(ListF.empty<Int>(), empty()?.takeLast(0) ?: ListF.empty<Int>())
		assertEquals(ListF.empty<Int>(), empty()?.takeLast(2) ?: ListF.empty<Int>())
	}

	@Test
	fun take() {
		assertEquals(ListF.empty<Int>(), of(1, 2, 3, 5).take(0))
		assertEquals(ListF.of(1, 2), of(1, 2, 3, 5).take(2))
		assertEquals(ListF.of(1, 2, 3, 5), of(1, 2, 3, 5).take(6))

		assertEquals(ListF.empty<Int>(), empty()?.take(0) ?: ListF.empty<Int>())
		assertEquals(ListF.empty<Int>(), empty()?.take(2) ?: ListF.empty<Int>())
	}

	@Test
	fun dropLast() {
		assertEquals(ListF.empty<Int>(), of(1, 2, 3, 5).dropLast(6))
		assertEquals(ListF.empty<Int>(), of(1, 2, 3, 5).dropLast(4))
		assertEquals(ListF.of(1, 2), of(1, 2, 3, 5).dropLast(2))
		assertEquals(ListF.of(1, 2, 3, 5), of(1, 2, 3, 5).dropLast(0))

		assertEquals(ListF.empty<Int>(), empty()?.dropLast(0) ?: ListF.empty<Int>())
		assertEquals(ListF.empty<Int>(), empty()?.dropLast(2) ?: ListF.empty<Int>())
	}

	@Test
	fun sliceRange() {
		assertEquals(ListF.empty<Int>(), of(1, 2, 3, 5).slice(IntRange(1, 0)))
		assertEquals(ListF.of(1, 2), of(1, 2, 3, 5).slice(0..1))
		assertEquals(ListF.of(1, 2, 3, 5), of(1, 2, 3, 5).slice(0..3))

		assertEquals(ListF.empty<Int>(), empty()?.slice(IntRange(1, 0)) ?: ListF.empty<Int>())
	}

	@Test
	fun sliceList() {
		assertEquals(ListF.empty<Int>(), of(1, 2, 3, 5).slice(ListF.empty()))
		assertEquals(ListF.of(1, 2), of(1, 2, 3, 5).slice(ListF.of(0, 1)))
		assertEquals(ListF.of(1, 3, 5), of(1, 2, 3, 5).slice(ListF.of(0, 2, 3)))
		assertEquals(ListF.of(2, 5), of(1, 2, 3, 5).slice(ListF.of(0, 1, 3)).slice(ListF.of(1, 2)))
		assertEquals(ListF.of(2, 5), of(1, 2, 3, 5).slice(ListF.of(0, 1, 3)).slice(1..2))

		assertEquals(ListF.empty<Int>(), empty()?.slice(ListF.empty()) ?: ListF.empty<Int>())
	}

	@Test
	fun chunked() {
		assertEquals(ListF.of(listOf(1), listOf(2), listOf(3), listOf(5)), of(1, 2, 3, 5).chunked(1))
		assertEquals(ListF.of(listOf(1, 2), listOf(3, 5)), of(1, 2, 3, 5).chunked(2))
		assertEquals(ListF.of(listOf(1, 2), listOf(3, 5), listOf(6)), of(1, 2, 3, 5, 6).chunked(2))

		assertEquals(ListF.empty<Int>(), empty()?.chunked(1) ?: ListF.empty<Int>())
		assertEquals(ListF.empty<Int>(), empty()?.chunked(2) ?: ListF.empty<Int>())
	}

	@Test
	fun windowed() {
		assertEquals(ListF.of(listOf(1), listOf(2), listOf(3), listOf(5)), of(1, 2, 3, 5).windowed(1, 1))
		assertEquals(ListF.empty<Int>(), of(1, 2, 3, 5).windowed(5, 5))
		assertEquals(ListF.of(listOf(1, 2, 3, 5)), of(1, 2, 3, 5).windowed(5, 5, true))
		assertEquals(ListF.of(listOf(1, 2), listOf(2, 3), listOf(3, 5)), of(1, 2, 3, 5).windowed(2, 1))
		assertEquals(ListF.of(listOf(1, 2), listOf(2, 3), listOf(3, 5), listOf(5)), of(1, 2, 3, 5).windowed(2, 1, true))
		assertEquals(ListF.of(listOf(1), listOf(3)), of(1, 2, 3, 5).windowed(1, 2))
		assertEquals(ListF.of(listOf(1), listOf(3)), of(1, 2, 3, 5).windowed(1, 2, true))
		assertEquals(ListF.of(listOf(1), listOf(3), listOf(6)), of(1, 2, 3, 5, 6).windowed(1, 2))
		assertEquals(ListF.of(listOf(1, 2), listOf(3, 5)), of(1, 2, 3, 5, 6).windowed(2, 2))
		assertEquals(ListF.of(listOf(1, 2), listOf(3, 5), listOf(6)), of(1, 2, 3, 5, 6).windowed(2, 2, true))

		assertEquals(ListF.empty<Int>(), empty()?.windowed(1, 1) ?: ListF.empty<Int>())
		assertEquals(ListF.empty<Int>(), empty()?.windowed(1, 2) ?: ListF.empty<Int>())
		assertEquals(ListF.empty<Int>(), empty()?.windowed(2, 1) ?: ListF.empty<Int>())
	}
}