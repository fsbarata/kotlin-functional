package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.id

interface Applicative<F, out A>: Functor<F, A> {
	val scope: Scope<F>

	fun <R> ap(ff: Applicative<F, (A) -> R>): Applicative<F, R> =
		apFromLift(this, ff)

	fun <B, R> lift2(fb: Applicative<F, B>, f: (A) -> (B) -> R): Applicative<F, R> =
		liftA2FromAp(this, fb, f)

	override fun <B> map(f: (A) -> B): Applicative<F, B> =
		ap(scope.just(f))

	interface Scope<F> {
		fun <A> just(a: A): Applicative<F, A>
	}
}

fun <F, A, R> apFromLift(app: Applicative<F, A>, ff: Applicative<F, (A) -> R>): Applicative<F, R> =
	ff.lift2(app, id())

fun <F, A, B, R> liftA2FromAp(appA: Applicative<F, A>, appB: Applicative<F, B>, f: (A) -> (B) -> R) =
	appB.ap(appA.map(f))

fun <F, A, B, R> liftA2(fa: Applicative<F, A>, f: (A) -> (B) -> R): (Applicative<F, B>) -> Applicative<F, R> =
	{ fa.lift2(it, f) }