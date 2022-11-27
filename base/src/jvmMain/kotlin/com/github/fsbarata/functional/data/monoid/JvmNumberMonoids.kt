package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.monoidOf
import java.math.BigDecimal

fun sumBigDecimalMonoid() = monoidOf(BigDecimal.ZERO, BigDecimal::plus)
fun productBigDecimalMonoid() = monoidOf(BigDecimal.ONE, BigDecimal::times)
