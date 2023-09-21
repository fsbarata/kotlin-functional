package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.toNel
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.data.sequence.nonEmpty
import com.github.fsbarata.functional.utils.nonEmptyIterator

fun <A> Sequence<A>.plusElementNe(item: A): NonEmptySequence<A> =
	NonEmptySequence { nonEmptyIterator(iterator(), item) }

fun <A> Sequence<A>.plusNe(other: NonEmptySequence<A>): NonEmptySequence<A> =
	plus(other).nonEmpty(other)

fun <T> Sequence<T>.chunkedNel(size: Int): Sequence<NonEmptyList<T>> =
	windowedNel(size, size, partialWindows = true)

fun <T> Sequence<T>.windowedNel(size: Int, step: Int = 1, partialWindows: Boolean = false): Sequence<NonEmptyList<T>> =
	windowed(size, step, partialWindows) { it.toNel() ?: throw NoSuchElementException() }

fun <A, R> Sequence<A>.scanNe(initialValue: R, operation: (R, A) -> R): NonEmptySequence<R> {
	val scan = scan(initialValue, operation)
	return NonEmptySequence { scan.iterator() }
}

fun <A, R: Any> Sequence<A>.mapNotNone(f: (A) -> Optional<R>): Sequence<R> =
	mapNotNull { f(it).orNull() }

fun <A: Any> Sequence<Optional<A>>.filterNotNone(): Sequence<A> =
	mapNotNone(::id)

inline fun <A, R: Any> Sequence<A>.mapNotNullToSet(f: (A) -> R?): Set<R> =
	mapNotNullTo(mutableSetOf(), f)

inline fun <A: Any> Sequence<A?>.filterNotNullToSet(): Set<A> =
	filterNotNullTo(mutableSetOf())

inline fun <A, R: Any> Sequence<A>.mapNotNoneToSet(f: (A) -> Optional<R>): Set<R> =
	mapNotNullToSet { f(it).orNull() }

inline fun <A: Any> Sequence<Optional<A>>.filterNotNoneToSet(): Set<A> =
	mapNotNoneToSet(::id)

inline fun <A> Sequence<A>.firstOrError(): A = first()
inline fun <A> Sequence<A>.lastOrError(): A = last()
