package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.sequence.nonEmptySequence
import kotlin.test.Test

class SequencesKtTest {
	@Test
	fun plusElementNe() {
		assertEquals(
			nelOf(3, 5, 2),
			sequenceOf(3, 5).plusElementNe(2).toList()
		)

		assertEquals(
			nelOf(2),
			emptySequence<Int>().plusElementNe(2).toList()
		)
	}

	@Test
	fun plusNe() {
		assertEquals(
			nelOf(3, 5, 5),
			sequenceOf(3, 5).plusNe(nonEmptySequence(5) { null }).toList()
		)

		assertEquals(
			nelOf(2),
			emptySequence<Int>().plusNe(nonEmptySequence(2) { null }).toList()
		)
	}

	@Test
	fun windowedNel() {
		assertEquals(emptyList<Int>(), emptySequence<Int>().windowedNel(1).toList())
		assertEquals(emptyList<Int>(), emptySequence<Int>().windowedNel(1, partialWindows = true).toList())

		assertEquals(
			listOf(nelOf(6, 3), nelOf(1, 2)),
			sequenceOf(6, 3, 1, 2, 5).windowedNel(2, step = 2, partialWindows = false).toList()
		)
		assertEquals(
			listOf(nelOf(6, 3), nelOf(1, 2), nelOf(5)),
			sequenceOf(6, 3, 1, 2, 5).chunked(2).toList()
		)
		assertEquals(
			listOf(nelOf(6), nelOf(2)),
			sequenceOf(6, 3, 1, 2, 5).windowedNel(1, step = 3, partialWindows = true).toList()
		)
	}

	@Test
	fun scanNe() {
		sequence<Int> { throw IllegalStateException() }
			.scanNe(1) { a, b -> a + b }

		assertEquals(
			nelOf(1, 4, 9, 16),
			nelOf(3, 5, 7).asSequence()
				.scanNe(1) { a, b -> a + b }
				.toList()
		)
	}
}