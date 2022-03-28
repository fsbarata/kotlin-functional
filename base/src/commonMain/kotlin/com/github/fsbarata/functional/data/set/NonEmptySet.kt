package com.github.fsbarata.functional.data.set

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.collection.NonEmptyCollection
import com.github.fsbarata.functional.utils.toNes
import com.github.fsbarata.io.Serializable

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

	override fun iterator(): Iterator<A> = super.iterator()

	override inline fun <B> map(f: (A) -> B): NonEmptySet<B> =
		of(f(head), tail.mapTo(mutableSetOf(), f))

	override fun <B> ap(ff: Functor<NonEmptySetContext, (A) -> B>): NonEmptySet<B> =
		ff.asNes.flatMap(this::map)

	override inline fun <B, R> lift2(fb: Functor<NonEmptySetContext, B>, f: (A, B) -> R): NonEmptySet<R> =
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
		f: (A) -> Functor<F, B>,
	): Functor<F, NonEmptySet<B>> =
		appScope.lift2(f(head), tail.traverse(appScope, f), ::of)

	inline fun <F, B> traverse(
		f: (A) -> Applicative<F, B>,
	): Functor<F, NonEmptySet<B>> {
		val mappedHead = f(head)
		return mappedHead.lift2(
			tail.traverse(mappedHead.scope, f),
			::of
		)
	}

	override fun combineWith(other: NonEmptySet<@UnsafeVariance A>): NonEmptySet<A> = this + other

	companion object: Monad.Scope<NonEmptySetContext>, Traversable.Scope<NonEmptySetContext> {
		override fun <A> just(a: A) = NonEmptySet(a, emptySet())
		fun <T> of(head: T, others: Set<T>) = NonEmptySet(head, others - head)
	}
}

internal typealias NonEmptySetContext = NonEmptySet<*>

@Suppress("UNCHECKED_CAST")
val <A> Context<NonEmptySetContext, A>.asNes
	get() = this as NonEmptySet<A>

fun <A> nesOf(head: A, vararg tail: A) = NonEmptySet.of(head, tail.toSet())

fun <A> Iterable<A>.toNes(): NonEmptySet<A>? {
	return when (this) {
		is NonEmptySet<A> -> this
		else -> iterator().toNes()
	}
}

@Suppress("NOTHING_TO_INLINE")
inline fun <F, A> NonEmptySet<Functor<F, A>>.sequenceA(appScope: Applicative.Scope<F>): Functor<F, NonEmptySet<A>> =
	traverse(appScope, ::id)

@Suppress("NOTHING_TO_INLINE")
inline fun <F, A> NonEmptySet<Applicative<F, A>>.sequenceA(): Functor<F, NonEmptySet<A>> =
	traverse(head.scope, ::id)

operator fun <A, R> Lift1<A, R>.invoke(
	set: NonEmptySet<A>,
): NonEmptySet<R> = fmap(set).asNes

operator fun <A, B, R> Lift2<A, B, R>.invoke(
	set1: NonEmptySet<A>,
	set2: NonEmptySet<B>,
): NonEmptySet<R> = app(set1, set2).asNes

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	set1: NonEmptySet<A>,
	set2: NonEmptySet<B>,
	set3: NonEmptySet<C>,
): NonEmptySet<R> = app(set1, set2, set3).asNes

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	set1: NonEmptySet<A>,
	set2: NonEmptySet<B>,
	set3: NonEmptySet<C>,
	set4: NonEmptySet<D>,
): NonEmptySet<R> = app(set1, set2, set3, set4).asNes

fun <A, R> liftNes(f: (A) -> R): (NonEmptySet<A>) -> NonEmptySet<R> = lift(f)::invoke
fun <A, B, R> liftNes2(f: (A, B) -> R): (NonEmptySet<A>, NonEmptySet<B>) -> NonEmptySet<R> = lift2(f)::invoke
fun <A, B, C, R> liftNes3(f: (A, B, C) -> R): (NonEmptySet<A>, NonEmptySet<B>, NonEmptySet<C>) -> NonEmptySet<R> =
	lift3(f)::invoke
