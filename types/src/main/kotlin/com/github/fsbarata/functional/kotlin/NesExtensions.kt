package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.utils.NonEmptyIterator

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
