package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.control.Alternative
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.NonEmptyList

fun <A, R> Foldable<A>.scanL(initialValue: R, accumulator: (R, A) -> R): NonEmptyList<R> =
	foldL(NonEmptyList.just(initialValue)) { nel, v ->
		nel + accumulator(nel.last(), v)
	}

fun <A: Semigroup<A>> Foldable<A>.scan(initialValue: A): NonEmptyList<A> =
	scanL(initialValue, ::combine)

fun <A> Foldable<A>.toList(): ListF<A> = foldMap(ListF.monoid()) { ListF.just(it) }

fun <F, A> Foldable<Alternative<F, A>>.asum(scope: Alternative.Scope<F>) =
	foldL(scope.empty(), Alternative<F, A>::associateWith)
