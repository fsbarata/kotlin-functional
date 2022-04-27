package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.list.startWithItem

interface Applicative<F, out A>: Functor<F, A> {
	override val scope: Scope<F>

	infix fun <R> ap(ff: Context<F, (A) -> R>): Applicative<F, R> =
		apFromLift2(scope, this, ff) as Applicative<F, R>

	fun <B, R> lift2(fb: Context<F, B>, f: (A, B) -> R): Applicative<F, R> =
		lift2FromAp(scope, this, fb, f) as Applicative<F, R>

	override fun <B> map(f: (A) -> B): Applicative<F, B> =
		ap(scope.just(f))

	interface Scope<F>: Functor.Scope<F> {
		fun <A> just(a: A): Context<F, A>

		fun <A, R> ap(app: Context<F, A>, ff: Context<F, (A) -> R>): Context<F, R> =
			(app as Applicative<F, A>).ap(ff)

		fun <A, B, R> lift2(fa: Context<F, A>, fb: Context<F, B>, f: (A, B) -> R): Context<F, R> =
			(fa as Applicative<F, A>).lift2(fb, f)
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

fun <F, A> Applicative<F, A>.replicate(times: Int): Applicative<F, NonEmptyList<A>> =
	if (times <= 1) map { nelOf(it) }
	else lift2(replicate(times - 1), NonEmptyList<A>::startWithItem.flip())
