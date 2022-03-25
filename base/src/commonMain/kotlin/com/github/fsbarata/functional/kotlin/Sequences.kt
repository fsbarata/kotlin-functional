package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.toNel
import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.data.sequence.headSequence
import com.github.fsbarata.functional.data.sequence.nonEmpty
import com.github.fsbarata.functional.data.sequence.tailSequence

fun <A> Sequence<A>.plusElementNe(item: A): NonEmptySequence<A> =
	tailSequence { iterator() to item }

fun <A> Sequence<A>.plusNe(other: NonEmptySequence<A>): NonEmptySequence<A> =
	plus(other).nonEmpty(other)

fun <T> Sequence<T>.windowedNel(size: Int, step: Int = 1, partialWindows: Boolean = false): Sequence<NonEmptyList<T>> =
	windowed(size, step, partialWindows) { it.toNel() ?: throw NoSuchElementException() }

fun <A, R> Sequence<A>.scanNe(initialValue: R, operation: (R, A) -> R): NonEmptySequence<R> {
	val scan = scan(initialValue, operation)
	return headSequence {
		val iterator = scan.iterator()
		Pair(iterator.next(), iterator)
	}
}
