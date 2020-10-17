package com.fsbarata.fp.concepts

interface Applicative<C, out A>: Functor<C, A> {
	val scope: Scope<C>

	fun <B> ap(ff: Functor<C, (A) -> B>): Applicative<C, B>

	override fun <B> map(f: (A) -> B): Applicative<C, B> =
		ap(scope.just(f))

	interface Scope<C> {
		fun <A> just(a: A): Applicative<C, A>
	}
}
