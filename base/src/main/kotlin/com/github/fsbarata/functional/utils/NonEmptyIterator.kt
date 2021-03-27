package com.github.fsbarata.functional.utils

import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.set.NonEmptySet


class NonEmptyIterator<out A>(
	val head: A,
	val tail: Iterator<A> = EmptyIterator,
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

internal fun <A> NonEmptyIterator<A>.toNel(): NonEmptyList<A> =
	NonEmptyList.of(head, tail.asSequence().toList())

internal fun <A> NonEmptyIterator<A>.toNes(): NonEmptySet<A> =
	NonEmptySet.of(head, tail.asSequence().toSet())

fun <A> Iterator<A>.nonEmpty(): NonEmptyIterator<A>? = when {
	this is NonEmptyIterator -> this
	!hasNext() -> null
	else -> NonEmptyIterator(next(), this)
}
