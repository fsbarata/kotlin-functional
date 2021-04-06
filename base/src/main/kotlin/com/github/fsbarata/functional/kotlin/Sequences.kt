package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.data.sequence.headSequence
import com.github.fsbarata.functional.data.sequence.nonEmpty
import com.github.fsbarata.functional.data.sequence.tailSequence

fun <A> Sequence<A>.plusElementNes(item: A): NonEmptySequence<A> =
	tailSequence { iterator() then item }

fun <A> Sequence<A>.plusNes(other: NonEmptySequence<A>): NonEmptySequence<A> =
	plus(other).nonEmpty(other)

fun <A, R> Sequence<A>.scanNes(initialValue: R, operation: (R, A) -> R): NonEmptySequence<R> {
	val scan = scan(initialValue, operation)
	return headSequence {
		val iterator = scan.iterator()
		iterator.next() then iterator
	}
}
