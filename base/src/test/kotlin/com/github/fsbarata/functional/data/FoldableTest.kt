package com.github.fsbarata.functional.data

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

	class StringSemigroup(val str: String): Semigroup<StringSemigroup> {
		override fun combineWith(other: StringSemigroup) = StringSemigroup(str + other.str)
	}

	@Test
	fun foldL() {
		assertEquals("1572", foldableList1.foldL(v, lf))
	}

	@Test
	fun foldMap() {
		assertEquals("572", foldableList2.foldMap(monoid(StringSemigroup(""))) {
			StringSemigroup(it.toString())
		}.str)
	}

	@Test
	fun foldR() {
		assertEquals("1275", foldableList1.foldR(v, rf))
		assertEquals("1275", foldableList2.foldR(v, rf))
	}
}
