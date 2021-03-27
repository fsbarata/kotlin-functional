package com.github.fsbarata.functional.data.set

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.list.NonEmptyIterable
import com.github.fsbarata.functional.utils.NonEmptyIterator
import com.github.fsbarata.functional.utils.nonEmpty
import java.io.Serializable
import kotlin.random.Random

class NonEmptySet<out A> private constructor(
	override val head: A,
	override val tail: Set<A>,
): Set<A>,
	AbstractSet<A>(),
	NonEmptyIterable<A>,
	Serializable,
	Foldable<A>,
	Semigroup<NonEmptySet<@UnsafeVariance A>> {
	override val size: Int = 1 + tail.size

	@Deprecated("Non empty set cannot be empty", replaceWith = ReplaceWith("false"))
	override fun isEmpty() = false

	fun first() = head

	@Deprecated("Non empty set always has a first", replaceWith = ReplaceWith("first()"))
	fun firstOrNull(): Nothing = throw UnsupportedOperationException()
	fun last() = if (tail.isEmpty()) head else tail.last()

	@Deprecated("Non empty set always has a last", replaceWith = ReplaceWith("last()"))
	fun lastOrNull(): Nothing = throw UnsupportedOperationException()

	@Deprecated("Non empty set always has a random", replaceWith = ReplaceWith("random()"))
	fun randomOrNull(): Nothing = throw UnsupportedOperationException()

	@Deprecated("Non empty set always has a random", replaceWith = ReplaceWith("random()"))
	fun randomOrNull(random: Random): Nothing = throw UnsupportedOperationException()

	override fun contains(element: @UnsafeVariance A) = head == element || tail.contains(element)
	override fun containsAll(elements: Collection<@UnsafeVariance A>) = elements.all(this::contains)

	override fun iterator(): NonEmptyIterator<A> =
		NonEmptyIterator(head, tail.iterator())

	operator fun plus(other: @UnsafeVariance A) = of(head, tail + other)
	operator fun plus(other: Iterable<@UnsafeVariance A>) = of(head, tail + other)

	override fun combineWith(other: NonEmptySet<@UnsafeVariance A>): NonEmptySet<A> = this + other

	companion object {
		fun <A> just(a: A) = of(a, emptySet())
		fun <T> of(head: T, others: Set<T>) = NonEmptySet(head, others - head)
	}
}

internal typealias NonEmptySetContext = NonEmptySet<*>

@Suppress("UNCHECKED_CAST")
val <A> Context<NonEmptySetContext, A>.asNes get() = this as NonEmptySet<A>

fun <A> Set<A>.toNes() =
	iterator().nonEmpty()?.toNes()

internal fun <A> NonEmptyIterator<A>.toNes(): NonEmptySet<A> =
	NonEmptySet.of(head, tail.asSequence().toSet())

fun <A> nesOf(head: A, vararg tail: A) = NonEmptySet.of(head, tail.toSet())