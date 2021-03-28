package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.MonadPlusLaws
import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.TraversableLaws
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.validation.Validation
import com.github.fsbarata.functional.data.validation.ValidationApplicativeScope
import com.github.fsbarata.functional.data.validation.asValidation
import org.junit.Assert.assertEquals
import org.junit.Test

class ListFTest:
	MonadPlusLaws<ListContext>,
	MonadZipLaws<ListContext>,
	TraversableLaws<ListContext> {
	override val traversableScope = ListF
	override val monadScope = ListF

	override val possibilities = 10
	override fun factory(possibility: Int) = createList(possibility)

	override fun <A> createTraversable(vararg items: A) =
		items.toList().f()

	@Test
	fun lift2() {
		assertEquals(
			listOf(1.3, 2.2, 2.3, 3.2, 3.3, 4.2),
			ListF.of(1, 2, 3).lift2(ListF.of(0.3, 1.2), Int::plus)
		)
	}

	@Test
	fun ap() {
		assertEquals(
			listOf(3, 4, 5, 2, 1, 0),
			ListF.of(1, 2, 3).ap(ListF.of({ a: Int -> a + 2 }, { a: Int -> 3 - a }))
		)
	}

	@Test
	fun traverse() {
		assertEquals(
			Optional.just(listOf("5", "7", "3")),
			ListF.of(3, 5, 1)
				.traverse(Optional) { a -> Optional.just("${a + 2}") }
		)

		assertEquals(
			Optional.empty<String>(),
			ListF.of(3, 5, 1)
				.traverse(Optional) { a ->
					if (a >= 5) Optional.empty()
					else Optional.just("${a + 2}")
				}
		)

		assertEquals(
			Validation.success<NonEmptyList<String>, Int>(2),
			ListF.of(5, 2, 1)
				.traverse(ValidationApplicativeScope()) { a ->
					when {
						a < 1 -> Validation.Failure(NonEmptyList.just("a"))
						a < 3 -> Validation.success(1)
						else -> Validation.success(0)
					}
				}
				.asValidation
				.map { it.sum() }
		)
	}
}

