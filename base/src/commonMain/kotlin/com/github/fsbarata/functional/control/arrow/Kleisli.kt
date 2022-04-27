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
	private val f: (A) -> Context<M, R>,
): F1<A, Context<M, R>> by f,
	ArrowChoice<Kleisli<M, *, *>, A, R>,
	ArrowApply<Kleisli<M, *, *>, A, R> {
	override val scope = KleisliScope(monadScope)

	override infix fun <B> compose(other: Category<Kleisli<M, *, *>, B, A>): Kleisli<M, B, R> =
		Kleisli(monadScope) { monadScope.bind(other.asKleisli.f(it), f) }

	override infix fun <RR> composeForward(other: Category<Kleisli<M, *, *>, R, RR>): Kleisli<M, A, RR> =
		Kleisli(monadScope) { monadScope.bind(f(it), other.asKleisli) }

	override fun <PASS> first(): Kleisli<M, Pair<A, PASS>, Pair<R, PASS>> =
		Kleisli(monadScope) { (a, d) -> monadScope.map(f(a)) { r -> Pair(r, d) } }

	override fun <PASS> second(): Kleisli<M, Pair<PASS, A>, Pair<PASS, R>> =
		Kleisli(monadScope) { (d, a) -> monadScope.map(f(a)) { r -> Pair(d, r) } }

	override fun <B, RR> split(other: Arrow<Kleisli<M, *, *>, B, RR>): Kleisli<M, Pair<A, B>, Pair<R, RR>> {
		val otherKleisli = other.asKleisli
		return Kleisli(monadScope) { (a, d) ->
			monadScope.bind(f(a)) { r -> monadScope.map(otherKleisli(d)) { e -> Pair(r, e) } }
		}
	}

	override fun <RR> fanout(other: Arrow<Kleisli<M, *, *>, A, RR>): Kleisli<M, A, Pair<R, RR>> {
		val otherKleisli = other.asKleisli
		return Kleisli(monadScope) { a ->
			monadScope.bind(f(a)) { r -> monadScope.map(otherKleisli(a)) { d -> Pair(r, d) } }
		}
	}

	override fun <PASS> left(): Kleisli<M, Either<A, PASS>, Either<R, PASS>> = splitChoice(scope.arr(id()))
	override fun <PASS> right(): Kleisli<M, Either<PASS, A>, Either<PASS, R>> = scope.arr(id<PASS>()).splitChoice(this)

	override infix fun <B, RR> splitChoice(other: ArrowChoice<Kleisli<M, *, *>, B, RR>): Kleisli<M, Either<A, B>, Either<R, RR>> =
		composeForward(scope.arr { Either.left<R, RR>(it) })
			.fanin(other.asKleisli.composeForward(scope.arr(::Right)))

	override infix fun <B> fanin(other: ArrowChoice<Kleisli<M, *, *>, B, R>): Kleisli<M, Either<A, B>, R> =
		Kleisli(monadScope, f faninWith other.asKleisli.f)
}

class KleisliScope<M>(private val monadScope: Monad.Scope<M>): ArrowApply.Scope<Kleisli<M, *, *>> {
	override fun <A, R> arr(f: (A) -> R): Kleisli<M, A, R> =
		monadScope.kleisli { monadScope.just(f(it)) }

	override fun <B> id(): Kleisli<M, B, B> = arr { it }

	override fun <A, R> app(): Kleisli<M, Pair<Kleisli<M, A, R>, A>, R> =
		Kleisli(monadScope) { (k, a) -> k.asKleisli(a) }
}

val <M, A, R> Category<Kleisli<M, *, *>, A, R>.asKleisli
	get() = this as Kleisli<M, A, R>

fun <M, A, R> Monad.Scope<M>.kleisli(f: (A) -> Context<M, R>): Kleisli<M, A, R> =
	Kleisli(this, f)

fun <M, A, R> Monad.Scope<M>.mapKleisli(f: (A) -> R): Kleisli<M, A, R> =
	kleisli(compose(::just, f))

fun <M, A, B, R> Monad.Scope<M>.lift2Kleisli(fb: Monad<M, B>, f: (A, B) -> R): Kleisli<M, A, R> =
	kleisli { a -> fb.map { b -> f(a, b) } }

val <M> Monad.Scope<M>.Kleisli get() = KleisliScope(this)

fun <M, A, B, R> composeKleisli(kleisli1: Kleisli<M, A, R>, kleisli2: Kleisli<M, B, A>) =
	kleisli1.compose(kleisli2)

fun <M, A, B, C, R> composeKleisli(kleisli1: Kleisli<M, A, R>, kleisli2: Kleisli<M, B, A>, kleisli3: Kleisli<M, C, B>) =
	kleisli1.compose(kleisli2).compose(kleisli3)

fun <M, A, B, C, D, R> composeKleisli(
	kleisli1: Kleisli<M, A, R>,
	kleisli2: Kleisli<M, B, A>,
	kleisli3: Kleisli<M, C, B>,
	kleisli4: Kleisli<M, D, C>,
) = kleisli1.compose(kleisli2).compose(kleisli3).compose(kleisli4)

fun <M, A, B, R> composeForwardKleisli(kleisli1: Kleisli<M, A, B>, kleisli2: Kleisli<M, B, R>) =
	kleisli1.composeForward(kleisli2)

