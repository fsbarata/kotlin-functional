package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.flip
import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.kotlin.plusElementNes


fun <F, A> Alternative<F, A>.some(): Alternative<F, NonEmptySequence<A>> =
	lift2(many(), Sequence<A>::plusElementNes.flip())

fun <F, A> Alternative<F, A>.many(): Alternative<F, Sequence<A>> =
	associate(some(), scope.just(emptySequence()))
