package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Monoid
import java.math.BigDecimal

fun Int.monoid() = object : Monoid<Int> {
	override fun empty() = 0

	override fun Int.combine(a: Int) = this + a
}

fun Long.monoid() = object : Monoid<Long> {
	override fun empty() = 0L

	override fun Long.combine(a: Long) = this + a
}

fun Double.monoid() = object : Monoid<Double> {
	override fun empty() = 0.0

	override fun Double.combine(a: Double) = this + a
}

fun BigDecimal.monoid() = object : Monoid<BigDecimal> {
	override fun empty() = BigDecimal.ZERO

	override fun BigDecimal.combine(a: BigDecimal) = this + a
}
