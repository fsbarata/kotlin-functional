package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.*

interface Applicative<F, out A>: Functor<F, A> {
	val scope: Scope<F>

	infix fun <R> ap(ff: Applicative<F, (A) -> R>): Applicative<F, R> =
		apFromLift2(this, ff)

	fun <B, R> lift2(fb: Applicative<F, B>, f: (A, B) -> R): Applicative<F, R> =
		lift2FromAp(this, fb, f)

	override fun <B> map(f: (A) -> B): Applicative<F, B> =
		ap(scope.just(f))

	interface Scope<F> {
		fun <A> just(a: A): Applicative<F, A>
	}
}

fun <F, A, R> apFromLift2(app: Applicative<F, A>, ff: Applicative<F, (A) -> R>): Applicative<F, R> =
	ff.lift2(app, id<(A) -> R>().uncurry())

fun <F, A, B, R> lift2FromAp(appA: Applicative<F, A>, appB: Applicative<F, B>, f: (A, B) -> R) =
	appB.ap(appA.map(f.curry()))
