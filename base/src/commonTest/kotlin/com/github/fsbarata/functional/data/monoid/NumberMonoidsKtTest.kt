package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.MonoidLaws
import kotlin.test.Test
import kotlin.test.assertEquals

class SumIntMonoidTest: MonoidLaws<Int>(sumIntMonoid()) {
	override val possibilities: Int = 20
	override fun factory(possibility: Int) = possibility - 10

	@Test
	fun adds() {
		assertEquals(8, sumIntMonoid().combine(5, 3))
		assertEquals(2, sumIntMonoid().combine(5, -3))
		assertEquals(-8, sumIntMonoid().combine(-5, -3))
	}
}

class ProductIntMonoidTest: MonoidLaws<Int>(productIntMonoid()) {
	override val possibilities: Int = 20
	override fun factory(possibility: Int) = possibility - 10

	@Test
	fun multiplies() {
		assertEquals(15, productIntMonoid().combine(5, 3))
		assertEquals(-15, productIntMonoid().combine(5, -3))
		assertEquals(15, productIntMonoid().combine(-5, -3))
	}
}

class SumLongMonoidTest: MonoidLaws<Long>(sumLongMonoid()) {
	override val possibilities: Int = 20
	override fun factory(possibility: Int) = (possibility - 10).toLong()

	@Test
	fun adds() {
		assertEquals(8L, sumLongMonoid().combine(5, 3))
		assertEquals(2L, sumLongMonoid().combine(5, -3))
		assertEquals(-8L, sumLongMonoid().combine(-5, -3))
	}
}

class ProductLongMonoidTest: MonoidLaws<Long>(productLongMonoid()) {
	override val possibilities: Int = 20
	override fun factory(possibility: Int) = (possibility - 10).toLong()

	@Test
	fun multiplies() {
		assertEquals(15L, productLongMonoid().combine(5, 3))
		assertEquals(-15L, productLongMonoid().combine(5, -3))
		assertEquals(15L, productLongMonoid().combine(-5, -3))
	}
}

class SumFloatMonoidTest: MonoidLaws<Float>(sumFloatMonoid()) {
	override val possibilities: Int = 20
	override fun factory(possibility: Int) = (possibility - 10) * 0.25f

	@Test
	fun adds() {
		assertEquals(8f, sumFloatMonoid().combine(5f, 3f))
		assertEquals(4.8f, sumFloatMonoid().combine(5f, -0.2f))
		assertEquals(-8.1f, sumFloatMonoid().combine(-5f, -3.1f))
	}
}

class ProductFloatMonoidTest: MonoidLaws<Float>(productFloatMonoid()) {
	override val possibilities: Int = 20
	override fun factory(possibility: Int) = (possibility - 10) * 0.25f

	@Test
	fun multiplies() {
		assertEquals(15f, productFloatMonoid().combine(5f, 3f))
		assertEquals(-1f, productFloatMonoid().combine(5f, -0.2f))
		assertEquals(15.5f, productFloatMonoid().combine(-5f, -3.1f))
	}
}

class SumDoubleMonoidTest: MonoidLaws<Double>(sumDoubleMonoid()) {
	override val possibilities: Int = 20
	override fun factory(possibility: Int) = (possibility - 10) * 0.25

	@Test
	fun adds() {
		assertEquals(8.0, sumDoubleMonoid().combine(5.0, 3.0), 1e-8)
		assertEquals(4.8, sumDoubleMonoid().combine(5.0, -0.2), 1e-8)
		assertEquals(-8.1, sumDoubleMonoid().combine(-5.0, -3.1), 1e-8)
	}
}

class ProductDoubleMonoidTest: MonoidLaws<Double>(productDoubleMonoid()) {
	override val possibilities: Int = 20
	override fun factory(possibility: Int) = (possibility - 10) * 0.25

	@Test
	fun multiplies() {
		assertEquals(15.0, productDoubleMonoid().combine(5.0, 3.0), 1e-8)
		assertEquals(-1.0, productDoubleMonoid().combine(5.0, -0.2), 1e-8)
		assertEquals(15.5, productDoubleMonoid().combine(-5.0, -3.1), 1e-8)
	}
}