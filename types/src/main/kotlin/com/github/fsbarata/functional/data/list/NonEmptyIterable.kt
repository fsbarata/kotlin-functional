package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.utils.NonEmptyIterator
import com.github.fsbarata.functional.kotlin.scanNel

interface NonEmptyIterable<out A>:
	Iterable<A>,
	Foldable<A> {
	val head: A
	val tail: Iterable<A>

	override fun iterator() = NonEmptyIterator(head, tail.iterator())

	override fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		tail.fold(accumulator(initialValue, head), accumulator)
}

fun <T> NonEmptyIterable<NonEmptyIterable<T>>.flatten() =
	NonEmptyList.of(head.head, head.tail + tail.flatten())

inline fun <A, B> NonEmptyIterable<A>.flatMapIterable(f: (A) -> Iterable<B>): List<B> = f(head) + tail.flatMap(f)

fun <S, A: S> NonEmptyIterable<A>.runningReduceNel(operation: (S, A) -> S) =
	tail.scanNel(head, operation)

fun <T: Comparable<T>> NonEmptyIterable<T>.max() = tail.maxOrNull()?.coerceAtLeast(head) ?: head
fun <T: Comparable<T>> NonEmptyIterable<T>.min() = tail.minOrNull()?.coerceAtMost(head) ?: head
