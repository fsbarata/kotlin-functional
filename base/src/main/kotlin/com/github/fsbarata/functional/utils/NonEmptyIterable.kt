package com.github.fsbarata.functional.utils

import com.github.fsbarata.functional.data.sequence.NonEmptySequence

interface NonEmptyIterable<out A>: Iterable<A> {
	override fun iterator(): NonEmptyIterator<A>

	fun toList() = iterator().toNel()
	fun toSet() = iterator().toNes()
	fun asSequence(): NonEmptySequence<@UnsafeVariance A> = NonEmptySequence { iterator() }
}