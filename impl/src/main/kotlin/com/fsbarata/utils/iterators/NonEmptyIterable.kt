package com.fsbarata.utils.iterators

import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.kotlin.scanNel
import com.fsbarata.fp.types.NonEmptyList

interface NonEmptyIterable<out A>:
	Iterable<A>,
	Foldable<A> {
	val head: A
	val tail: Iterable<A>

	override fun iterator() = NonEmptyIterator(head, tail.iterator())

	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R =
		tail.fold(accumulator(initialValue, head), accumulator)
}

fun <A> nonEmptyIterable(head: A, iterable: Iterable<A>) =
	object: NonEmptyIterable<A> {
		override val head = head
		override val tail: Iterable<A> = iterable
	}

internal fun <A, B> NonEmptyIterable<A>.transform(f: NonEmptyIterator<A>.() -> B) = f(iterator())


inline fun <A, B> NonEmptyIterable<A>.map(f: (A) -> B): NonEmptyList<B> = NonEmptyList.of(f(head), tail.map(f))

fun <T> NonEmptyIterable<NonEmptyIterable<T>>.flatten() =
	NonEmptyList.of(head.head, head.tail + tail.flatten())

inline fun <A, B> NonEmptyIterable<A>.flatMap(f: (A) -> NonEmptyIterable<B>): NonEmptyList<B> = map(f).flatten()

inline fun <A, B> NonEmptyIterable<A>.flatMapIterable(f: (A) -> Iterable<B>): List<B> = f(head) + tail.flatMap(f)

fun <S, A: S> NonEmptyIterable<A>.runningReduceNel(operation: (S, A) -> S) =
	tail.scanNel(head, operation)

fun <T: Comparable<T>> NonEmptyIterable<T>.max() = tail.maxOrNull()?.coerceAtLeast(head) ?: head
fun <T: Comparable<T>> NonEmptyIterable<T>.min() = tail.minOrNull()?.coerceAtMost(head) ?: head
