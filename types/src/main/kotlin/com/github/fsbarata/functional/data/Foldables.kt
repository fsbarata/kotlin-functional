package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.NonEmptyList

fun <A, R> Foldable<A>.scanL(initialValue: R, accumulator: (R, A) -> R): NonEmptyList<R> =
	foldL(NonEmptyList.just(initialValue)) { nel, v ->
		nel + accumulator(nel.last(), v)
	}

fun <A: Semigroup<A>> Foldable<A>.scan(initialValue: A): NonEmptyList<A> =
	scanL(initialValue, ::combine)

fun <A> Foldable<A>.toList(): ListF<A> = foldMap(ListF.monoid()) { ListF.just(it) }
