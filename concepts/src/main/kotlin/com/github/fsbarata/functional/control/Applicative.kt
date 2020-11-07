package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.id

interface Applicative<C, out A>: Functor<C, A> {
	val scope: Scope<C>

	fun <B> ap(ff: Applicative<C, (A) -> B>): Applicative<C, B> =
		apFromLift(this, ff)

	fun <B, D> liftA2(f: (A) -> (B) -> D): (Applicative<C, B>) -> Applicative<C, D> =
		{ liftA2FromAp(this, it, f) }

	override fun <B> map(f: (A) -> B): Applicative<C, B> =
		ap(scope.just(f))

	interface Scope<C> {
		fun <A> just(a: A): Applicative<C, A>
	}
}

fun <C, A, B> apFromLift(app: Applicative<C, A>, ff: Applicative<C, (A) -> B>) =
	ff.liftA2(id())(app)

fun <C, A, B, R> liftA2FromAp(appA: Applicative<C, A>, appB: Applicative<C, B>, f: (A) -> (B) -> R) =
	appB.ap(appA.map(f))
