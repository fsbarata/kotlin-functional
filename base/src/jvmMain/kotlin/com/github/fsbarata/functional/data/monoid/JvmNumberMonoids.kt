package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.monoid
import java.math.BigDecimal

fun sumBigDecimalMonoid() = monoid(BigDecimal.ZERO, BigDecimal::plus)
fun productBigDecimalMonoid() = monoid(BigDecimal.ONE, BigDecimal::times)
