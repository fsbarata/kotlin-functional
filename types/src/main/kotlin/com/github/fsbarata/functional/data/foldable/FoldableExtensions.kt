package com.github.fsbarata.functional.data.foldable

import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.monoid.concatListMonoid

fun <T, R> Foldable<T>.scan(initialValue: R, accumulator: (R, T) -> R): NonEmptyList<R> =
	foldL(Pair(initialValue, NonEmptyList.just(initialValue))) { (a, list), v ->
		val newValue = accumulator(a, v)
		Pair(newValue, list + newValue)
	}.second

fun <A> Foldable<A>.scan(monoid: Monoid<A>): NonEmptyList<A> = scan(monoid.empty, monoid::combine)

fun <A> Foldable<A>.toList(): List<A> = foldMap(concatListMonoid(), ::nelOf)
