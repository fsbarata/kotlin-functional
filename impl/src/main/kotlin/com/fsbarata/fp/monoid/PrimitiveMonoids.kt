package com.fsbarata.fp.monoid

import com.fsbarata.fp.concepts.Monoid
import java.math.BigDecimal

fun sumIntMonoid() = object: Monoid<Int> {
	override fun empty() = 0

	override fun Int.combine(other: Int) = this + other
}

fun productIntMonoid() = object: Monoid<Int> {
	override fun empty() = 1

	override fun Int.combine(other: Int) = this * other
}

fun sumLongMonoid() = object: Monoid<Long> {
	override fun empty() = 0L

	override fun Long.combine(other: Long) = this + other
}

fun productLongMonoid() = object: Monoid<Long> {
	override fun empty() = 1L

	override fun Long.combine(other: Long) = this * other
}

fun sumFloatMonoid() = object: Monoid<Float> {
	override fun empty() = 0.0f

	override fun Float.combine(other: Float) = this + other
}

fun productFloatMonoid() = object: Monoid<Float> {
	override fun empty() = 1.0f

	override fun Float.combine(other: Float) = this * other
}

fun sumDoubleMonoid() = object: Monoid<Double> {
	override fun empty() = 0.0

	override fun Double.combine(other: Double) = this + other
}

fun productDoubleMonoid() = object: Monoid<Double> {
	override fun empty() = 1.0

	override fun Double.combine(other: Double) = this * other
}

fun sumBigDecimalMonoid() = object: Monoid<BigDecimal> {
	override fun empty() = BigDecimal.ZERO

	override fun BigDecimal.combine(other: BigDecimal) = this + other
}

fun productBigDecimalMonoid() = object: Monoid<BigDecimal> {
	override fun empty() = BigDecimal.ONE

	override fun BigDecimal.combine(other: BigDecimal) = this * other
}
