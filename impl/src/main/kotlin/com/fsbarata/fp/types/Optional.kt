package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

data class Optional<A> private constructor(
	val value: A?,
): Monad<Optional<*>, A>,
   Foldable<A> {
	inline fun orNull() = value

	infix fun orElse(a: A) = orElseGet { a }
	infix fun orElseGet(a: () -> A) = value ?: a()
	infix fun orOptional(a: Optional<A>) = orOptionalGet { a }
	infix fun orOptionalGet(a: () -> Optional<A>) = Optional(value ?: a().value)

	fun isPresent() = value != null
	fun isAbsent() = value == null

	override fun <B> just(b: B) = Optional.just(b)

	override fun <B> map(f: (A) -> B) =
		Optional(value?.let(f))

	override fun <B> ap(ff: Functor<Optional<*>, (A) -> B>): Optional<B> =
		ff.map { map(it) }.asOptional.value ?: empty()

	override fun <B> bind(f: (A) -> Functor<Optional<*>, B>) =
		flatMap { f(it).asOptional }

	fun <B> flatMap(f: (A) -> Optional<B>): Optional<B> =
		value?.let(f) ?: empty()

	fun <B> fold(ifEmpty: () -> B, ifSome: (A) -> B): B {
		return ifSome(value ?: return ifEmpty())
	}

	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R {
		return accumulator(initialValue, value ?: return initialValue)
	}

	companion object {
		fun <A> empty() = Optional<A>(null)
		fun <A> just(a: A) = Optional(a)
		fun <A> ofNullable(a: A?) = Optional(a)
	}
}

fun <A: Any> A?.toOptional() = Optional.ofNullable(this)
fun <A: Any> A?.f() = toOptional()

val <A> Context<Optional<*>, A>.asOptional
	get() = this as Optional<A>
