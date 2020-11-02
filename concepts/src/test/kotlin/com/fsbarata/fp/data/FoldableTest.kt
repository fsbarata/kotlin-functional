package com.fsbarata.fp.data

import org.junit.Assert.assertEquals
import org.junit.Test

class FoldableTest {
	val list = listOf(5, 7, 2)
	val foldableList1 = object: Foldable<Int> {
		override fun <M> foldMap(monoid: Monoid<M>, f: (Int) -> M): M =
			list.map(f).fold(monoid.empty, monoid::combine)
	}
	val foldableList2 = object: Foldable<Int> {
		override fun <R> foldL(initialValue: R, accumulator: (R, Int) -> R): R =
			list.fold(initialValue, accumulator)
	}

	val v = "1"
	val lf = { b: String, a: Int -> b + a }
	val rf = { a: Int, b: String -> b + a }
	val monoid = Semigroup { a1: String, a2: String -> a1 + a2 }.monoid(v)

	@Test
	fun foldL() {
		assertEquals("1572", foldableList1.foldL(v, lf))
	}

	@Test
	fun foldMap() {
		assertEquals("1572", foldableList2.foldMap(monoid) { it.toString() })
	}

	@Test
	fun foldR() {
		assertEquals("1275", foldableList1.foldR(v, rf))
		assertEquals("1275", foldableList2.foldR(v, rf))
	}
}
