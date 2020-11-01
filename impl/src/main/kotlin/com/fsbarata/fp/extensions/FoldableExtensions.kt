package com.fsbarata.fp.extensions

import com.fsbarata.fp.data.Foldable
import com.fsbarata.fp.data.Monoid
import com.fsbarata.fp.types.NonEmptyList


fun <T, R> Foldable<T>.scan(initialValue: R, accumulator: (R, T) -> R): NonEmptyList<R> =
	fold(Pair(initialValue, NonEmptyList.just(initialValue))) { (a, list), v ->
		val newValue = accumulator(a, v)
		Pair(newValue, list + newValue)
	}.second

fun <A> Foldable<A>.scan(monoid: Monoid<A>): NonEmptyList<A> = scan(monoid.empty, monoid::combine)
