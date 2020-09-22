package com.fsbarata.fp.extensions

import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.concepts.Semigroup
import com.fsbarata.fp.types.NonEmptyList


fun <T, R> Foldable<T>.scan(initialValue: R, accumulator: (R, T) -> R): NonEmptyList<R> =
		fold(Pair(initialValue, NonEmptyList.just(initialValue))) { (a, list), v ->
			val newValue = accumulator(a, v)
			Pair(newValue, list + newValue)
		}.second

fun <A> Foldable<A>.scan(initialValue: A, semigroup: Semigroup<A>) =
		with(semigroup) { scan(initialValue) { acc, a -> acc.combine(a) } }
