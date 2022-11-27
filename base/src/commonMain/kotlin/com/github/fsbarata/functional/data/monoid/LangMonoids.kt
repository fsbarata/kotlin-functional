package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.monoidOf

fun concatStringMonoid() = monoidOf("", String::plus)

inline fun <reified T> concatArrayMonoid(): Monoid<Array<T>> = monoidOf(emptyArray(), Array<T>::plus)

