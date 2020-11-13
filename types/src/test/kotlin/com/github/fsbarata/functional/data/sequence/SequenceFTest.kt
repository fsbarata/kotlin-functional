package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.control.test.MonadLaws
import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.asOptional
import com.github.fsbarata.functional.data.test.TraversableLaws
import org.junit.Assert.assertEquals
import org.junit.Test


class SequenceFTest: MonadLaws<SequenceContext>,
	MonadZipLaws<SequenceContext>,
	TraversableLaws<SequenceContext> {
	override val traversableScope = SequenceF
	override val monadScope = SequenceF

	override fun <A> createFunctor(a: A) = SequenceF.just(a)

	override fun <A> createTraversable(vararg items: A) =
		items.asSequence().f()

	override fun <A> Functor<SequenceContext, A>.equalTo(other: Functor<SequenceContext, A>) =
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
