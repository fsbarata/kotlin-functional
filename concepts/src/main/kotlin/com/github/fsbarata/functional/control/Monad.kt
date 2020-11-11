package com.github.fsbarata.functional.control

interface Monad<M, out A>: Applicative<M, A> {
	override val scope: Scope<M>

	infix fun <B> bind(f: (A) -> Context<M, B>): Monad<M, B>

	override fun <B> map(f: (A) -> B): Monad<M, B> = ap(scope.just(f))

	override infix fun <B> ap(ff: Applicative<M, (A) -> B>): Monad<M, B> =
		bind { a -> ff.map { it(a) } }

	interface Scope<C>: Applicative.Scope<C> {
		override fun <A> just(a: A): Monad<C, A>
	}
}
