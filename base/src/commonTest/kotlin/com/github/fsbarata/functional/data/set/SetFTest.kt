package com.github.fsbarata.functional.data.set

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.MonadPlusLaws
import com.github.fsbarata.functional.data.TraversableLaws
import com.github.fsbarata.functional.data.list.createList
import com.github.fsbarata.functional.data.maybe.Optional
import kotlin.test.Test

class SetFTest:
	MonadPlusLaws<SetContext>,
	TraversableLaws<SetContext> {
	override val traversableScope = SetF
	override val monadScope = SetF

	override val possibilities = 10
	override fun factory(possibility: Int) = createList(possibility).toSet().f()

	override fun <A> createTraversable(vararg items: A) =
		items.toSet().f()

	@Test
	fun lift2() {
		assertEquals(
			setOf(1.3, 2.2, 2.3, 3.2, 3.3, 4.2),
			SetF.of(1, 2, 3).lift2(SetF.of(0.3, 1.2), Int::plus)
		)
	}

	@Test
	fun ap() {
		assertEquals(
			setOf(3, 4, 5, 2, 1, 0),
			SetF.of(1, 2, 3).ap(SetF.of({ a: Int -> a + 2 }, { a: Int -> 3 - a }))
		)
	}

	@Test
	fun traverse() {
		assertEquals(
			Optional.just(setOf("5", "7", "3")),
			SetF.of(3, 5, 1)
				.traverse(Optional) { a -> Optional.just("${a + 2}") }
		)

		assertEquals(
			Optional.empty<String>(),
			SetF.of(3, 5, 1)
				.traverse(Optional) { a ->
					if (a >= 5) Optional.empty()
					else Optional.just("${a + 2}")
				}
		)
	}
}