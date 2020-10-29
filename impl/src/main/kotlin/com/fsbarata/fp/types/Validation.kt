package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Semigroup
import com.fsbarata.fp.types.Validation.Failure
import com.fsbarata.fp.types.Validation.Success
import java.io.Serializable

sealed class Validation<out E, out A>:
	Functor<Validation<Nothing, *>, A>,
	Serializable {
	data class Failure<out E>(val err: E): Validation<E, Nothing>()
	data class Success<out A>(val value: A): Validation<Nothing, A>()

	inline fun <R> fold(ifFailure: (E) -> R, ifSuccess: (A) -> R): R = when (this) {
		is Failure -> ifFailure(err)
		is Success -> ifSuccess(value)
	}

	final override inline fun <B> map(f: (A) -> B): Validation<E, B> = when (this) {
		is Failure -> this
		is Success -> Success(f(value))
	}

	fun toEither() = fold({ Either.Left(it) }, { Either.Right(it) })

	companion object {
		fun <E, A> fromEither(either: Either<E, A>): Validation<E, A> =
			either.fold({ Failure(it) }, { Success(it) })

		fun <E, B, A> liftError(either: Either<B, A>, f: (B) -> E): Validation<E, A> =
			either.fold({ Failure(f(it)) }, { Success(it) })

		fun <E, A> validationNel(either: Either<E, A>): Validation<NonEmptyList<E>, A> =
			liftError(either) { nelOf(it) }
	}
}

val <A> Context<Validation<Nothing, *>, A>.asValidation: Validation<Nothing, A>
	get() = this as Validation<Nothing, A>

inline fun <A> Validation<*, A>.orElse(a: () -> A): A = fold(ifFailure = { a() }, ifSuccess = { it })
inline fun <E, A> Validation<E, A>.valueOr(f: (E) -> A): A = fold(ifFailure = { f(it) }, ifSuccess = { it })

inline fun <E, A, B> Validation<E, A>.ensure(e: E, f: (A) -> Optional<B>): Validation<E, B> =
	bindValidation { a -> f(a).map { Success(it) }.orElseGet { Failure(e) } }

fun <A> Validation<A, A>.codiagonal() = fold({ it }, { it })

inline fun <E, A, B> Validation<E, A>.bindValidation(f: (A) -> Validation<E, B>): Validation<E, B> =
	fold(
		ifFailure = ::Failure,
		ifSuccess = f
	)

inline fun <E, A, B, R> Validation<E, A>.sequence(
	errSg: Semigroup<E>,
	other: Validation<E, B>,
	f: (A, B) -> R,
): Validation<E, R> =
	fold(
		ifFailure = { e1 ->
			Failure(other.fold(
				ifFailure = { with(errSg) { e1.combine(it) } },
				ifSuccess = { e1 }
			))
		},
		ifSuccess = { a -> other.map { f(a, it) } }
	)
