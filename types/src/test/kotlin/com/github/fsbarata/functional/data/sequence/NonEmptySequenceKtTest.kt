package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.test.MonadTest
import com.github.fsbarata.functional.control.test.MonadZipTest
import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.test.FoldableTest
import com.github.fsbarata.functional.iterators.NonEmptyIterator
import org.junit.Assert.assertEquals
import org.junit.Test

class NonEmptySequenceKtTest: MonadTest<NonEmptySequence<*>>, MonadZipTest<NonEmptySequence<*>>, FoldableTest {
	override val monadScope = NonEmptySequence
	override fun Monad<NonEmptySequence<*>, Int>.equalTo(other: Monad<NonEmptySequence<*>, Int>): Boolean =
		asNes.toList() == other.asNes.toList()

	override fun createFoldable(item1: Int, item2: Int, item3: Int): Foldable<Int> =
		nonEmptySequenceOf(item1, item2, item3)

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