package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.monoid
import java.math.BigDecimal

fun sumIntMonoid() = monoid(0, Int::plus)
fun productIntSemigroup() = monoid(1, Int::times)
fun productIntMonoid() = monoid(1, Int::times)
fun sumLongMonoid() = monoid(0L, Long::plus)
fun productLongMonoid() = monoid(1L, Long::times)
fun sumFloatMonoid() = monoid(0.0f, Float::plus)
fun productFloatMonoid() = monoid(1.0f, Float::times)
fun sumDoubleMonoid() = monoid(0.0, Double::plus)
fun productDoubleMonoid() = monoid(1.0, Double::times)
fun sumBigDecimalMonoid() = monoid(BigDecimal.ZERO, BigDecimal::plus)
fun productBigDecimalMonoid() = monoid(BigDecimal.ONE, BigDecimal::times)
