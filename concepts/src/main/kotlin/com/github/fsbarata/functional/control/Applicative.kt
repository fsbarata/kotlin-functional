package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.id

interface Applicative<F, out A>: Functor<F, A> {
	val scope: Scope<F>

	fun <B> ap(ff: Applicative<F, (A) -> B>): Applicative<F, B> =
		apFromLift(this, ff)

	fun <B, R> liftA2(f: (A) -> (B) -> R): (Applicative<F, B>) -> Applicative<F, R> =
		{ liftA2FromAp(this, it, f) }

	override fun <B> map(f: (A) -> B): Applicative<F, B> =
		ap(scope.just(f))

	interface Scope<C> {
		fun <A> just(a: A): Applicative<C, A>
	}
}

fun <F, A, B> apFromLift(app: Applicative<F, A>, ff: Applicative<F, (A) -> B>) =
	ff.liftA2(id())(app)

fun <F, A, B, R> liftA2FromAp(appA: Applicative<F, A>, appB: Applicative<F, B>, f: (A) -> (B) -> R) =
	appB.ap(appA.map(f))
