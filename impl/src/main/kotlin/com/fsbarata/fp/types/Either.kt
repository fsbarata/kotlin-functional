package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad
import java.io.Serializable

sealed class Either<out E, out A>: Monad<Either<Nothing, *>, A>, Serializable {
	data class Left<out E>(val value: E): Either<E, Nothing>()
	data class Right<out A>(val value: A): Either<Nothing, A>()

	override val scope get() = Companion

	final override inline fun <B> map(f: (A) -> B): Either<E, B> =
		flatMap { Right(f(it)) }

	inline fun <B> mapLeft(f: (E) -> B): Either<B, A> {
		return fold(ifLeft = { Left(f(it)) }, { Right(it) })
	}

	inline fun <R> fold(ifLeft: (E) -> R, ifRight: (A) -> R): R = when (this) {
		is Left -> ifLeft(value)
		is Right -> ifRight(value)
	}

	override fun <B> bind(f: (A) -> Functor<Either<Nothing, *>, B>): Either<E, B> =
		flatMap { f(it).asEither }

	fun orNull() = fold({ null }, { it })
	fun toOptional(): Optional<A> = fold({ Optional.empty() }, { Optional.just(it) })

	companion object: Monad.Scope<Either<Nothing, *>> {
		override fun <A> just(a: A) = Right(a)
	}
}

val <A> Context<Either<Nothing, *>, A>.asEither: Either<Nothing, A>
	get() = this as Either<Nothing, A>

inline fun <E, A, B> Either<E, A>.flatMap(f: (A) -> Either<E, B>): Either<E, B> {
	return fold(ifLeft = { Either.Left(it) }, ifRight = { f(it) })
}

