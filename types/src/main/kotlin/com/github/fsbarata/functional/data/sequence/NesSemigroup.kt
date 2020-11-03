package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.data.Semigroup

fun <A> concatNesSemigroup(): Semigroup<NonEmptySequence<A>> =
	Semigroup(NonEmptySequence<A>::plus)
