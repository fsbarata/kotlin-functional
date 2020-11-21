package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.control.MonadPlusLaws
import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.asOptional
import com.github.fsbarata.functional.data.SemigroupLaws
import com.github.fsbarata.functional.data.TraversableLaws
import org.junit.Assert.assertEquals
import org.junit.Test


class SequenceFTest:
	MonadPlusLaws<SequenceContext>,
	MonadZipLaws<SequenceContext>,
	TraversableLaws<SequenceContext>,
	SemigroupLaws<SequenceF<Int>> {
	override val traversableScope = SequenceF
	override val monadScope = SequenceF

	override val possibilities = 5
	override fun factory(possibility: Int) = (0..possibility).asSequence().map { it - 3 }.f()

	override fun <A> createFunctor(a: A) = SequenceF.just(a)

	override fun <A> createTraversable(vararg items: A) =
		items.asSequence().f()

	override fun <A> Functor<SequenceContext, A>.equalTo(other: Functor<SequenceContext, A>) =
		asSequence.toList() == other.asSequence.toList()

	override fun equals(a1: SequenceF<Int>, a2: SequenceF<Int>) =
		a1.toList() == a2.toList()

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
