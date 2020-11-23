package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.data.FunctorLaws
import com.github.fsbarata.functional.data.list.ListContext
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.createList
import com.github.fsbarata.functional.data.maybe.None
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.OptionalContext
import com.github.fsbarata.functional.data.maybe.Some
import org.junit.Assert.assertEquals
import org.junit.Test

class ComposedTest: FunctorLaws<ComposeContext<ListContext, OptionalContext>> {
	override val possibilities: Int = 10
	override fun factory(possibility: Int) =
		createList(possibility).map { if (it > 2) Optional.empty() else Optional.just(it) }
			.compose()

	@Test
	fun `compose maps underlying functor`() {
		assertEquals(
			listOf(Some(3), None, None, Some(5), Some(7)),
			ListF.of(Some(6), None, None, Some(11), Some(14))
				.compose()
				.map { it / 2 }
				.decompose<Optional<Int>>()
		)
	}
}