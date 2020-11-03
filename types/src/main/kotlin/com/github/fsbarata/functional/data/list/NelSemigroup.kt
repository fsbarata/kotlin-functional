package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.data.Semigroup

fun <A> concatNelSemigroup(): Semigroup<NonEmptyList<A>> =
	Semigroup(NonEmptyList<A>::plus)
