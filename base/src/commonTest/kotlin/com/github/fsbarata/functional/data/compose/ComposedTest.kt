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
			listOf(Some(3), None, None, Some(5), Some(7)),
			ListF.of(Some(6), None, None, Some(11), Some(14))
				.mapComposed { it / 2 }
		)
	}
}
