@file:Suppress("NOTHING_TO_INLINE")

package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.control.arrow.Kleisli
import com.github.fsbarata.functional.control.arrow.kleisli
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.io.Serializable

/**
 * Optional (Maybe) Monad
 *
 * Wraps a value that may or may not be present.
 */
@Suppress("OVERRIDE_BY_INLINE")
sealed class Optional<out A>:
	MonadZip<OptionalContext, A>,
	MonadPlus<OptionalContext, A>,
	Traversable<OptionalContext, A>,
	Serializable {
	override val scope get() = Optional

	abstract fun orNull(): A?

	fun isPresent() = orNull() != null
	fun isAbsent() = !isPresent()

	final override inline fun filter(predicate: (A) -> Boolean): Optional<A> =
		ofNullable(orNull()?.takeIf(predicate))

	final override inline fun partition(predicate: (A) -> Boolean): Pair<Optional<A>, Optional<A>> =
		Pair(filter(predicate), filter { !predicate(it) })

	final override inline fun <B: Any> mapNotNull(f: (A) -> B?): Optional<B> =
		flatMap { f(it).toOptional() }

	@Deprecated("Same as flatMap", replaceWith = ReplaceWith("flatMap"))
	final override inline fun <B: Any> mapNotNone(f: (A) -> Optional<B>) = flatMap(f)

	final override inline fun <B> map(f: (A) -> B): Optional<B> =
		flatMap { just(f(it)) }

	final override inline fun <B, R> lift2(fb: Context<OptionalContext, B>, f: (A, B) -> R): Optional<R> =
		flatMap { fb.asOptional.map(f.partial(it)) }

	final override inline infix fun <B> bind(f: (A) -> Context<OptionalContext, B>): Optional<B> =
		flatMap { f(it).asOptional }

	inline fun <B> flatMap(f: (A) -> Optional<B>): Optional<B> =
		fold(ifEmpty = { empty() }, ifSome = { f(it) })

	inline fun <R> fold(ifEmpty: () -> R, ifSome: (A) -> R): R {
		return ifSome(orNull() ?: return ifEmpty())
	}

	final override inline fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		maybe(monoid.empty, f)

	final override inline fun <R> foldL(initialValue: R, accumulator: (R, A) -> R) =
		maybe(initialValue) { accumulator(initialValue, it) }

	final override inline fun <R> foldR(initialValue: R, accumulator: (A, R) -> R) =
		maybe(initialValue) { accumulator(it, initialValue) }

	final override inline fun <B, R> zipWith(other: Context<OptionalContext, B>, f: (A, B) -> R) =
		lift2(other.asOptional, f)

	inline fun <B> maybe(b: B, f: (A) -> B): B = map(f) orElse b

	final override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Context<F, B>,
	): Context<F, Optional<B>> =
		fold(
			ifEmpty = { appScope.just(None) },
			ifSome = { appScope.map(f(it), ::Some) },
		)

	override fun combineWith(other: Context<OptionalContext, @UnsafeVariance A>) =
		orOptional(other.asOptional)

	companion object:
		MonadPlus.Scope<OptionalContext>,
		Traversable.Scope<OptionalContext> {
		override fun <A> empty(): Optional<A> = None
		override fun <A> just(a: A): Optional<A> = Some(a)
		fun <A> ofNullable(a: A?) = if (a != null) Some(a) else None

		fun <A: Semigroup<A>> monoid() = OptionalMonoid<A>()

		override fun <A> fromIterable(iterable: Iterable<A>) = iterable.firstOrNull().toOptional()
		override fun <A> fromSequence(sequence: Sequence<A>) = sequence.firstOrNull().toOptional()
		override fun <A> fromList(list: List<A>) = list.firstOrNull().toOptional()

		@Deprecated("Does not need conversion", replaceWith = ReplaceWith("optional"))
		override fun <A> fromOptional(optional: Optional<A>) = optional
	}
}

data class Some<T>(val value: T): Optional<T>() {
	override fun orNull() = value
}

object None: Optional<Nothing>() {
	override fun orNull() = null
}

internal typealias OptionalContext = Optional<*>

inline fun <A> Context<OptionalContext, A>.orNull() = asOptional.orNull()
inline fun <A> Context<OptionalContext, A>.isPresent() = asOptional.isPresent()
inline fun <A> Context<OptionalContext, A>.isAbsent() = asOptional.isAbsent()

inline infix fun <A> Context<OptionalContext, A>.orElse(a: A): A = orNull() ?: a
inline infix fun <A> Context<OptionalContext, A>.orElseGet(a: () -> A): A = orNull() ?: a()
inline infix fun <A> Context<OptionalContext, A>.orOptional(a: Context<OptionalContext, A>): Optional<A> =
	orOptionalGet { a }

inline fun <A, R> Context<OptionalContext, A>.fold(ifEmpty: () -> R, ifSome: (A) -> R): R =
	asOptional.fold(ifEmpty, ifSome)

inline infix fun <A> Context<OptionalContext, A>.orOptionalGet(a: () -> Context<OptionalContext, A>): Optional<A> =
	fold(ifEmpty = { a().asOptional }, ifSome = { Optional.just(it) })

inline fun <A: Any> A?.toOptional() = Optional.ofNullable(this)

inline val <A> Context<OptionalContext, A>.asOptional
	get() = this as Optional<A>

operator fun <A, R> Lift1<A, R>.invoke(
	opt: Context<OptionalContext, A>,
): Optional<R> = fmap(Optional, opt).asOptional

operator fun <A, B, R> Lift2<A, B, R>.invoke(
	opt1: Context<OptionalContext, A>,
	opt2: Context<OptionalContext, B>,
): Optional<R> = app(Optional, opt1, opt2).asOptional

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	opt1: Context<OptionalContext, A>,
	opt2: Context<OptionalContext, B>,
	opt3: Context<OptionalContext, C>,
): Optional<R> = app(Optional, opt1, opt2, opt3).asOptional

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	opt1: Context<OptionalContext, A>,
	opt2: Context<OptionalContext, B>,
	opt3: Context<OptionalContext, C>,
	opt4: Context<OptionalContext, D>,
): Optional<R> = app(Optional, opt1, opt2, opt3, opt4).asOptional

fun <A, R> liftOpt(f: (A) -> R): (Context<OptionalContext, A>) -> Optional<R> = lift(f)::invoke
fun <A, B, R> liftOpt2(f: (A, B) -> R): (Context<OptionalContext, A>, Context<OptionalContext, B>) -> Optional<R> =
	lift2(f)::invoke

fun <A, B, C, R> liftOpt3(f: (A, B, C) -> R): (Context<OptionalContext, A>, Context<OptionalContext, B>, Context<OptionalContext, C>) -> Optional<R> =
	lift3(f)::invoke

fun <A: Any, R: Any> liftNull(f: (A) -> R): (A?) -> R? = { it?.let(f) }
fun <A: Any, B: Any, R: Any> liftNull2(f: (A, B) -> R): (A?, B?) -> R? =
	t@{ a, b -> f(a ?: return@t null, b ?: return@t null) }

fun <A: Any, B: Any, C: Any, R: Any> liftNull3(f: (A, B, C) -> R): (A?, B?, C?) -> R? =
	t@{ a, b, c -> f(a ?: return@t null, b ?: return@t null, c ?: return@t null) }

inline fun <A, R: Any> optionalKleisli(f: (A) -> R?): Kleisli<OptionalContext, A, R> =
	Optional.kleisli(f composeForward { it.toOptional() })
