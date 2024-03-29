package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.nelOf
import kotlin.test.Test

class FoldableTest {
	val list = listOf(5, 7, 2)
	val foldableList1 = object: Foldable<Int> {
		override fun <M> foldMap(monoid: Monoid<M>, f: (Int) -> M): M =
			list.map(f).fold(monoid.empty, monoid::concat)
	}
	val foldableList2 = object: Foldable<Int> {
		override fun <R> foldL(initialValue: R, accumulator: (R, Int) -> R): R =
			list.fold(initialValue, accumulator)
	}

	val v = "1"
	val lf = { b: String, a: Int -> b + a }
	val rf = { a: Int, b: String -> b + a }

	data class StringConcatSg(val str: String): Semigroup<StringConcatSg> {
		override fun concatWith(other: StringConcatSg) = StringConcatSg(str + other.str)
	}

	@Test
	fun foldL() {
		assertEquals("1572", foldableList1.foldL(v, lf))
	}

	@Test
	fun foldMap() {
		assertEquals("572", foldableList2.foldMap(monoid(StringConcatSg(""))) {
			StringConcatSg(it.toString())
		}.str)
	}

	@Test
	fun foldR() {
		assertEquals("1275", foldableList1.foldR(v, rf))
		assertEquals("1275", foldableList2.foldR(v, rf))
	}

	@Test
	fun scanL() {
		assertEquals(
			nelOf(2, 5, 10, 11),
			nelOf(3, 5, 1).scanL(2, Int::plus),
		)
	}

	@Test
	fun scanL_semigroup() {
		assertEquals(
			nelOf(StringConcatSg("0"), StringConcatSg("03"), StringConcatSg("035"), StringConcatSg("0351")),
			nelOf(StringConcatSg("3"), StringConcatSg("5"), StringConcatSg("1")).scanL(StringConcatSg("0")),
		)
	}

	@Test
	fun asum() {
		assertEquals(
			ListF.of(3, 3, 5, 2, 1),
			nelOf(
				ListF.just(3),
				ListF.of(3, 5),
				ListF.empty(),
				ListF.of(2, 1)
			).asum(ListF)
		)
	}
}
