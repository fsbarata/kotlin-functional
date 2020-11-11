package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.Traversable
import java.io.Serializable

/**
 * Optional (Maybe) Monad
 *
 * Wraps a value that may or may not be present.
 */
@Suppress("OVERRIDE_BY_INLINE")
sealed class Optional<out A>:
	Monad<OptionalContext, A>,
	MonadZip<OptionalContext, A>,
	Traversable<OptionalContext, A>,
	Alternative<OptionalContext, A>,
	Serializable {
	override val scope get() = Optional

	abstract fun orNull(): A?

	fun isPresent() = orNull() != null
	fun isAbsent() = !isPresent()

	inline fun filter(predicate: (A) -> Boolean) =
		ofNullable(orNull()?.takeIf(predicate))

	final override inline fun <B> map(f: (A) -> B) =
		flatMap { just(f(it)) }

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
		fold(ifEmpty = { initialValue }, ifSome = { accumulator(initialValue, it) })

	final override inline fun <R> foldR(initialValue: R, accumulator: (A, R) -> R) =
		fold(ifEmpty = { initialValue }, ifSome = { accumulator(it, initialValue) })

	final override inline fun <B, R> zipWith(other: MonadZip<OptionalContext, B>, f: (A, B) -> R): Optional<R> {
		return flatMap { a -> other.asOptional.map { b -> f(a, b) } }
	}

	inline fun <B> maybe(b: B, f: (A) -> B): B = map(f) orElse b

	final override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Applicative<F, B>,
	): Applicative<F, Optional<B>> =
		fold(
			ifEmpty = { appScope.just(None) },
			ifSome = { f(it).map(::Some) },
		)

	override fun associateWith(other: Alternative<OptionalContext, @UnsafeVariance A>) =
		orOptionalGet { other.asOptional }

	companion object:
		Monad.Scope<OptionalContext>,
		Traversable.Scope<OptionalContext>,
		Alternative.Scope<OptionalContext> {
		override fun <A> empty(): Optional<A> = None
		override fun <A> just(a: A): Optional<A> = Some(a)
		fun <A> ofNullable(a: A?) = if (a != null) Some(a) else None

		fun <A> monoid(sg: Semigroup<A>) = object: Monoid<Optional<A>> {
			override val empty = empty<A>()
			override fun combine(a1: Optional<A>, a2: Optional<A>): Optional<A> =
				a1.map { a -> a2.map { otherA -> sg.combine(a, otherA) } orElse a } orOptional a2
		}
	}
}

data class Some<T>(val value: T): Optional<T>() {
	override fun orNull() = value
}

object None: Optional<Nothing>() {
	override fun orNull() = null
}

private typealias OptionalContext = Optional<*>

infix fun <A> Optional<A>.orElse(a: A) = orNull() ?: a
inline infix fun <A> Optional<A>.orElseGet(a: () -> A) = orNull() ?: a()
infix fun <A> Optional<A>.orOptional(a: Optional<A>) =
	orOptionalGet { a }

inline infix fun <A> Optional<A>.orOptionalGet(a: () -> Optional<A>): Optional<A> =
	fold(ifEmpty = a, ifSome = { Optional.just(it) })

fun <A: Any> A?.toOptional() = Optional.ofNullable(this)
fun <A: Any> A?.f() = toOptional()

val <A> Context<OptionalContext, A>.asOptional
	get() = this as Optional<A>
