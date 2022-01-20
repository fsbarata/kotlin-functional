package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.FoldableLaws
import com.github.fsbarata.functional.data.TraversableLaws
import com.github.fsbarata.functional.data.list.nelOf
import kotlin.test.Test

class NonEmptySequenceKtTest:
	MonadZipLaws<NonEmptySequenceContext>,
	TraversableLaws<NonEmptySequenceContext>,
	FoldableLaws {
	override val monadScope = NonEmptySequence
	override val traversableScope = NonEmptySequence
	override fun <A> Context<NonEmptySequenceContext, A>.equalTo(other: Context<NonEmptySequenceContext, A>): Boolean =
		asNes.toList() == other.asNes.toList()

	override fun <A> Context<NonEmptySequenceContext, A>.describe() = asNes.toList().toString()

	override val possibilities: Int = 10
	override fun factory(possibility: Int) = createNes(possibility)

	override fun <A> createTraversable(vararg items: A) =
		NonEmptySequence.of(items.first(), items.drop(1))

	@Test
	fun map() {
		assertEquals(
			nelOf(8, 10, 12),
			nelOf(3, 5, 7).asSequence()
				.map { it + 5 }
				.toList()
		)
	}

	@Test
	fun `non empty sequence will yield values`() {
		assertEquals(
			nelOf(3, 5, 7),
			nonEmptySequence(3) { if (it < 6) it + 2 else null }.toList()
		)
	}

	@Test
	fun `convert sequence to nonempty`() {
		assertEquals(
			nelOf(3, 5, 7),
			generateSequence(3) { if (it < 6) it + 2 else null }.nonEmpty { throw NoSuchElementException() }.toList()
		)

		assertEquals(
			nelOf(11),
			generateSequence(null as Int?) { null }.nonEmpty(nonEmptySequence(11) { null }).toList()
		)

		assertEquals(
			nelOf(11),
			generateSequence(null as Int?) { null }.nonEmpty {
				NonEmptySequence.of(11, emptySequence<Int>())
			}.toList()
		)
	}
}