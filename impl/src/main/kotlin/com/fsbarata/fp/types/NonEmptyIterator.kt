package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Foldable


class NonEmptyIterator<out A>(
	val head: A,
	val tail: Iterator<A>,
): Iterator<A> {
	private var begin: Boolean = true

	override fun hasNext() = begin || tail.hasNext()

	override fun next(): A {
		if (begin) {
			begin = false
			return head
		}
		return tail.next()
	}
}

fun <A> Iterator<A>.nonEmpty(): NonEmptyIterator<A>? = when {
	this is NonEmptyIterator -> this
	!hasNext() -> null
	else -> NonEmptyIterator(next(), this)
}

interface NonEmptyIterable<out A>:
	Iterable<A>,
	Foldable<A> {
	override fun iterator(): NonEmptyIterator<A>

	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R =
		transform { Iterable { tail }.fold(accumulator(initialValue, head), accumulator) }
}

fun <A> nonEmptyIterable(head: A, iterable: Iterable<A>) =
	NonEmptyIterable { NonEmptyIterator(head, iterable.iterator()) }

internal fun <A, B> NonEmptyIterable<A>.transform(f: NonEmptyIterator<A>.() -> B) =
	f(iterator())

inline fun <A> NonEmptyIterable(crossinline iterator: () -> NonEmptyIterator<A>): NonEmptyIterable<A> =
	object: NonEmptyIterable<A> {
		override fun iterator(): NonEmptyIterator<A> = iterator()
	}
