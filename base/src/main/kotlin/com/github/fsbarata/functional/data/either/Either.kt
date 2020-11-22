package com.github.fsbarata.functional.data.either

import com.github.fsbarata.functional.BiContext
import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.maybe.Optional
import java.io.Serializable

/**
 * Either Monad
 *
 * Union between two values, where one is assumed to be right/successful, which biases the Monad operators such as map
 * and flatMap.
 */
@Suppress("OVERRIDE_BY_INLINE")
sealed class Either<out E, out A>:
	Monad<EitherContext<@UnsafeVariance E>, A>,
	BiFunctor<EitherBiContext, E, A>,
	Traversable<EitherContext<@UnsafeVariance E>, A>,
	Semigroup<Either<@UnsafeVariance E, @UnsafeVariance A>>,
	Serializable {
	data class Left<out E>(val value: E): Either<E, Nothing>()
	data class Right<out A>(val value: A): Either<Nothing, A>()

	override val scope get() = Scope<@UnsafeVariance E>()

	inline fun <R> fold(ifLeft: (E) -> R, ifRight: (A) -> R): R = when (this) {
		is Left -> ifLeft(value)
		is Right -> ifRight(value)
	}

	final override inline fun <B> map(f: (A) -> B): Either<E, B> =
		flatMap { Right(f(it)) }

	final override inline fun <B, R> lift2(
		fb: Applicative<EitherContext<@UnsafeVariance E>, B>,
		f: (A, B) -> R,
	): Either<E, R> =
		flatMap { fb.asEither.map(f.partial(it)) }

	final override inline fun <B> mapLeft(f: (E) -> B): Either<B, A> {
		return fold(ifLeft = { Left(f(it)) }, { Right(it) })
	}

	final override inline fun <C, D> bimap(f: (E) -> C, g: (A) -> D): Either<C, D> {
		return fold(ifLeft = { Left(f(it)) }, { Right(g(it)) })
	}

	final override inline infix fun <B> bind(f: (A) -> Context<EitherContext<@UnsafeVariance E>, B>): Either<E, B> =
		flatMap { f(it).asEither }

	final override inline fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		map(f) orElse monoid.empty

	final override inline fun <R> foldL(initialValue: R, accumulator: (R, A) -> R) =
		fold(ifLeft = { initialValue }, ifRight = { accumulator(initialValue, it) })

	final override inline fun <R> foldR(initialValue: R, accumulator: (A, R) -> R) =
		fold(ifLeft = { initialValue }, ifRight = { accumulator(it, initialValue) })

	final override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Applicative<F, B>,
	): Applicative<F, Either<E, B>> = fold(
		ifLeft = { appScope.just(Left(it)) },
		ifRight = { f(it).map(::Right) }
	)

	fun orNull() = fold({ null }, { it })
	fun toOptional(): Optional<A> = fold({ Optional.empty() }, { Optional.just(it) })

	fun swap() = fold(ifLeft = { Right(it) }, ifRight = { Left(it) })

	override fun combineWith(other: Either<@UnsafeVariance E, @UnsafeVariance A>) =
		fold(
			ifLeft = { other },
			ifRight = { Right(it) },
		)

	class Scope<E>: Monad.Scope<EitherContext<E>>, Traversable.Scope<EitherContext<E>> {
		override fun <A> just(a: A) = just<E, A>(a)
	}

	companion object {
		fun <E, A> just(a: A): Either<E, A> = Right(a)
		fun <E, A> ofNullable(a: A?, e: () -> E): Either<E, A> =
			a?.let(::Right) ?: Left(e())
	}
}

internal typealias EitherContext<E> = Either<E, *>
internal typealias EitherBiContext = Either<*, *>

@Suppress("UNCHECKED_CAST")
val <E, A> Context<EitherContext<E>, A>.asEither
	get() = this as Either<Nothing, A>

val <E, A> BiContext<EitherBiContext, E, A>.asEither
	get() = this as Either<E, A>

inline fun <E, A, B> Either<E, A>.flatMap(f: (A) -> Either<E, B>): Either<E, B> {
	return fold(ifLeft = { Either.Left(it) }, ifRight = { f(it) })
}

inline fun <E, A> Optional<A>.toEither(e: () -> E) =
	fold(ifEmpty = { Either.Left(e()) }, ifSome = { Either.Right(it) })

infix fun <A> Either<*, A>.orElse(a: A): A = valueOr { a }
inline infix fun <E, A> Either<E, A>.valueOr(f: (E) -> A): A = fold(ifLeft = f, ifRight = { it })

inline fun <E, A, B> Either<E, A>.ensure(e: E, f: (A) -> Optional<B>): Either<E, B> =
	flatMap { a -> f(a).toEither { e } }

operator fun <E, A, B, R> Lift2<A, B, R>.invoke(
	either1: Either<E, A>,
	either2: Either<E, B>,
): Either<E, R> = app(either1, either2).asEither

operator fun <E, A, B, C, R> Lift3<A, B, C, R>.invoke(
	either1: Either<E, A>,
	either2: Either<E, B>,
	either3: Either<E, C>,
): Either<E, R> = app(either1, either2, either3).asEither

operator fun <E, A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	either1: Either<E, A>,
	either2: Either<E, B>,
	either3: Either<E, C>,
	either4: Either<E, D>,
): Either<E, R> = app(either1, either2, either3, either4).asEither