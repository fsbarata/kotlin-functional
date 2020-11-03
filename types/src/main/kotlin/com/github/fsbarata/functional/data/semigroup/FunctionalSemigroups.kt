package com.github.fsbarata.functional.data.semigroup

import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.sequence.NonEmptySequence

fun <A> concatNesSemigroup(): Semigroup<NonEmptySequence<A>> =
	Semigroup(NonEmptySequence<A>::plus)
