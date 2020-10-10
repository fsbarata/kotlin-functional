package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

sealed class Either<out E, out A>: Monad<Either<*, *>, A> {
	data class Left<out E>(val value: E): Either<E, Nothing>()
	data class Right<out A>(val value: A): Either<Nothing, A>()

	override fun <B> just(b: B): Either<E, B> = Right(b)

	final override inline fun <B> map(f: (A) -> B): Either<E, B> =
		flatMap { Right(f(it)) }

	inline fun <B> mapLeft(f: (E) -> B): Either<B, A> {
		return fold(ifLeft = { Left(f(it)) }, { Right(it) })
	}

	inline fun <R> fold(ifLeft: (E) -> R, ifRight: (A) -> R): R = when (this) {
		is Left -> ifLeft(value)
		is Right -> ifRight(value)
	}

	override fun <B> bind(f: (A) -> Functor<Either<*, *>, B>): Either<E, B> =
		flatMap { f(it).asEither }

	fun orNull() = fold({ null }, { it })
	fun toOptional(): Optional<A> = fold({ Optional.empty() }, { Optional.just(it) })
}

val <A> Context<Either<*, *>, A>.asEither: Either<Nothing, A>
	get() = this as Either<Nothing, A>

inline fun <E, A, B> Either<E, A>.flatMap(f: (A) -> Either<E, B>): Either<E, B> {
	return fold(ifLeft = { Either.Left(it) }, ifRight = { f(it) })
}

