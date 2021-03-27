package com.github.fsbarata.functional.data.set

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.collection.NonEmptyCollection
import com.github.fsbarata.functional.utils.NonEmptyIterator
import com.github.fsbarata.functional.utils.nonEmpty
import com.github.fsbarata.functional.utils.toNes
import java.io.Serializable

class NonEmptySet<out A> private constructor(
	override val head: A,
	override val tail: Set<A>,
): Set<A>,
	AbstractSet<A>(),
	NonEmptyCollection<A>,
	Serializable,
	Semigroup<NonEmptySet<@UnsafeVariance A>> {
	override val size: Int = 1 + tail.size

	@Deprecated("Non empty set cannot be empty", replaceWith = ReplaceWith("false"))
	override fun isEmpty() = false

	override fun contains(element: @UnsafeVariance A) = super<NonEmptyCollection>.contains(element)
	override fun containsAll(elements: Collection<@UnsafeVariance A>) = super<NonEmptyCollection>.containsAll(elements)

	override fun iterator(): NonEmptyIterator<A> = super.iterator()

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
val <A> Context<NonEmptySetContext, A>.asNes
	get() = this as NonEmptySet<A>

fun <A> Set<A>.toNes(): NonEmptySet<A>? = iterator().nonEmpty()?.toNes()

fun <A> nesOf(head: A, vararg tail: A) = NonEmptySet.of(head, tail.toSet())