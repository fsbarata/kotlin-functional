package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad
import java.io.Serializable

class NonEmptyList<A>(
		private val head: A,
		private val tail: List<A>
) : AbstractList<A>(),
		Monad<NonEmptyList<*>, A>,
		Foldable<A>,
		Serializable,
		List<A> {
	override val size: Int = 1 + tail.size

	override fun get(index: Int): A =
			if (index == 0) head
			else tail[index - 1]

	override fun isEmpty() = false

	fun first() = head
	fun firstOrNull(): Nothing = throw UnsupportedOperationException()
	fun last() = if (tail.isEmpty()) head else tail.last()
	fun lastOrNull(): Nothing = throw UnsupportedOperationException()

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

	fun plus(other: A) = NonEmptyList(head, tail + other)
	fun plus(other: Iterable<A>) = NonEmptyList(head, tail + other)

	companion object {
		fun <T> just(item: T) = of(item, emptyList())
		fun <T> of(head: T, vararg others: T) = of(head, others.toList())
		fun <T> of(head: T, others: List<T>) = NonEmptyList(head, others)
	}
}

val <A> Context<NonEmptyList<*>, A>.asNel get() = this as NonEmptyList<A>

fun <A> List<A>.nel(): NonEmptyList<A>? {
	return NonEmptyList(
			firstOrNull() ?: return null,
			drop(1)
	)
}

fun <A> List<A>.concatNel(item: A) =
		nel()?.plus(item) ?: NonEmptyList.just(item)

fun <A> List<A>.concatNel(other: NonEmptyList<A>) = this + other
operator fun <A> List<A>.plus(other: NonEmptyList<A>) =
		nel()?.plus(other) ?: other
