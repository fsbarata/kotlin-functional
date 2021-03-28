package com.github.fsbarata.functional.data.set

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.maybe.Optional
import java.io.Serializable

@Suppress("OVERRIDE_BY_INLINE")
class SetF<A>(private val wrapped: Set<A>): Set<A> by wrapped,
	Serializable,
	MonadPlus<SetContext, A>,
	Traversable<SetContext, A>,
	Semigroup<SetF<A>> {
	override val scope get() = SetF

	override inline fun <B> map(f: (A) -> B): SetF<B> =
		mapTo(mutableSetOf(), f).f()

	override infix fun <B> ap(ff: Functor<SetContext, (A) -> B>): SetF<B> =
		wrapped.ap(ff.asSet).f()

	override inline fun <B, R> lift2(fb: Functor<SetContext, B>, f: (A, B) -> R): SetF<R> =
		(this as Set<A>).lift2(fb.asSet, f).f()

	override inline infix fun <B> bind(f: (A) -> Context<SetContext, B>): SetF<B> =
		flatMap { f(it).asSet }

	inline fun <B> flatMap(f: (A) -> Set<B>): SetF<B> =
		(this as Set<A>).flatMapTo(mutableSetOf(), f).f()

	override inline fun filter(predicate: (A) -> Boolean): SetF<A> =
		(this as Set<A>).filterTo(mutableSetOf(), predicate).f()

	override inline fun partition(predicate: (A) -> Boolean): Pair<SetF<A>, SetF<A>> {
		val p = (this as Set<A>).partition(predicate)
		return Pair(p.first.toSet().f(), p.second.toSet().f())
	}

	override inline fun <B: Any> mapNotNull(f: (A) -> B?): SetF<B> =
		(this as Set<A>).mapNotNullTo(mutableSetOf(), f).f()

	override inline fun <B: Any> mapNotNone(f: (A) -> Optional<B>): SetF<B> =
		mapNotNull { f(it).orNull() }

	override inline fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		(this as Set<A>).fold(initialValue, accumulator)

	override inline fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		(this as Set<A>).foldMap(monoid, f)

	override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Functor<F, B>,
	): Functor<F, SetF<B>> =
		(this as Set<A>).traverse(appScope, f).map(Set<B>::f)

	override fun associateWith(other: Context<SetContext, A>): SetF<A> =
		combineWith(other.asSet)

	override fun combineWith(other: SetF<A>): SetF<A> =
		SetF(wrapped + other.wrapped)

	override fun toString() = wrapped.toString()
	override fun equals(other: Any?) = wrapped == other
	override fun hashCode() = wrapped.hashCode()

	companion object:
		MonadPlus.Scope<SetContext>,
		Traversable.Scope<SetContext> {
		override fun <A> empty() = emptySet<A>().f()
		override fun <A> just(a: A) = setOf(a).f()
		fun <A> of(vararg items: A) = setOf(*items).f()

		fun <A> monoid() = monoid(empty<A>())

		override fun <A> fromList(list: List<A>) = list.toSet().f()

		override fun <A> fromOptional(optional: Optional<A>) =
			optional.maybe(empty(), ::just)
	}
}

fun <A> Set<A>.f() = SetF(this)
fun <A> Set<A>.asFoldable(): Foldable<A> = f()
fun <A, R> Set<A>.f(block: SetF<A>.() -> Context<SetContext, R>) =
	f().block().asSet

internal typealias SetContext = SetF<*>

val <A> Context<SetContext, A>.asSet: SetF<A>
	get() = this as SetF<A>


operator fun <A, B, R> Lift2<A, B, R>.invoke(
	list1: SetF<A>,
	list2: SetF<B>,
): SetF<R> = app(list1, list2).asSet

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	list1: SetF<A>,
	list2: SetF<B>,
	list3: SetF<C>,
): SetF<R> = app(list1, list2, list3).asSet

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	list1: SetF<A>,
	list2: SetF<B>,
	list3: SetF<C>,
	list4: SetF<D>,
): SetF<R> = app(list1, list2, list3, list4).asSet
