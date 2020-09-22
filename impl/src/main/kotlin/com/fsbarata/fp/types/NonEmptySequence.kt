package com.fsbarata.fp.types

interface NonEmptySequence<out A> : Sequence<A> {
	override fun iterator(): NonEmptyIterator<A>

	@Deprecated("Non empty sequence always has a first", replaceWith = ReplaceWith("first()"))
	fun firstOrNull(): Nothing = throw UnsupportedOperationException()
	fun first() = iterator().head

	@Deprecated("Non empty sequence always has a last", replaceWith = ReplaceWith("last()"))
	fun lastOrNull(): Nothing = throw UnsupportedOperationException()
	fun last() = iterator().let { it.tail.asSequence().lastOrNull() ?: it.head }

	fun toList(): NonEmptyList<A> {
		val iterator = iterator()
		return NonEmptyList.of(iterator.next(), iterator.asSequence().toList())
	}
}

inline fun <A> NonEmptySequence(crossinline iterator: () -> NonEmptyIterator<A>): NonEmptySequence<A> =
		object : NonEmptySequence<A> {
			override fun iterator() = iterator()
		}

fun <A : Any> nonEmptySequence(head: A, nextFunction: (A) -> A?) = NonEmptySequence {
	NonEmptyIterator(
			head,
			generateSequence(nextFunction(head), nextFunction).iterator()
	)
}

fun <A> Sequence<A>.nonEmpty(ifEmpty: () -> NonEmptySequence<A>) =
		NonEmptySequence {
			val iterator = iterator()
			if (iterator.hasNext()) NonEmptyIterator(iterator.next(), iterator)
			else ifEmpty().iterator()
		}



