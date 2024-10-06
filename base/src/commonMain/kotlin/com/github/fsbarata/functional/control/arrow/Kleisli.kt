package com.github.fsbarata.functional.control.arrow

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.F1
import com.github.fsbarata.functional.data.compose
import com.github.fsbarata.functional.data.either.Either
import com.github.fsbarata.functional.data.either.Either.Right
import com.github.fsbarata.functional.data.id

/**
 * Kleisli Arrow
 *
 * Wraps a transformation that can be used in {@link com.github.fsbarata.functional.control.Monad#bind}
 *
 * The common use is to compose multiple bind functions into one, as they cannot be directly composed.
 *
 * Eg.:
 * val f = { b: Double -> Optional.just("$b") }
 * val g = { a: Int -> Optional.just(a + 0.5) }
 * f compose g // fails to compile
 * val k = Optional.kleisli(f) compose Optional.kleisli(g) // compiles
 * Optional.just(3).bind(k) // = Some("3.5")
 */
class Kleisli<M, A, R> internal constructor(
	private val monadScope: Monad.Scope<M>,
	private val f: F1<A, Context<M, R>>,
): F1<A, Context<M, R>> by f,
	ArrowChoice<Kleisli<M, *, *>, A, R> {
	override val scope = KleisliScope(monadScope)

	override infix fun <B> compose(other: Category<Kleisli<M, *, *>, B, A>): Kleisli<M, B, R> =
		composeKleisli(other.asKleisli)

	infix fun <B> composeKleisli(other: F1<B, Context<M, A>>): Kleisli<M, B, R> =
		Kleisli(monadScope) { monadScope.bind(other(it), f) }

	override infix fun <RR> composeForward(other: Category<Kleisli<M, *, *>, R, RR>): Kleisli<M, A, RR> =
		composeForwardKleisli(other.asKleisli)

	infix fun <RR> composeForwardKleisli(other: F1<R, Context<M, RR>>): Kleisli<M, A, RR> =
		Kleisli(monadScope) { monadScope.bind(f(it), other) }

	override fun <PASS> first(): Kleisli<M, Pair<A, PASS>, Pair<R, PASS>> =
		Kleisli(monadScope) { (a, d) -> monadScope.map(f(a)) { r -> Pair(r, d) } }

	override fun <PASS> second(): Kleisli<M, Pair<PASS, A>, Pair<PASS, R>> =
		Kleisli(monadScope) { (d, a) -> monadScope.map(f(a)) { r -> Pair(d, r) } }

	override fun <B, RR> split(other: Category<Kleisli<M, *, *>, B, RR>): Kleisli<M, Pair<A, B>, Pair<R, RR>> {
		val otherKleisli = other.asKleisli
		return Kleisli(monadScope) { (a, d) ->
			monadScope.bind(f(a)) { r -> monadScope.map(otherKleisli(d)) { e -> Pair(r, e) } }
		}
	}

	override fun <RR> fanout(other: Category<Kleisli<M, *, *>, A, RR>): Kleisli<M, A, Pair<R, RR>> {
		val otherKleisli = other.asKleisli
		return Kleisli(monadScope) { a ->
			monadScope.bind(f(a)) { r -> monadScope.map(otherKleisli(a)) { d -> Pair(r, d) } }
		}
	}

	override fun <PASS> left() = super.left<PASS>() as Kleisli

	override fun <PASS> right() = super.right<PASS>() as Kleisli

	override infix fun <B, RR> splitChoice(other: Category<Kleisli<M, *, *>, B, RR>): Kleisli<M, Either<A, B>, Either<R, RR>> =
		composeForward(scope.arr { Either.left<R, RR>(it) })
			.fanin(other.asKleisli.composeForward(scope.arr(::Right)))

	override infix fun <B> fanin(other: Category<Kleisli<M, *, *>, B, R>): Kleisli<M, Either<A, B>, R> =
		Kleisli(monadScope, f faninWith other.asKleisli.f)
}

class KleisliScope<M>(private val monadScope: Monad.Scope<M>):
	ArrowChoice.Scope<Kleisli<M, *, *>>,
	ArrowApply.Scope<Kleisli<M, *, *>> {
	override fun <A, R> arr(f: (A) -> R): Kleisli<M, A, R> =
		monadScope.kleisli { monadScope.just(f(it)) }

	override fun <B> id(): Kleisli<M, B, B> = arr { it }

	override fun <A, R> app(): Kleisli<M, Pair<Kleisli<M, A, R>, A>, R> =
		Kleisli(monadScope) { (k, a) -> k.asKleisli(a) }
}

val <M, A, R> Category<Kleisli<M, *, *>, A, R>.asKleisli
	get() = this as Kleisli

fun <M, A, R> Monad.Scope<M>.kleisli(f: F1<A, Context<M, R>>): Kleisli<M, A, R> =
	if (f is Kleisli) f
	else Kleisli(this, f)

fun <M, A, R> Monad.Scope<M>.mapKleisli(f: (A) -> R): Kleisli<M, A, R> =
	kleisli(compose(::just, f))

fun <M, A, B, R> Monad.Scope<M>.lift2Kleisli(fb: Monad<M, B>, f: (A, B) -> R): Kleisli<M, A, R> =
	kleisli { a -> fb.map { b -> f(a, b) } }

val <M> Monad.Scope<M>.Kleisli get() = KleisliScope(this)

fun <M, A, B, R> composeKleisli(kleisli1: Kleisli<M, A, R>, f2: F1<B, Context<M, A>>): Kleisli<M, B, R> =
	kleisli1.composeKleisli(f2)

fun <M, A, B, C, R> composeKleisli(
	kleisli1: Kleisli<M, A, R>,
	f2: F1<B, Context<M, A>>,
	f3: F1<C, Context<M, B>>,
): Kleisli<M, C, R> = kleisli1.composeKleisli(f2).composeKleisli(f3)

fun <M, A, B, C, D, R> composeKleisli(
	kleisli1: Kleisli<M, A, R>,
	f2: F1<B, Context<M, A>>,
	f3: F1<C, Context<M, B>>,
	f4: F1<D, Context<M, C>>,
): Kleisli<M, D, R> = kleisli1.composeKleisli(f2).composeKleisli(f3).composeKleisli(f4)

fun <M, A, B, R> composeForwardKleisli(kleisli1: Kleisli<M, A, B>, f2: F1<B, Context<M, R>>) =
	kleisli1.composeForwardKleisli(f2)

fun <M, A, B, R> Monad.Scope<M>.composeKleisli(f1: F1<A, Context<M, R>>, f2: F1<B, Context<M, A>>): Kleisli<M, B, R> =
	kleisli(f1).composeKleisli(f2)

fun <M, A, B, R> Monad.Scope<M>.composeForwardKleisli(
	f1: F1<A, Context<M, B>>,
	f2: F1<B, Context<M, R>>
): Kleisli<M, A, R> =
	kleisli(f1).composeForwardKleisli(f2)

