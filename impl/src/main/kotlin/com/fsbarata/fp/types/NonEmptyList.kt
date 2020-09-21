package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad
import java.io.Serializable

class NonEmptyList<out A> private constructor(
		private val head: A,
		private val tail: List<A>
) : AbstractList<A>(),
		List<A>,
		Monad<NonEmptyList<*>, A>,
		Foldable<A>,
		Serializable {
	override val size: Int = 1 + tail.size

	override fun get(index: Int): A =
			if (index == 0) head
			else tail[index - 1]

	@Deprecated("Non empty list cannot be empty")
	override fun isEmpty() = false

	fun first() = head

	@Deprecated("Non empty list always has a first", replaceWith = ReplaceWith("first()"))
	fun firstOrNull(): Nothing = throw UnsupportedOperationException()
	fun last() = if (tail.isEmpty()) head else tail.last()

	@Deprecated("Non empty list always has a last", replaceWith = ReplaceWith("last()"))
	fun lastOrNull(): Nothing = throw UnsupportedOperationException()

	override fun contains(element: @UnsafeVariance A) = head == element || tail.contains(element)
	override fun containsAll(elements: Collection<@UnsafeVariance A>) = elements.all(this::contains)

	override fun indexOf(element: @UnsafeVariance A) =
			if (head == element) 0
			else (tail.indexOf(element) + 1).takeIf { it != 0 } ?: -1

	override fun lastIndexOf(element: @UnsafeVariance A) =
			(tail.lastIndexOf(element) + 1).takeIf { it != 0 }
					?: if (head == element) 0 else -1

	override fun iterator(): Iterator<A> =
			NonEmptyIterator(head, tail.iterator())

	override fun subList(fromIndex: Int, toIndex: Int): List<A> = when {
		fromIndex == 0 && toIndex == 0 -> emptyList()
		fromIndex == 0 -> NonEmptyList(head, tail.subList(0, toIndex - 1))
		else -> tail.subList(fromIndex - 1, toIndex - 1)
	}

	override fun <B> just(b: B): NonEmptyList<B> =
			Companion.just(b)

	override fun <B> map(f: (A) -> B): NonEmptyList<B> =
			NonEmptyList(f(head), tail.map(f))

	override fun <B> bind(f: (A) -> Functor<NonEmptyList<*>, B>): NonEmptyList<B> =
			flatMap { f(it).asNel }

	fun <B> flatMap(f: (A) -> NonEmptyList<B>): NonEmptyList<B> {
		val headList = f(head)
		return NonEmptyList(headList.head, headList.tail + tail.flatMap(f))
	}

	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R =
			tail.fold(accumulator(initialValue, head), accumulator)

	operator fun plus(other: @UnsafeVariance A) = NonEmptyList(head, tail + other)
	operator fun plus(other: Iterable<@UnsafeVariance A>) = NonEmptyList(head, tail + other)

	companion object {
		fun <T> just(item: T) = of(item, emptyList())
		fun <T> of(head: T, vararg others: T) = of(head, others.toList())
		fun <T> of(head: T, others: List<T>) = NonEmptyList(head, others)
	}
}

val <A> Context<NonEmptyList<*>, A>.asNel get() = this as NonEmptyList<A>

fun <A> List<A>.nel(): NonEmptyList<A>? {
	return NonEmptyList.of(
			firstOrNull() ?: return null,
			drop(1)
	)
}

fun <A> List<A>.concatNel(item: A) =
		nel()?.plus(item) ?: NonEmptyList.just(item)

fun <A> List<A>.concatNel(other: NonEmptyList<A>) = this + other
operator fun <A> List<A>.plus(other: NonEmptyList<A>) =
		nel()?.plus(other) ?: other

class NonEmptyIterator<A> internal constructor(
		private val head: A,
		private val tailIterator: Iterator<A>,
		internal var begin: Boolean
) : Iterator<A> {
	constructor(head: A, tailIterator: Iterator<A>) : this(head, tailIterator, true)

	override fun hasNext() = begin || tailIterator.hasNext()
	override fun next(): A {
		if (begin) {
			begin = false
			return head
		}
		return tailIterator.next()
	}
}
