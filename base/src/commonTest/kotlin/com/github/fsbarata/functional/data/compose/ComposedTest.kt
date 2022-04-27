package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.maybe.None
import com.github.fsbarata.functional.data.maybe.Some
import kotlin.test.Test

class ComposedTest {
	@Test
	fun `mapComposed maps underlying functor`() {
		assertEquals(
			ListF.of(Some(3), None, None, Some(5), Some(7)),
			ListF.of(Some(6), None, None, Some(11), Some(14))
				.mapComposed { it / 2 }
		)
	}

	@Test
	fun `lift2Composed lifts underlying functor`() {
		assertEquals(
			ListF.of(
				Some(1), None, Some(2),
				None, None, None,
				None, None, None,
				Some(4), None, Some(4),
				Some(5), None, Some(6),
			),
			ListF.of(Some(6), None, None, Some(11), Some(14))
				.lift2Composed(ListF.of(Some(3), None, Some(2))) { a, b -> (a - b) / 2 }
		)
	}
}
