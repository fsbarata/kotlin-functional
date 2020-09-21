package com.fsbarata.fp.types

class NonEmptySequence<out A>(
		val head: A,
		val tail: Sequence<A>
) : Sequence<A> {
	override fun iterator() = NonEmptyIterator(head, tail.iterator())

	@Deprecated("Non empty sequence always has a first", replaceWith = ReplaceWith("first()"))
	fun firstOrNull(): Nothing = throw UnsupportedOperationException()
	fun first() = head

	@Deprecated("Non empty sequence always has a last", replaceWith = ReplaceWith("last()"))
	fun lastOrNull(): Nothing = throw UnsupportedOperationException()
	fun last() = tail.lastOrNull() ?: head

	fun <B> map(f: (A) -> B) = NonEmptySequence(f(head), tail.map(f))
	fun <B> flatMap(f: (A) -> NonEmptySequence<B>): NonEmptySequence<B> = map(f).flatten()
	fun <B> flatMap(f: (A) -> Sequence<B>): Sequence<B> = f(head) + tail.flatMap(f)

	operator fun plus(other: @UnsafeVariance A) = NonEmptySequence(head, tail + other)
	operator fun plus(other: Iterable<@UnsafeVariance A>) = NonEmptySequence(head, tail + other)
	operator fun plus(other: Sequence<@UnsafeVariance A>) = NonEmptySequence(head, tail + other)

}

fun <A> nonEmptySequence(head: A, nextFunction: (A) -> A?) =
		NonEmptySequence(head, generateSequence(nextFunction(head), nextFunction))

fun <A> NonEmptySequence<A>.toNonEmptyList() =
		NonEmptyList.of(head, tail.toList())

fun <A> NonEmptySequence<NonEmptySequence<A>>.flatten() =
		NonEmptySequence(head.head, head.tail + tail.flatten())

fun <A> Sequence<A>.nonEmpty(ifEmpty: () -> NonEmptySequence<A>): NonEmptySequence<A> {
	val iterator = iterator()
	return if (iterator.hasNext()) {
		NonEmptySequence(
				iterator.next(),
				Sequence { iterator }.constrainOnce()
		)
	} else ifEmpty()
}



