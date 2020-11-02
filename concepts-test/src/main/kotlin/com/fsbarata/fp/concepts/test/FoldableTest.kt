package com.fsbarata.fp.concepts.test

import com.fsbarata.fp.data.Foldable
import com.fsbarata.fp.data.Monoid
import org.junit.Assert.assertEquals
import org.junit.Test

interface FoldableTest {
	fun createFoldable(): Foldable<Int> = createFoldable(5, 8, 1)

	fun createFoldable(item1: Int, item2: Int, item3: Int): Foldable<Int> =
		throw NotImplementedError()

	@Test
	fun `foldL and foldMap consistency`() {
		val foldable = createFoldable()
		val s1 = foldable.foldL("3") { a, b -> a + b }
		val s2 = object: Foldable<Int> {
			override fun <M> foldMap(monoid: Monoid<M>, f: (Int) -> M): M =
				foldable.foldMap(monoid, f)
		}.foldL("3") { a, b -> a + b }

		assertEquals(s2, s1)
	}

	@Test
	fun `foldR and foldMap consistency`() {
		val foldable = createFoldable()
		val s1 = foldable.foldR("3") { a, b -> a.toString() + b }
		val s2 = object: Foldable<Int> {
			override fun <M> foldMap(monoid: Monoid<M>, f: (Int) -> M): M =
				foldable.foldMap(monoid, f)
		}.foldR("3") { a, b -> a.toString() + b }

		assertEquals(s2, s1)
	}
}
