package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

sealed class Either<out T, out A>: Monad<Either<*, *>, A> {
	data class Left<out T>(val value: T): Either<T, Nothing>()
	data class Right<out U>(val value: U): Either<Nothing, U>()

	override fun <B> just(b: B): Either<T, B> = Right(b)

	override fun <B> map(f: (A) -> B): Either<T, B> =
		when (this) {
			is Left -> Left(value)
			is Right -> Right(f(value))
		}

	override fun <B> bind(f: (A) -> Functor<Either<*, *>, B>): Either<T, B> =
		when (this) {
			is Left -> Left(value)
			is Right -> f(value).asEither
		}

	fun <B> flatMap(f: (A) -> Either<Nothing, B>): Either<T, B> =
		when (this) {
			is Left -> Left(value)
			is Right -> f(value)
		}

	companion object {
		fun <L, R> left(value: L): Either<L, R> = Left(value)
		fun <L, R> right(value: R): Either<L, R> = Right(value)
	}
}

val <A> Context<Either<*, *>, A>.asEither: Either<Nothing, A>
	get() = this as Either<Nothing, A>

