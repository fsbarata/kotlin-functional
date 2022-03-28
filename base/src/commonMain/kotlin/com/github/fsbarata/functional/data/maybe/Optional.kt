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

	final override inline fun filter(predicate: (A) -> Boolean) =
		ofNullable(orNull()?.takeIf(predicate))

	final override inline fun partition(predicate: (A) -> Boolean) =
		Pair(filter(predicate), filter { !predicate(it) })

	final override inline fun <B: Any> mapNotNull(f: (A) -> B?) =
		flatMap { f(it).toOptional() }

	@Deprecated("Same as flatMap", replaceWith = ReplaceWith("flatMap"))
	final override inline fun <B: Any> mapNotNone(f: (A) -> Optional<B>) = flatMap(f)

	final override inline fun <B> map(f: (A) -> B) =
		flatMap { just(f(it)) }

	final override inline fun <B, R> lift2(fb: Functor<OptionalContext, B>, f: (A, B) -> R) =
		flatMap { fb.asOptional.map(f.partial(it)) }

	final override inline infix fun <B> bind(f: (A) -> Context<OptionalContext, B>) =
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

	final override inline fun <B, R> zipWith(other: Functor<OptionalContext, B>, f: (A, B) -> R) =
		lift2(other.asOptional, f)

	inline fun <B> maybe(b: B, f: (A) -> B): B = map(f) orElse b

	final override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Functor<F, B>,
	): Functor<F, Optional<B>> =
		fold(
			ifEmpty = { appScope.just(None) },
			ifSome = { f(it).map(::Some) },
		)

	override fun associateWith(other: Context<OptionalContext, @UnsafeVariance A>) =
		orOptional(other.asOptional)

	companion object:
		MonadPlus.Scope<OptionalContext>,
		Traversable.Scope<OptionalContext> {
		override fun <A> empty(): Optional<A> = None
		override fun <A> just(a: A): Optional<A> = Some(a)
		fun <A> ofNullable(a: A?) = if (a != null) Some(a) else None

		fun <A: Semigroup<A>> monoid() = OptionalMonoid<A>()

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

infix fun <A> Optional<A>.orElse(a: A) = orNull() ?: a
inline infix fun <A> Optional<A>.orElseGet(a: () -> A) = orNull() ?: a()
infix fun <A> Optional<A>.orOptional(a: Optional<A>) =
	orOptionalGet { a }

inline infix fun <A> Optional<A>.orOptionalGet(a: () -> Optional<A>): Optional<A> =
	fold(ifEmpty = a, ifSome = { Optional.just(it) })

fun <A: Any> A?.toOptional() = Optional.ofNullable(this)

val <A> Context<OptionalContext, A>.asOptional
	get() = this as Optional<A>

operator fun <A, R> Lift1<A, R>.invoke(
	opt: Optional<A>,
): Optional<R> = fmap(opt).asOptional

operator fun <A, B, R> Lift2<A, B, R>.invoke(
	opt1: Optional<A>,
	opt2: Optional<B>,
): Optional<R> = app(opt1, opt2).asOptional

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	opt1: Optional<A>,
	opt2: Optional<B>,
	opt3: Optional<C>,
): Optional<R> = app(opt1, opt2, opt3).asOptional

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	opt1: Optional<A>,
	opt2: Optional<B>,
	opt3: Optional<C>,
	opt4: Optional<D>,
): Optional<R> = app(opt1, opt2, opt3, opt4).asOptional

fun <A, R> liftOpt(f: (A) -> R): (Optional<A>) -> Optional<R> = lift(f)::invoke
fun <A, B, R> liftOpt2(f: (A, B) -> R): (Optional<A>, Optional<B>) -> Optional<R> = lift2(f)::invoke
fun <A, B, C, R> liftOpt3(f: (A, B, C) -> R): (Optional<A>, Optional<B>, Optional<C>) -> Optional<R> = lift3(f)::invoke

fun <A: Any, R: Any> liftNull(f: (A) -> R): (A?) -> R? = { it?.let(f) }
fun <A: Any, B: Any, R: Any> liftNull2(f: (A, B) -> R): (A?, B?) -> R? =
	t@{ a, b -> f(a ?: return@t null, b ?: return@t null) }

fun <A: Any, B: Any, C: Any, R: Any> liftNull3(f: (A, B, C) -> R): (A?, B?, C?) -> R? =
	t@{ a, b, c -> f(a ?: return@t null, b ?: return@t null, c ?: return@t null) }

inline fun <A, R: Any> optionalKleisli(f: (A) -> R?): Kleisli<OptionalContext, A, R> =
	Optional.kleisli(f composeForward { it.toOptional() })
