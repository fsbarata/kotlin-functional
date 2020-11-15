package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.test.MonadLaws
import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.test.SemigroupLaws
import com.github.fsbarata.functional.data.test.TraversableLaws
import org.junit.Assert.assertEquals
import org.junit.Test

class ListFTest:
	MonadLaws<ListF<*>>,
	MonadZipLaws<ListF<*>>,
	SemigroupLaws<ListF<Int>>,
	TraversableLaws<ListF<*>> {
	override val traversableScope = ListF
	override val monadScope = ListF

	override val possibilities = 5
	override fun factory(possibility: Int) = (0..possibility).map { it - 3 }.f()

	override fun <A> createFunctor(a: A) = ListF.just(a)

	override fun <A> createTraversable(vararg items: A) =
		items.toList().f()

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
	}
}
