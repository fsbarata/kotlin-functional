@file:Suppress("NOTHING_TO_INLINE")

package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.io.Serializable

/**
 * Functional list extension.
 *
 * Guaranteed to be immutable by construction
 */
@Suppress("OVERRIDE_BY_INLINE")
class ListF<out A> internal constructor(private val wrapped: List<A>): List<A> by wrapped,
	Serializable,
	MonadZip<ListContext, A>,
	MonadPlus<ListContext, A>,
	Traversable<ListContext, A>,
	Semigroup<ListF<@UnsafeVariance A>> {
	override val scope get() = ListF

	constructor(size: Int, init: (index: Int) -> A): this(List(size, init))

	override fun subList(fromIndex: Int, toIndex: Int) = ListF(wrapped.subList(fromIndex, toIndex))

	override inline fun <B> map(f: (A) -> B): ListF<B> =
		(this as List<A>).map(f).f()

	inline fun <B> mapIndexed(f: (index: Int, A) -> B): ListF<B> =
		(this as List<A>).mapIndexed(f).f()

	override infix fun <B> ap(ff: Functor<ListContext, (A) -> B>): ListF<B> =
		wrapped.ap(ff.asList).f()

	override inline fun <B, R> lift2(fb: Functor<ListContext, B>, f: (A, B) -> R): ListF<R> =
		(this as List<A>).lift2(fb.asList, f).f()

	override inline infix fun <B> bind(f: (A) -> Context<ListContext, B>): ListF<B> =
		flatMap { f(it).asList }

	inline fun <B> flatMap(f: (A) -> List<B>): ListF<B> =
		(this as List<A>).flatMap(f).f()

	override inline fun filter(predicate: (A) -> Boolean): ListF<A> =
		(this as List<A>).filter(predicate).f()

	override inline fun partition(predicate: (A) -> Boolean): Pair<ListF<A>, ListF<A>> {
		val p = (this as List<A>).partition(predicate)
		return Pair(p.first.f(), p.second.f())
	}

	override inline fun <B: Any> mapNotNull(f: (A) -> B?) =
		(this as List<A>).mapNotNull(f).f()

	override inline fun <B: Any> mapNotNone(f: (A) -> Optional<B>) =
		(this as List<A>).mapNotNull { f(it).orNull() }.f()

	override inline fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		(this as Iterable<A>).fold(initialValue, accumulator)

	override inline fun <R> foldR(initialValue: R, accumulator: (A, R) -> R): R =
		(this as List<A>).foldRight(initialValue, accumulator)

	override inline fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		(this as Iterable<A>).foldMap(monoid, f)

	override inline fun <B, R> zipWith(other: Functor<ListContext, B>, f: (A, B) -> R): ListF<R> =
		zip(other.asList, f).f()

	override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Functor<F, B>,
	): Functor<F, ListF<B>> =
		(this as List<A>).traverse(appScope, f)

	operator fun plus(other: @UnsafeVariance A) = ListF(wrapped + other)
	operator fun plus(other: Iterable<@UnsafeVariance A>) = ListF(wrapped + other)

	override fun associateWith(other: Context<ListContext, @UnsafeVariance A>) = plus(other.asList)
	override fun combineWith(other: ListF<@UnsafeVariance A>) = plus(other)

	override fun toString() = wrapped.toString()
	override fun equals(other: Any?) = wrapped == other
	override fun hashCode() = wrapped.hashCode()

	fun toSet(): SetF<A> = SetF.fromIterable(wrapped)
	fun asSequence(): SequenceF<A> = SequenceF.fromIterable(wrapped)

	companion object:
		MonadPlus.Scope<ListContext>,
		Traversable.Scope<ListContext> {
		private val EMPTY = ListF<Nothing>(emptyList())
		override fun <A> empty(): ListF<A> = EMPTY
		override fun <A> just(a: A): ListF<A> = ListF(NonEmptyList.just(a))
		fun <A> of(vararg items: A): ListF<A> = ListF(listOf(*items))

		fun <A> monoid(): Monoid<ListF<A>> = monoid(empty())

		override fun <A> fromIterable(iterable: Iterable<A>): ListF<A> = when (iterable) {
			is ListF -> iterable
			is NonEmptyList<A> -> ListF(iterable)
			else -> ListF(iterable.toList())
		}

		override fun <A> fromSequence(sequence: Sequence<A>) = ListF(sequence.toList())
		override inline fun <A> fromList(list: List<A>) = fromIterable(list)

		override fun <A> fromOptional(optional: Optional<A>): ListF<A> =
			optional.fold(ifEmpty = ::empty, ifSome = ::just)
	}
}

inline fun <A> List<A>.f() = ListF.fromList(this)

fun <A> List<A>.asFoldable(): Foldable<A> = f()
fun <A, R> List<A>.f(block: ListF<A>.() -> Context<ListContext, R>) =
	f().block().asList

internal typealias ListContext = ListF<*>

val <A> Context<ListContext, A>.asList: ListF<A>
	get() = this as ListF<A>


operator fun <A, R> Lift1<A, R>.invoke(
	list: List<A>,
): List<R> = ListF(list.map(f))

operator fun <A, B, R> Lift2<A, B, R>.invoke(
	list1: List<A>,
	list2: List<B>,
): ListF<R> = app(ListF(list1), ListF(list2)).asList

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	list1: List<A>,
	list2: List<B>,
	list3: List<C>,
): ListF<R> = app(ListF(list1), ListF(list2), ListF(list3)).asList

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	list1: List<A>,
	list2: List<B>,
	list3: List<C>,
	list4: List<D>,
): ListF<R> = app(ListF(list1), ListF(list2), ListF(list3), ListF(list4)).asList

fun <A, R> liftList(f: (A) -> R): (List<A>) -> List<R> = lift(f)::invoke
fun <A, B, R> liftList2(f: (A, B) -> R): (List<A>, List<B>) -> List<R> = lift2(f)::invoke
fun <A, B, C, R> liftList3(f: (A, B, C) -> R): (List<A>, List<B>, List<C>) -> List<R> = lift3(f)::invoke
