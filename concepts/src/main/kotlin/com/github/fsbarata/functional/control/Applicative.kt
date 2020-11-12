package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.curry
import com.github.fsbarata.functional.data.curry2
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.uncurry

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

fun <A, B, R> lift2(f: (A, B) -> R) = Lift2(f)

class Lift2<A, B, R>(private val f: (A, B) -> R) {
	operator fun <F> invoke(fa: Applicative<F, A>, fb: Applicative<F, B>): Applicative<F, R> =
		fa.lift2(fb, f)
}

fun <A, B, C, R> lift3(f: (A, B, C) -> R) = Lift3(f)

class Lift3<A, B, C, R>(private val f: (A, B, C) -> R) {
	operator fun <F> invoke(
		fa: Applicative<F, A>,
		fb: Applicative<F, B>,
		fc: Applicative<F, C>,
	): Applicative<F, R> =
		fc.ap(lift2(f.curry2())(fa, fb))
}
