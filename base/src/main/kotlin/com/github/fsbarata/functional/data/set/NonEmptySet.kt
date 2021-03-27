package com.github.fsbarata.functional.data.set

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Comonad
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.Traversable
import com.github.fsbarata.functional.data.collection.NonEmptyCollection
import com.github.fsbarata.functional.data.list.*
import com.github.fsbarata.functional.data.partial
import com.github.fsbarata.functional.kotlin.plusElementNes
import com.github.fsbarata.functional.utils.NonEmptyIterator
import com.github.fsbarata.functional.utils.nonEmpty
import com.github.fsbarata.functional.utils.toNel
import com.github.fsbarata.functional.utils.toNes
import java.io.Serializable

@Suppress("OVERRIDE_BY_INLINE")
class NonEmptySet<out A> private constructor(
	override val head: A,
	override val tail: Set<A>,
): Set<A>,
	AbstractSet<A>(),
	NonEmptyCollection<A>,
	Serializable,
	Monad<NonEmptySetContext, A>,
	Traversable<NonEmptySetContext, A>,
	Comonad<NonEmptySetContext, A>,
	Semigroup<NonEmptySet<@UnsafeVariance A>> {
	override val scope get() = Companion

	override val size: Int = 1 + tail.size

	@Deprecated("Non empty set cannot be empty", replaceWith = ReplaceWith("false"))
	override fun isEmpty() = false

	override fun contains(element: @UnsafeVariance A) = super<NonEmptyCollection>.contains(element)
	override fun containsAll(elements: Collection<@UnsafeVariance A>) = super<NonEmptyCollection>.containsAll(elements)

	override fun iterator(): NonEmptyIterator<A> = super.iterator()

	override inline fun <B> map(f: (A) -> B): NonEmptySet<B> =
		of(f(head), tail.mapTo(mutableSetOf(), f))

	override fun <B> ap(ff: Applicative<NonEmptySetContext, (A) -> B>): NonEmptySet<B> =
		ff.asNes.flatMap(this::map)

	override inline fun <B, R> lift2(fb: Applicative<NonEmptySetContext, B>, f: (A, B) -> R): NonEmptySet<R> =
		flatMap { a -> fb.asNes.map(f.partial(a)) }

	override inline infix fun <B> bind(f: (A) -> Context<NonEmptySetContext, B>): NonEmptySet<B> =
		flatMap { f(it).asNes }

	inline fun <B> flatMap(f: (A) -> NonEmptySet<B>): NonEmptySet<B> {
		val mappedHead = f(head)
		return of(mappedHead.head, mappedHead.tail + tail.flatMap(f))
	}

	operator fun plus(other: @UnsafeVariance A) = of(head, tail + other)
	operator fun plus(other: Iterable<@UnsafeVariance A>) = of(head, tail + other)

	override fun extract(): A = head
	override fun <B> extend(f: (Comonad<NonEmptySetContext, A>) -> B): NonEmptySet<B> = coflatMap(f)

	fun <B> coflatMap(f: (NonEmptySet<A>) -> B): NonEmptySet<B> {
		val newHead = f(this)
		return of(
			newHead,
			(tail.toNes() ?: return just(newHead)).coflatMap(f)
		)
	}

	override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Applicative<F, B>,
	): Applicative<F, NonEmptySet<B>> =
		tail.traverse(appScope, f)
			.lift2(f(head), Set<B>::plusElementNes)

	override fun combineWith(other: NonEmptySet<@UnsafeVariance A>): NonEmptySet<A> = this + other

	companion object: Monad.Scope<NonEmptySetContext>, Traversable.Scope<NonEmptySetContext> {
		override fun <A> just(a: A) = of(a, emptySet())
		fun <T> of(head: T, others: Set<T>) = NonEmptySet(head, others - head)
	}
}

internal typealias NonEmptySetContext = NonEmptySet<*>

@Suppress("UNCHECKED_CAST")
val <A> Context<NonEmptySetContext, A>.asNes
	get() = this as NonEmptySet<A>

fun <A> Set<A>.toNes(): NonEmptySet<A>? = iterator().nonEmpty()?.toNes()

fun <A> nesOf(head: A, vararg tail: A) = NonEmptySet.of(head, tail.toSet())

fun <A> Iterable<A>.toNes(): NonEmptySet<A>? {
	return when (this) {
		is NonEmptySet<A> -> this
		else -> iterator().nonEmpty()?.toNes()
	}
}