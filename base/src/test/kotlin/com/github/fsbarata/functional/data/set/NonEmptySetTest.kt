package com.github.fsbarata.functional.data.set

import com.github.fsbarata.functional.data.FoldableLaws
import org.junit.Assert.assertEquals
import org.junit.Test

class NonEmptySetTest: FoldableLaws {
	override fun <A> createFoldable(vararg items: A) =
		items.toSet().toNes()!!

	private val nes1 = NonEmptySet.just(9)
	private val nes2 = nesOf(5, 1, 3)
	private val nes3 = NonEmptySet.of(2, nesOf(4, 2, 5))

	@Test
	fun size() {
		assertEquals(1, nes1.size)
		assertEquals(3, nes2.size)
		assertEquals(3, nes3.size)
	}
}