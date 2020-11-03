package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.monoid
import java.math.BigDecimal

fun concatStringMonoid() = monoid("", String::plus)

fun sumBigDecimalMonoid() = monoid(BigDecimal.ZERO, BigDecimal::plus)
fun productBigDecimalMonoid() = monoid(BigDecimal.ONE, BigDecimal::times)

inline fun <reified T> concatArrayMonoid(): Monoid<Array<T>> = monoid(emptyArray(), Array<T>::plus)
fun <A> concatSequenceMonoid(): Monoid<Sequence<A>> = monoid(emptySequence(), Sequence<A>::plus)
fun <A> concatListMonoid(): Monoid<List<A>> = monoid(emptyList(), Iterable<A>::plus)

