package com.fsbarata.fp.types

import org.junit.Assert.assertEquals
import org.junit.Test

class NonEmptySequenceKtTest {
	@Test
	fun `non empty sequence from iterator`() {
		assertEquals(
				NonEmptyList.of(3, 5, 7),
				NonEmptySequence { NonEmptyList.of(3, 5, 7).iterator() }.toList()
		)
	}

	@Test
	fun map() {
		assertEquals(
				NonEmptyList.of(8, 10, 12),
				NonEmptySequence { nelOf(3, 5, 7).iterator() }
						.map { it + 5 }
						.toList()
		)
	}

	@Test
	fun `non empty sequence will yield values`() {
		assertEquals(
				NonEmptyList.of(3, 5, 7),
				nonEmptySequence(3) { if (it < 6) it + 2 else null }.toList()
		)
	}

	@Test
	fun `convert sequence to nonempty`() {
		assertEquals(
				NonEmptyList.of(3, 5, 7),
				generateSequence(3) { if (it < 6) it + 2 else null }.nonEmpty { throw NoSuchFieldException() }.toList()
		)

		assertEquals(
				NonEmptyList.of(11),
				generateSequence(null as Int?) { null }.nonEmpty(nonEmptySequence(11) { null }).toList()
		)

		assertEquals(
				NonEmptyList.of(11),
				generateSequence(null as Int?) { null }.nonEmpty { NonEmptyIterator(11, emptySequence<Int>().iterator()) }.toList()
		)
	}
}