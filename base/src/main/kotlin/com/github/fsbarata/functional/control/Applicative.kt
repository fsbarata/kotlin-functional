package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.list.startWithItem

interface Applicative<F, out A>: Functor<F, A> {
	val scope: Scope<F>

	infix fun <R> ap(ff: Functor<F, (A) -> R>): Applicative<F, R> =
		apFromLift2(scope, this, ff) as Applicative<F, R>

	fun <B, R> lift2(fb: Functor<F, B>, f: (A, B) -> R): Applicative<F, R> =
		lift2FromAp(scope, this, fb, f) as Applicative<F, R>

	override fun <B> map(f: (A) -> B): Functor<F, B> =
		ap(scope.just(f))

	interface Scope<F> {
		fun <A> just(a: A): Functor<F, A>

		fun <A, R> ap(app: Functor<F, A>, ff: Functor<F, (A) -> R>): Functor<F, R> =
			(app as Applicative<F, A>).ap(ff)

		fun <A, B, R> lift2(fa: Functor<F, A>, fb: Functor<F, B>, f: (A, B) -> R): Functor<F, R> =
			(fa as Applicative<F, A>).lift2(fb, f)
	}
}

fun <F, A, R> apFromLift2(scope: Applicative.Scope<F>, app: Functor<F, A>, ff: Functor<F, (A) -> R>): Functor<F, R> =
	scope.lift2(ff, app, id<(A) -> R>().uncurry())

fun <F, A, B, R> lift2FromAp(
	scope: Applicative.Scope<F>,
	appA: Functor<F, A>,
	appB: Functor<F, B>,
	f: (A, B) -> R,
): Functor<F, R> =
	scope.ap(appB, appA.map(f.curry()))

fun <F, A> Applicative<F, A>.replicate(times: Int): Functor<F, NonEmptyList<A>> =
	if (times <= 1) map { nelOf(it) }
	else lift2(replicate(times - 1), NonEmptyList<A>::startWithItem.flip())
