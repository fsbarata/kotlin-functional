package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.id

interface Applicative<C, out A>: Functor<C, A> {
	val scope: Scope<C>

	fun <B> ap(ff: Applicative<C, (A) -> B>): Applicative<C, B> =
		ff.liftA2(id())(this)

	fun <B, D> liftA2(f: (A) -> (B) -> D): (Applicative<C, B>) -> Applicative<C, D> =
		{ app -> app.ap(map { f(it) }) }

	override fun <B> map(f: (A) -> B): Applicative<C, B> =
		ap(scope.just(f))

	interface Scope<C> {
		fun <A> just(a: A): Applicative<C, A>
	}
}
