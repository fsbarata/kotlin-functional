package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

sealed class Either<out T, out A>: Monad<Either<*, *>, A> {
	data class Left<out T>(val value: T): Either<T, Nothing>() {
		override fun <B> map(f: (Nothing) -> B) = this
		override fun <B> flatMap(f: (Nothing) -> Either<Nothing, B>) = this
		override fun <R> fold(ifLeft: (T) -> R, ifRight: (Nothing) -> R): R = ifLeft(value)
	}

	data class Right<out A>(val value: A): Either<Nothing, A>() {
		override fun <B> map(f: (A) -> B) = Right(f(value))
		override fun <B> flatMap(f: (A) -> Either<Nothing, B>) = f(value)
		override fun <R> fold(ifLeft: (Nothing) -> R, ifRight: (A) -> R): R = ifRight(value)
	}

	override fun <B> just(b: B): Either<T, B> = Right(b)

	abstract fun <B> flatMap(f: (A) -> Either<Nothing, B>): Either<T, B>
	abstract fun <R> fold(ifLeft: (T) -> R, ifRight: (A) -> R): R

	override fun <B> bind(f: (A) -> Functor<Either<*, *>, B>): Either<T, B> =
		flatMap { f(it).asEither }
}

val <A> Context<Either<*, *>, A>.asEither: Either<Nothing, A>
	get() = this as Either<Nothing, A>

