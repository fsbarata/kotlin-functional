package com.fsbarata.fp.monoid

import com.fsbarata.fp.concepts.monoid

fun sumIntMonoid() = monoid(0, Int::plus)
fun productIntMonoid() = monoid(1, Int::times)
fun sumLongMonoid() = monoid(0L, Long::plus)
fun productLongMonoid() = monoid(1L, Long::times)
fun sumFloatMonoid() = monoid(0.0f, Float::plus)
fun productFloatMonoid() = monoid(1.0f, Float::times)
fun sumDoubleMonoid() = monoid(0.0, Double::plus)
fun productDoubleMonoid() = monoid(1.0, Double::times)

