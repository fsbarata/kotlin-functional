package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.flip
import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.kotlin.plusElementNes


fun <C, A> Alternative<C, A>.some(): Alternative<C, NonEmptySequence<A>> =
	lift2(many(), Sequence<A>::plusElementNes.flip())

fun <C, A> Alternative<C, A>.many(): Alternative<C, Sequence<A>> =
	associate(some(), scope.just(emptySequence()))
