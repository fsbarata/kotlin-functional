package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.data.sequence.nonEmpty
import com.github.fsbarata.functional.utils.NonEmptyIterator

fun <A> Sequence<A>.plusElementNes(item: A): NonEmptySequence<A> =
	plusElement(item).nonEmpty { throw NoSuchElementException() }

fun <A> Sequence<A>.plusNes(other: NonEmptySequence<A>): NonEmptySequence<A> =
	plus(other).nonEmpty { throw NoSuchElementException() }

fun <A, R> Sequence<A>.scanNes(initialValue: R, operation: (R, A) -> R): NonEmptySequence<R> {
	val scan = scan(initialValue, operation)
	return NonEmptySequence {
		val iterator = scan.iterator()
		NonEmptyIterator(
			iterator.next(),
			iterator
		)
	}
}
