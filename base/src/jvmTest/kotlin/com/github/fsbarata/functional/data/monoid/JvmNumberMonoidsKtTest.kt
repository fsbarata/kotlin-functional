package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.MonoidLaws
import java.math.BigDecimal
import kotlin.test.Test

class SumBigDecimalMonoidTest: MonoidLaws<BigDecimal>(sumBigDecimalMonoid()) {
	override val possibilities: Int = 25
	override fun factory(possibility: Int) = BigDecimal("$possibility")

	@Test
	fun adds() {
		assertEquals(BigDecimal("5.8"), sumBigDecimalMonoid().combine(BigDecimal("1.3"), BigDecimal("4.5")))
	}
}

class ProductBigDecimalMonoidTest: MonoidLaws<BigDecimal>(productBigDecimalMonoid()) {
	override val possibilities: Int = 25
	override fun factory(possibility: Int) = BigDecimal("$possibility")

	@Test
	fun multiplies() {
		assertEquals(BigDecimal("6.75"),
			productBigDecimalMonoid().combine(BigDecimal("1.5"), BigDecimal("4.5")))
	}
}
