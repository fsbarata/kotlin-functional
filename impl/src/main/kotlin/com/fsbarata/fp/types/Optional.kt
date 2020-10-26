package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.monad.MonadZip
import java.io.Serializable

/**
 * Optional (Maybe) Monad
 *
 * Wraps a value that may or may not be present.
 */
sealed class Optional<out A>:
	Monad<Optional<*>, A>,
	MonadZip<Optional<*>, A>,
	Foldable<A>,
	Serializable {
	override val scope get() = Companion

	abstract fun orNull(): A?

	fun isPresent() = orNull() != null
	fun isAbsent() = !isPresent()

	inline fun filter(predicate: (A) -> Boolean) =
		ofNullable(orNull()?.takeIf(predicate))

	override fun <B> map(f: (A) -> B) =
		flatMap { just(f(it)) }

	override fun <B> bind(f: (A) -> Context<Optional<*>, B>) =
		flatMap { f(it).asOptional }

	inline fun <B> flatMap(f: (A) -> Optional<B>): Optional<B> =
		fold(ifEmpty = { empty<B>() }, ifSome = { f(it) })

	inline fun <R> fold(ifEmpty: () -> R, ifSome: (A) -> R): R {
		return ifSome(orNull() ?: return ifEmpty())
	}

	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R {
		return fold(ifEmpty = { initialValue }, ifSome = { accumulator(initialValue, it) })
	}

	override fun <B, R> zipWith(other: MonadZip<Optional<*>, B>, f: (A, B) -> R): Optional<R> {
		return flatMap { a -> other.asOptional.map { b -> f(a, b) } }
	}

	companion object: Monad.Scope<Optional<*>> {
		fun <A> empty(): Optional<A> = None
		override fun <A> just(a: A): Optional<A> = Some(a)
		fun <A> ofNullable(a: A?) = if (a != null) Some(a) else None
	}
}

data class Some<T>(val value: T): Optional<T>() {
	override fun orNull() = value
}

object None: Optional<Nothing>() {
	override fun orNull() = null
}

infix fun <A> Optional<A>.orElse(a: A) = orNull() ?: a
inline infix fun <A> Optional<A>.orElseGet(a: () -> A) = orNull() ?: a()
infix fun <A> Optional<A>.orOptional(a: Optional<A>) =
	orOptionalGet { a }

inline infix fun <A> Optional<A>.orOptionalGet(a: () -> Optional<A>) =
	map { Optional.just(it) }.orElseGet(a)

fun <A: Any> A?.toOptional() = Optional.ofNullable(this)
fun <A: Any> A?.f() = toOptional()

val <A> Context<Optional<*>, A>.asOptional
	get() = this as Optional<A>
