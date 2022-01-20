package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.monoid

fun concatStringMonoid() = monoid("", String::plus)

inline fun <reified T> concatArrayMonoid(): Monoid<Array<T>> = monoid(emptyArray(), Array<T>::plus)

