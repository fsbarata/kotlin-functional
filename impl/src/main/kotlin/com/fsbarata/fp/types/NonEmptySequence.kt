package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Foldable

interface NonEmptySequence<out A>:
	Sequence<A>,
	Foldable<A> {
	override fun iterator(): NonEmptyIterator<A>

	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R =
		(this as Sequence<A>).fold(initialValue, accumulator)

	fun <B> map(f: (A) -> B) = NonEmptySequence {
		iterator().let {
			NonEmptyIterator(
				f(it.head),
				it.tail.asSequence().map(f).iterator()
			)
		}
	}

	@Deprecated("Non empty sequence always has a first", replaceWith = ReplaceWith("first()"))
	fun firstOrNull(): Nothing = throw UnsupportedOperationException()
	fun first() = iterator().head

	@Deprecated("Non empty sequence always has a last", replaceWith = ReplaceWith("last()"))
	fun lastOrNull(): Nothing = throw UnsupportedOperationException()
	fun last() = iterator().let { it.tail.asSequence().lastOrNull() ?: it.head }

	fun toList(): NonEmptyList<A> = iterator().toNel()
}

inline fun <A> NonEmptySequence(crossinline iterator: () -> NonEmptyIterator<A>): NonEmptySequence<A> =
	object: NonEmptySequence<A> {
		override fun iterator() = iterator()
	}

fun <A: Any> nonEmptySequence(head: A, nextFunction: (A) -> A?) = NonEmptySequence {
	NonEmptyIterator(
		head,
		generateSequence(nextFunction(head), nextFunction).iterator()
	)
}

fun <A> nonEmptySequenceOf(head: A, vararg tail: A) =
	NonEmptySequence { NonEmptyIterator(head, tail.iterator()) }

fun <A> Sequence<A>.nonEmpty(ifEmpty: NonEmptySequence<A>) = nonEmpty(ifEmpty::iterator)

fun <A> Sequence<A>.nonEmpty(ifEmpty: () -> NonEmptyIterator<A>) =
	NonEmptySequence {
		val iterator = iterator()
		if (iterator.hasNext()) NonEmptyIterator(iterator.next(), iterator)
		else ifEmpty()
	}



