package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.MonadPlusLaws
import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.TraversableLaws
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.asOptional
import kotlin.test.Test


class SequenceFTest:
	MonadPlusLaws<SequenceContext>,
	MonadZipLaws<SequenceContext>,
	TraversableLaws<SequenceContext> {
	override val traversableScope = SequenceF
	override val monadScope = SequenceF

	override val possibilities = 10
	override fun factory(possibility: Int) = createSequence(possibility)

	override fun <A> createTraversable(vararg items: A) =
		items.asSequence().f()

	override fun <A> Context<SequenceContext, A>.equalTo(other: Context<SequenceContext, A>) =
		asSequence.toList() == other.asSequence.toList()

	@Test
	fun traverse() {
		assertEquals(
			listOf("5", "7", "3"),
			SequenceF.of(3, 5, 1)
				.traverse(Optional) { a -> Optional.just("${a + 2}") }
				.asOptional
				.orNull()
				?.toList()
		)

		assertEquals(
			Optional.empty<String>(),
			SequenceF.of(3, 5, 1)
				.traverse(Optional) { a ->
					if (a >= 5) Optional.empty()
					else Optional.just("${a + 2}")
				}
		)
	}
}
