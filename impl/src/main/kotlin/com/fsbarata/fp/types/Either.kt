package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Monad
import java.io.Serializable

/**
 * Either Monad
 *
 * Union between two values, where one is assumed to be right/successful, which biases the Monad operators such as map
 * and flatMap.
 */
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

	override fun <B> bind(f: (A) -> Context<Either<Nothing, *>, B>): Either<E, B> =
		flatMap { f(it).asEither }

	fun orNull() = fold({ null }, { it })
	fun toOptional(): Optional<A> = fold({ Optional.empty() }, { Optional.just(it) })

	fun swap() = fold(ifLeft = { Right(it) }, ifRight = { Left(it) })

	companion object: Monad.Scope<Either<Nothing, *>> {
		override fun <A> just(a: A) = Right(a)
	}
}

val <A> Context<Either<Nothing, *>, A>.asEither: Either<Nothing, A>
	get() = this as Either<Nothing, A>

inline fun <E, A, B> Either<E, A>.flatMap(f: (A) -> Either<E, B>): Either<E, B> {
	return fold(ifLeft = { Either.Left(it) }, ifRight = { f(it) })
}

inline fun <E, A> Optional<A>.toEither(e: () -> E) =
	fold(ifEmpty = { Either.Left(e()) }, ifSome = { Either.Right(it) })

inline fun <A> Either<*, A>.orElse(a: () -> A): A = fold(ifLeft = { a() }, ifRight = { it })
inline fun <E, A> Either<E, A>.valueOr(f: (E) -> A): A = fold(ifLeft = { f(it) }, ifRight = { it })

inline fun <E, A, B> Either<E, A>.ensure(e: E, f: (A) -> Optional<B>): Either<E, B> =
	flatMap { a -> f(a).toEither { e } }
