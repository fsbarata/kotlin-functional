package com.fsbarata.fp.semigroup

import com.fsbarata.fp.data.Semigroup
import com.fsbarata.fp.types.NonEmptyList
import com.fsbarata.fp.types.NonEmptySequence

fun <A> concatNelSemigroup(): Semigroup<NonEmptyList<A>> =
	Semigroup { nel1, nel2 -> nel1 + nel2 }

fun <A> concatNesSemigroup(): Semigroup<NonEmptySequence<A>> =
	Semigroup { nes1, nes2 -> nes1 + nes2 }
