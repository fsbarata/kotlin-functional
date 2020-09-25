package com.fsbarata.fp.types


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

internal fun <A> NonEmptyIterator<A>.toNel(): NonEmptyList<A> =
	NonEmptyList.of(head, tail.asSequence().toList())

fun <A> Iterator<A>.nonEmpty(): NonEmptyIterator<A>? = when {
	this is NonEmptyIterator -> this
	!hasNext() -> null
	else -> NonEmptyIterator(next(), this)
}
