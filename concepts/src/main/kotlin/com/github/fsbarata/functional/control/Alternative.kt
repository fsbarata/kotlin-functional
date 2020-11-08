package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.curry

interface Alternative<C, out A>: Applicative<C, A> {
	override val scope: Scope<C>

	fun associateWith(other: Alternative<C, @UnsafeVariance A>): Alternative<C, A>

	interface Scope<C>: Applicative.Scope<C> {
		fun <A> empty(): Alternative<C, A>
		override fun <A> just(a: A): Alternative<C, A>
	}
}

fun <C, A> some(alt: Alternative<C, A>): Alternative<C, List<A>> =
	liftA2(alt, { a: A, la: List<A> -> la + a }.curry())(many(alt)) as Alternative<C, List<A>>

fun <C, A> many(alt: Alternative<C, A>): Alternative<C, List<A>> =
	some(alt).associateWith(alt.scope.just(emptyList()))
