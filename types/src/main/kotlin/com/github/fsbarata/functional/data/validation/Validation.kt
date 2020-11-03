package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.control.Context
import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.either.Either
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.partial
import com.github.fsbarata.functional.data.validation.Validation.Failure
import com.github.fsbarata.functional.data.validation.Validation.Success
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

	@Suppress("OVERRIDE_BY_INLINE")
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
	}
}

val <A> Context<Validation<Nothing, *>, A>.asValidation: Validation<Nothing, A>
	get() = this as Validation<Nothing, A>

inline fun <E, A> Optional<A>.toValidation(e: () -> E) =
	fold(ifEmpty = { Failure(e()) }, ifSome = { Success(it) })

fun <E, A> Either<E, A>.toValidation() =
	fold(ifLeft = { Failure(it) }, ifRight = { Success(it) })

fun <E, A> Either<E, A>.toValidationNel(): Validation<NonEmptyList<E>, A> =
	Validation.liftError(this) { nelOf(it) }

infix fun <A> Validation<*, A>.orElse(a: A): A = fold(ifFailure = { a }, ifSuccess = { it })
inline fun <E, A> Validation<E, A>.valueOr(f: (E) -> A): A = fold(ifFailure = { f(it) }, ifSuccess = { it })

inline fun <E, A, B> Validation<E, A>.ensure(e: E, f: (A) -> Optional<B>): Validation<E, B> =
	bindValidation { a -> f(a).toValidation { e } }

fun <A> Validation<A, A>.codiagonal() = fold({ it }, { it })

inline fun <E, A, B> Validation<E, A>.bindValidation(f: (A) -> Validation<E, B>): Validation<E, B> =
	fold(
		ifFailure = ::Failure,
		ifSuccess = f
	)

fun <E, A, R> Semigroup<E>.ap(
	v: Validation<E, A>,
	vf: Validation<E, (A) -> R>,
) =
	vf.fold(
		ifFailure = { e1 ->
			Failure(v.fold(
				ifFailure = { combine(e1, it) },
				ifSuccess = { e1 }
			))
		},
		ifSuccess = { f -> v.map { f(it) } }
	)

inline fun <E, A, B, R> Semigroup<E>.sequence(
	v1: Validation<E, A>,
	v2: Validation<E, B>,
	f: (A, B) -> R,
): Validation<E, R> = ap(v2, v1.map { f.partial(it) })