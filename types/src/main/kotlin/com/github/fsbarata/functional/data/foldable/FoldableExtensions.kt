package com.github.fsbarata.functional.data.foldable

import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf

fun <A, R> Foldable<A>.scanL(initialValue: R, accumulator: (R, A) -> R): NonEmptyList<R> =
	foldL(NonEmptyList.just(initialValue)) { nel, v ->
		nel + accumulator(nel.last(), v)
	}

fun <A> Foldable<A>.scan(monoid: Monoid<A>): NonEmptyList<A> =
	scanL(monoid.empty, monoid::combine)

fun <A> Foldable<A>.toList(): List<A> = foldMap(ListF.concatMonoid(), ::nelOf)
