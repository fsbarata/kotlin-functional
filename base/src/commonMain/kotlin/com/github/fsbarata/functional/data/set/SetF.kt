package com.github.fsbarata.functional.data.set

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.invoke
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.sequence.SequenceF
import com.github.fsbarata.io.Serializable

@Suppress("OVERRIDE_BY_INLINE")
class SetF<out A>(private val wrapped: Set<A>): Set<A> by wrapped,
	Serializable,
	MonadPlus<SetContext, A>,
	Traversable<SetContext, A>,
	Semigroup<SetF<@UnsafeVariance A>> {
	override val scope get() = SetF

	override inline fun <B> map(f: (A) -> B): SetF<B> =
		mapTo(mutableSetOf(), f).f()

	override inline fun onEach(f: (A) -> Unit): SetF<A> {
		forEach(f)
		return this
	}

	override infix fun <B> ap(ff: Context<SetContext, (A) -> B>): SetF<B> =
		wrapped.ap(ff.asSet).f()

	override inline fun <B, R> lift2(fb: Context<SetContext, B>, f: (A, B) -> R): SetF<R> =
		(this as Set<A>).lift2(fb.asSet, f).f()

	override inline infix fun <B> bind(f: (A) -> Context<SetContext, B>): SetF<B> =
		flatMap { f(it).asSet }

	inline fun <B> flatMap(f: (A) -> Set<B>): SetF<B> =
		flatMapTo(mutableSetOf(), f).f()

	override inline fun filter(predicate: (A) -> Boolean): SetF<A> =
		filterTo(mutableSetOf(), predicate).f()

	override inline fun partition(predicate: (A) -> Boolean): Pair<SetF<A>, SetF<A>> =
		Pair(filter(predicate), filter { !predicate(it) })

	override inline fun <B: Any> mapNotNull(f: (A) -> B?): SetF<B> =
		mapNotNullTo(mutableSetOf(), f).f()

	override inline fun <B: Any> mapNotNone(f: (A) -> Optional<B>): SetF<B> =
		mapNotNull { f(it).orNull() }

	override inline fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		fold(initialValue, accumulator)

	override inline fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		(this as Set<A>).foldMap(monoid, f)

	override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Context<F, B>,
	): Context<F, SetF<B>> =
		appScope.map((this as Set<A>).traverse(appScope, f), Set<B>::f)

	override fun combineWith(other: Context<SetContext, @UnsafeVariance A>): SetF<A> =
		concatWith(other.asSet)

	override fun concatWith(other: SetF<@UnsafeVariance A>): SetF<A> =
		SetF(wrapped + other.wrapped)

	override fun toString() = wrapped.toString()
	override fun equals(other: Any?) = wrapped == other
	override fun hashCode() = wrapped.hashCode()

	override fun toList(): ListF<A> = ListF.fromIterable(wrapped)

	@Deprecated("Unnecessary call to toSet()", replaceWith = ReplaceWith("this"))
	override fun toSet(): SetF<A> = this

	fun asSequence(): SequenceF<A> = SequenceF.fromIterable(wrapped)

	companion object:
		MonadPlus.Scope<SetContext>,
		Traversable.Scope<SetContext> {
		override fun <A> empty() = SetF(emptySet<A>())
		override fun <A> just(a: A) = SetF(NonEmptySet.just(a))
		fun <A> of(vararg items: A) = SetF(setOf(*items))

		fun <A> monoid() = monoid(empty<A>())

		override fun <A> fromIterable(iterable: Iterable<A>) = SetF(iterable.toSet())
		override fun <A> fromSequence(sequence: Sequence<A>) = SetF(sequence.toSet())
		override fun <A> fromList(list: List<A>): SetF<A> = fromIterable(list)

		override fun <A> fromOptional(optional: Optional<A>): SetF<A> =
			optional.maybe(empty(), ::just)
	}
}

fun <A> Set<A>.f() = if (this is SetF) this else SetF(this)
fun <A> Set<A>.asFoldable(): Foldable<A> = f()
fun <A, R> Set<A>.f(block: SetF<A>.() -> Context<SetContext, R>) =
	f().block().asSet

internal typealias SetContext = SetF<*>

val <A> Context<SetContext, A>.asSet: SetF<A>
	get() = this as SetF<A>


operator fun <A, R> Lift1<A, R>.invoke(
	set: Set<A>,
): SetF<R> = fmap(set.f()).asSet

operator fun <A, B, R> Lift2<A, B, R>.invoke(
	set1: Set<A>,
	set2: Set<B>,
): SetF<R> = app(set1.f(), set2.f()).asSet

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	set1: Set<A>,
	set2: Set<B>,
	set3: Set<C>,
): SetF<R> = app(set1.f(), set2.f(), set3.f()).asSet

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	set1: Set<A>,
	set2: Set<B>,
	set3: Set<C>,
	set4: Set<D>,
): SetF<R> = app(set1.f(), set2.f(), set3.f(), set4.f()).asSet

fun <A, R> liftSet(f: (A) -> R): (Set<A>) -> SetF<R> = lift(f)::invoke
fun <A, B, R> liftSet2(f: (A, B) -> R): (Set<A>, Set<B>) -> SetF<R> = lift2(f)::invoke
fun <A, B, C, R> liftSet3(f: (A, B, C) -> R): (Set<A>, Set<B>, Set<C>) -> SetF<R> = lift3(f)::invoke
