package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.test.MonoidLaws
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class ConcatStringMonoidTest: MonoidLaws<String>(
	concatStringMonoid(),
) {
	override val possibilities: Int = 25
	override fun nonEmpty(possibility: Int) = "$possibility"

	@Test
	fun concats() {
		assertEquals("a2bb", concatStringMonoid().combine("a2", "bb"))
	}
}

class SumBigDecimalMonoidTest: MonoidLaws<BigDecimal>(
	sumBigDecimalMonoid(),
) {
	override val possibilities: Int = 25
	override fun nonEmpty(possibility: Int) = BigDecimal("$possibility")

	@Test
	fun adds() {
		assertEquals(BigDecimal("5.8"), sumBigDecimalMonoid().combine(BigDecimal("1.3"), BigDecimal("4.5")))
	}
}

class ProductBigDecimalMonoidTest: MonoidLaws<BigDecimal>(
	productBigDecimalMonoid(),
) {
	override val possibilities: Int = 25
	override fun nonEmpty(possibility: Int) = BigDecimal("$possibility")

	@Test
	fun multiplies() {
		assertEquals(BigDecimal("6.75"),
			productBigDecimalMonoid().combine(BigDecimal("1.5"), BigDecimal("4.5")))
	}
}

class ConcatArrayMonoidTest: MonoidLaws<Array<Any>>(
	concatArrayMonoid(),
) {
	override val possibilities: Int = 30
	override fun nonEmpty(possibility: Int): Array<Any> =
		(0..(possibility/2)).map { possibility - it }
			.toTypedArray()

	override fun equals(a1: Array<Any>, a2: Array<Any>): Boolean = a1.contentEquals(a2)

	@Test
	fun concats() {
		assertArrayEquals(
			arrayOf("6", "5", "1", "1L", "ajfg"),
			concatArrayMonoid<String>().combine(arrayOf("6", "5"), arrayOf("1", "1L", "ajfg"))
		)
	}
}

class ConcatSequenceMonoid: MonoidLaws<Sequence<Int>>(
	concatSequenceMonoid(),
) {
	override val possibilities: Int = 25
	override fun nonEmpty(possibility: Int) =
		(0..possibility).map { it % 13 }
			.asSequence()

	override fun equals(a1: Sequence<Int>, a2: Sequence<Int>): Boolean =
		a1.toList() == a2.toList()

	@Test
	fun concats() {
		assertEquals(
			listOf("6", "5", "1", "1L", "ajfg"),
			concatSequenceMonoid<String>()
				.combine(sequenceOf("6", "5"), sequenceOf("1", "1L", "ajfg"))
				.toList()
		)
	}
}

class ConcatListMonoid: MonoidLaws<List<Int>>(
	concatListMonoid(),
) {
	override val possibilities: Int = 25
	override fun nonEmpty(possibility: Int) =
		(0..possibility).map { it % 13 }

	@Test
	fun concats() {
		assertEquals(
			listOf("6", "5", "1", "1L", "ajfg"),
			concatListMonoid<String>()
				.combine(listOf("6", "5"), listOf("1", "1L", "ajfg"))

		)
	}
}
