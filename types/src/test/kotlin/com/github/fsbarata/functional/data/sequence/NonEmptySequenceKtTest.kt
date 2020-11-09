package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.control.test.MonadLaws
import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.test.FoldableLaws
import com.github.fsbarata.functional.iterators.NonEmptyIterator
import org.junit.Assert.assertEquals
import org.junit.Test

class NonEmptySequenceKtTest: MonadLaws<NonEmptySequence<*>>, MonadZipLaws<NonEmptySequence<*>>, FoldableLaws {
	override val monadScope = NonEmptySequence
	override fun <A> Functor<NonEmptySequence<*>, A>.equalTo(other: Functor<NonEmptySequence<*>, A>): Boolean =
		asNes.toList() == other.asNes.toList()

	override fun <A> createFoldable(vararg items: A): Foldable<A> =
		nonEmptySequenceOf(items.first(), items.drop(1))

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
			generateSequence(null as Int?) { null }.nonEmpty {
				NonEmptyIterator(11,
					emptySequence<Int>().iterator())
			}.toList()
		)
	}
}