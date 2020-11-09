package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.curry
import com.github.fsbarata.functional.data.curry2
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.uncurry

interface Applicative<F, out A>: Functor<F, A> {
	val scope: Scope<F>

	fun <R> ap(ff: Applicative<F, (A) -> R>): Applicative<F, R> =
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

fun <F, A, B, R> liftAC2(fa: Applicative<F, A>, f: (A) -> (B) -> R): (Applicative<F, B>) -> Applicative<F, R> =
	{ fa.lift2(it, f.uncurry()) }

fun <F, A, B, C, R> Applicative<F, A>.liftA3(
	fb: Applicative<F, B>,
	fc: Applicative<F, C>,
	f: (A, B, C) -> R,
): Applicative<F, R> = fc.ap(lift2(fb, f.curry2()))

fun <F, A, B, C, R> liftAC3(
	fa: Applicative<F, A>,
	f: (A) -> (B) -> (C) -> R
): (Applicative<F, B>, Applicative<F, C>) -> Applicative<F, R> =
	{ fb, fc -> fa.liftA3(fb, fc, f.uncurry()) }
