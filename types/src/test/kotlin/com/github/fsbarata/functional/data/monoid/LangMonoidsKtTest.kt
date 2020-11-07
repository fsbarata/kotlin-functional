package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.test.MonoidTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.util.*
import kotlin.random.Random

class ConcatStringMonoidTest: MonoidTest<String>(
	concatStringMonoid(),
	{ UUID.randomUUID().toString() }
) {
	@Test
	fun concats() {
		assertEquals("a2bb", concatStringMonoid().combine("a2", "bb"))
	}
}

class SumBigDecimalMonoidTest: MonoidTest<BigDecimal>(
	sumBigDecimalMonoid(),
	{ BigDecimal(Random.nextDouble(1.0, 50.0)) }
) {
	@Test
	fun adds() {
		assertEquals(BigDecimal("5.8"), sumBigDecimalMonoid().combine(BigDecimal("1.3"), BigDecimal("4.5")))
	}
}

class ProductBigDecimalMonoidTest: MonoidTest<BigDecimal>(
	productBigDecimalMonoid(),
	{ BigDecimal(Random.nextDouble(2.0, 50.0)) }
) {
	@Test
	fun multiplies() {
		assertEquals(BigDecimal("6.75"),
			productBigDecimalMonoid().combine(BigDecimal("1.5"), BigDecimal("4.5")))
	}
}

class ConcatArrayMonoidTest: MonoidTest<Array<Any>>(
	concatArrayMonoid(),
	{ arrayOf(if (Random.nextBoolean()) Random.nextInt(1, 5) else Random.nextDouble(1.0, 5.0)) }
) {
	override fun equals(a1: Array<Any>, a2: Array<Any>): Boolean = a1.contentEquals(a2)

	@Test
	fun concats() {
		assertArrayEquals(
			arrayOf("6", "5", "1", "1L", "ajfg"),
			concatArrayMonoid<String>().combine(arrayOf("6", "5"), arrayOf("1", "1L", "ajfg"))
		)
	}
}

class ConcatSequenceMonoid: MonoidTest<Sequence<Double>>(
	concatSequenceMonoid(),
	{
		generateSequence(Random.nextDouble()) { (it - Random.nextDouble()).takeIf { it > 0 } }
			.toList().asSequence()
	}
) {
	override fun equals(a1: Sequence<Double>, a2: Sequence<Double>): Boolean =
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

class ConcatListMonoid: MonoidTest<List<Double>>(
	concatListMonoid(),
	{
		generateSequence(Random.nextDouble()) { (it - Random.nextDouble()).takeIf { it > 0 } }
			.toList()
	}
) {
	@Test
	fun concats() {
		assertEquals(
			listOf("6", "5", "1", "1L", "ajfg"),
			concatListMonoid<String>()
				.combine(listOf("6", "5"), listOf("1", "1L", "ajfg"))

		)
	}
}
