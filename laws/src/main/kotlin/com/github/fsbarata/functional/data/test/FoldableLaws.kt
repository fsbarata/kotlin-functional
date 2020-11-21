package com.github.fsbarata.functional.data.test

import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.Monoid
import org.junit.Assert.assertEquals
import org.junit.Test

interface FoldableLaws {
	fun <A> createFoldable(vararg items: A): Foldable<A>

	@Test
	fun `fold empty`() {
		val foldable = try {
			createFoldable<Int>()
		} catch (error: Throwable) {
			return
		}

		assertEquals("3", foldable.foldL("3") { _, _ -> throw IllegalStateException() })
		assertEquals("8", foldable.foldR("8") { _, _ -> throw IllegalStateException() })
	}

	@Test
	fun `foldL and foldMap consistency`() {
		val foldable = createFoldable(5, 8, 1)
		val s1 = foldable.foldL("3") { a, b -> a + b }
		val s2 = object: Foldable<Int> {
			override fun <M> foldMap(monoid: Monoid<M>, f: (Int) -> M): M =
				foldable.foldMap(monoid, f)
		}.foldL("3") { a, b -> a + b }

		assertEquals(s2, s1)
	}

	@Test
	fun `foldR and foldMap consistency`() {
		val foldable = createFoldable(5, 8, 1)
		val s1 = foldable.foldR("3") { a, b -> a.toString() + b }
		val s2 = object: Foldable<Int> {
			override fun <M> foldMap(monoid: Monoid<M>, f: (Int) -> M): M =
				foldable.foldMap(monoid, f)
		}.foldR("3") { a, b -> a.toString() + b }

		assertEquals(s2, s1)
	}
}
