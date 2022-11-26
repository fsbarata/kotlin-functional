package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.curry
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.uncurry

interface Applicative<F, out A>: Functor<F, A> {
	val scope: Scope<F>

	infix fun <R> ap(ff: Context<F, (A) -> R>): Applicative<F, R> =
		apFromLift2(scope, this, ff) as Applicative<F, R>

	fun <B, R> lift2(fb: Context<F, B>, f: (A, B) -> R): Applicative<F, R> =
		lift2FromAp(scope, this, fb, f) as Applicative<F, R>

	override fun <B> map(f: (A) -> B): Applicative<F, B> =
		ap(scope.just(f))

	interface Scope<F>: Functor.Scope<F> {
		fun <A> just(a: A): Context<F, A>

		fun <A, R> ap(fa: Context<F, A>, ff: Context<F, (A) -> R>): Context<F, R> =
			if (fa is Applicative) fa.ap(ff)
			else apFromLift2(this, fa, ff)

		fun <A, B, R> lift2(fa: Context<F, A>, fb: Context<F, B>, f: (A, B) -> R): Context<F, R> =
			if (fa is Applicative) fa.lift2(fb, f)
			else lift2FromAp(this, fa, fb, f)

		override fun <A, B> map(ca: Context<F, A>, f: (A) -> B): Context<F, B> =
			if (ca is Functor) ca.map(f)
			else ap(ca, just(f))
	}
}

fun <F, A, R> apFromLift2(scope: Applicative.Scope<F>, app: Context<F, A>, ff: Context<F, (A) -> R>): Context<F, R> =
	scope.lift2(ff, app, uncurry(id<(A) -> R>()))

fun <F, A, B, R> lift2FromAp(
	scope: Applicative.Scope<F>,
	appA: Context<F, A>,
	appB: Context<F, B>,
	f: (A, B) -> R,
): Context<F, R> =
	scope.ap(appB, scope.map(appA, f.curry()))
