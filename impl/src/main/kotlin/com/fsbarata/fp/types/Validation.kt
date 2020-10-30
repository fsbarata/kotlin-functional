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

	fun toEither() = fold(ifFailure = { Either.Left(it) }, ifSuccess = { Either.Right(it) })
	fun toOptional() = fold(ifFailure = { Optional.empty() }, ifSuccess = { Optional.just(it) })

	inline fun <EE, AA> withEither(f: (Either<E, A>) -> Either<EE, AA>): Validation<EE, AA> =
		fromEither(f(toEither()))

	fun toValidationNel() =
		fold({ Failure(nelOf(it)) }, { Success(it) })

	companion object {
		inline fun <E, A> fromOptional(optional: Optional<A>, e: () -> E): Validation<E, A> =
			optional.toValidation(e)

		fun <E, A> fromEither(either: Either<E, A>): Validation<E, A> =
			either.toValidation()

		fun <E, B, A> liftError(either: Either<B, A>, f: (B) -> E): Validation<E, A> =
			either.fold({ Failure(f(it)) }, { Success(it) })

		fun <E, A> validationNel(either: Either<E, A>): Validation<NonEmptyList<E>, A> =
			liftError(either) { nelOf(it) }
	}
}

val <A> Context<Validation<Nothing, *>, A>.asValidation: Validation<Nothing, A>
	get() = this as Validation<Nothing, A>

inline fun <E, A> Optional<A>.toValidation(e: () -> E) =
	fold(ifEmpty = { Failure(e()) }, ifSome = { Success(it) })

fun <E, A> Either<E, A>.toValidation() =
	fold(ifLeft = { Failure(it) }, ifRight = { Success(it) })

inline fun <A> Validation<*, A>.orElse(a: () -> A): A = fold(ifFailure = { a() }, ifSuccess = { it })
inline fun <E, A> Validation<E, A>.valueOr(f: (E) -> A): A = fold(ifFailure = { f(it) }, ifSuccess = { it })

inline fun <E, A, B> Validation<E, A>.ensure(e: E, f: (A) -> Optional<B>): Validation<E, B> =
	bindValidation { a -> f(a).toValidation { e } }

fun <A> Validation<A, A>.codiagonal() = fold({ it }, { it })

fun <E, A, EE, AA> validationed(f: (Either<E, A>) -> Either<EE, AA>): (Validation<E, A>) -> Validation<EE, AA> =
	{ it.withEither(f) }

inline fun <E, A, B> Validation<E, A>.bindValidation(f: (A) -> Validation<E, B>): Validation<E, B> =
	fold(
		ifFailure = ::Failure,
		ifSuccess = f
	)

inline fun <E, A, B, R> Semigroup<E>.sequence(
	v1: Validation<E, A>,
	v2: Validation<E, B>,
	f: (A, B) -> R,
): Validation<E, R> =
	v1.fold(
		ifFailure = { e1 ->
			Failure(v2.fold(
				ifFailure = { e1.combine(it) },
				ifSuccess = { e1 }
			))
		},
		ifSuccess = { a -> v2.map { f(a, it) } }
	)