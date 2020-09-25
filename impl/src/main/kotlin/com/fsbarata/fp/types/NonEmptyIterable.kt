package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Foldable

interface NonEmptyIterable<out A>:
	Iterable<A>,
	Foldable<A> {
	val head: A
	val tail: Iterable<A>

	override fun iterator() = NonEmptyIterator(head, tail.iterator())

	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R =
		transform { Iterable { tail }.fold(accumulator(initialValue, head), accumulator) }
}

fun <A> nonEmptyIterable(head: A, iterable: Iterable<A>) =
	object: NonEmptyIterable<A> {
		override val head = head
		override val tail: Iterable<A> = iterable
	}

internal fun <A, B> NonEmptyIterable<A>.transform(f: NonEmptyIterator<A>.() -> B) = f(iterator())

fun <T> NonEmptyIterable<NonEmptyIterable<T>>.flatten() =
	NonEmptyList.of(head.head, head.tail + tail.flatten())

fun <S, A: S> NonEmptyIterable<A>.runningReduceNel(operation: (S, A) -> S) =
	tail.scanNel(head, operation)

fun <T: Comparable<T>> NonEmptyIterable<T>.max() = tail.maxOrNull()?.coerceAtLeast(head) ?: head
fun <T: Comparable<T>> NonEmptyIterable<T>.min() = tail.minOrNull()?.coerceAtMost(head) ?: head
