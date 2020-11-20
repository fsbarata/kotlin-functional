package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.data.sequence.nonEmptySequence
import org.junit.Assert.assertEquals
import org.junit.Test

class NesExtensionsKtTest {
	@Test
	fun plusNesElement() {
		assertEquals(
			nelOf(3, 5, 2),
			sequenceOf(3, 5).plusElementNes(2).toList()
		)

		assertEquals(
			nelOf(2),
			emptySequence<Int>().plusElementNes(2).toList()
		)
	}

	@Test
	fun plusNes() {
		assertEquals(
			nelOf(3, 5, 5),
			sequenceOf(3, 5).plusNes(nonEmptySequence(5) { null }).toList()
		)

		assertEquals(
			nelOf(2),
			emptySequence<Int>().plusNes(nonEmptySequence(2) { null }).toList()
		)
	}

	@Test
	fun scanNes() {
		sequence<Int> { throw IllegalStateException() }
			.scanNes(1) { a, b -> a + b }

		assertEquals(
			nelOf(1, 4, 9, 16),
			NonEmptySequence { nelOf(3, 5, 7).iterator() }
				.scanNes(1) { a, b -> a + b }
				.toList()
		)
	}
}