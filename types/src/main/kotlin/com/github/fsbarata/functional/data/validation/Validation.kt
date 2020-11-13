package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.control.Context
import com.github.fsbarata.functional.data.BiFunctor
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.either.Either
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.validation.Validation.Failure
import com.github.fsbarata.functional.data.validation.Validation.Success
import java.io.Serializable

@Suppress("OVERRIDE_BY_INLINE")
sealed class Validation<out E, out A>:
	Functor<ValidationContext<@UnsafeVariance E>, A>,
	BiFunctor<ValidationContext<Nothing>, E, A>,
	Serializable {
	data class Failure<out E>(val err: E): Validation<E, Nothing>()
	data class Success<out A>(val value: A): Validation<Nothing, A>()

	inline fun <R> fold(ifFailure: (E) -> R, ifSuccess: (A) -> R): R = when (this) {
		is Failure -> ifFailure(err)
		is Success -> ifSuccess(value)
	}

	final override inline fun <C> mapLeft(f: (E) -> C): Validation<C, A> = when (this) {
		is Failure -> Failure(f(err))
		is Success -> this
	}

	final override inline fun <B> map(f: (A) -> B): Validation<E, B> = when (this) {
		is Failure -> this
		is Success -> Success(f(value))
	}

	final override inline fun <C, D> bimap(f: (E) -> C, g: (A) -> D): Validation<C, D> = when (this) {
		is Failure -> Failure(f(err))
		is Success -> Success(g(value))
	}

	fun toEither() = fold(ifFailure = { Either.Left(it) }, ifSuccess = { Either.Right(it) })
	fun toOptional() = fold(ifFailure = { Optional.empty() }, ifSuccess = { Optional.just(it) })

	inline fun <EE, AA> withEither(f: (Either<E, A>) -> Either<EE, AA>): Validation<EE, AA> =
		fromEither(f(toEither()))

	fun toValidationNel() = mapLeft { nelOf(it) }

	companion object {
		inline fun <E, A> fromOptional(optional: Optional<A>, e: () -> E): Validation<E, A> =
			optional.toValidation(e)

		fun <E, A> fromEither(either: Either<E, A>): Validation<E, A> =
			either.toValidation()

		fun <E, B, A> liftError(either: Either<B, A>, f: (B) -> E): Validation<E, A> =
			either.fold({ Failure(f(it)) }, { Success(it) })

		fun <E> applicative(semigroup: Semigroup<E>) =
			ValidationApplicative(semigroup)

		fun <E, T> applicative(semigroup: Semigroup<E>, f: ValidationApplicative<E>.() -> T): T =
			ValidationApplicative(semigroup).run(f)
	}
}

internal typealias ValidationContext<E> = Validation<E, *>

@Suppress("UNCHECKED_CAST")
val <E, A> Context<ValidationContext<E>, A>.asValidation
	get() = this as Validation<E, A>

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
