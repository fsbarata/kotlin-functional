package com.github.fsbarata.functional.control.arrow

import com.github.fsbarata.functional.control.Arrow
import com.github.fsbarata.functional.control.Category
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.data.F1

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
 * val k = Optional.kleisli(f) compose Optional.kleisli(g) // compiles
 * Optional.just(3).bind(k) // = Some("3.5")
 * f compose g // fails to compile
 */
class Kleisli<M, A, R> internal constructor(
	private val monadScope: Monad.Scope<M>,
	private val f: (A) -> Monad<M, R>,
): Arrow<Kleisli<M, *, *>, A, R>, F1<A, Monad<M, R>> by f {
	override val scope = Scope(monadScope)

	override infix fun <B> compose(other: Category<Kleisli<M, *, *>, B, A>): Kleisli<M, B, R> =
		Kleisli(monadScope) { other.asKleisli.f(it).bind(f) }

	override fun <PASS> first(): Kleisli<M, Pair<A, PASS>, Pair<R, PASS>> =
		Kleisli(monadScope) { (a, d) -> f(a).map { r -> Pair(r, d) } }

	override fun <PASS> second(): Kleisli<M, Pair<PASS, A>, Pair<PASS, R>> =
		Kleisli(monadScope) { (d, a) -> f(a).map { r -> Pair(d, r) } }

	override fun <B, RR> split(other: Arrow<Kleisli<M, *, *>, B, RR>): Kleisli<M, Pair<A, B>, Pair<R, RR>> {
		val otherKleisli = other.asKleisli
		return Kleisli(monadScope) { (a, d) ->
			f(a).bind { r -> otherKleisli(d).map { e -> Pair(r, e) } }
		}
	}

	override fun <RR> fanout(other: Arrow<Kleisli<M, *, *>, A, RR>): Kleisli<M, A, Pair<R, RR>> {
		val otherKleisli = other.asKleisli
		return Kleisli(monadScope) { a ->
			f(a).bind { r -> otherKleisli(a).map { d -> Pair(r, d) } }
		}
	}

	class Scope<M>(private val monadScope: Monad.Scope<M>): Arrow.Scope<Kleisli<M, *, *>> {
		override fun <A, R> arr(f: (A) -> R) = monadScope.kleisli<M, A, R> { monadScope.just(f(it)) }
	}
}

val <M, A, R> Category<Kleisli<M, *, *>, A, R>.asKleisli
	get() = this as Kleisli<M, A, R>

fun <M, A, R> Monad.Scope<M>.kleisli(f: (A) -> Monad<M, R>) =
	Kleisli(this, f)
