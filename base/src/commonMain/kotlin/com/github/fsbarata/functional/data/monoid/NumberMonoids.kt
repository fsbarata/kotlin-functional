package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.monoidOf

fun sumIntMonoid() = monoidOf(0, Int::plus)
fun productIntMonoid() = monoidOf(1, Int::times)
fun sumLongMonoid() = monoidOf(0L, Long::plus)
fun productLongMonoid() = monoidOf(1L, Long::times)
fun sumFloatMonoid() = monoidOf(0.0f, Float::plus)
fun productFloatMonoid() = monoidOf(1.0f, Float::times)
fun sumDoubleMonoid() = monoidOf(0.0, Double::plus)
fun productDoubleMonoid() = monoidOf(1.0, Double::times)

