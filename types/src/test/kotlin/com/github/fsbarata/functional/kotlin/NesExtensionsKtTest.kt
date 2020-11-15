package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import org.junit.Assert.assertEquals
import org.junit.Test

class NesExtensionsKtTest {
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