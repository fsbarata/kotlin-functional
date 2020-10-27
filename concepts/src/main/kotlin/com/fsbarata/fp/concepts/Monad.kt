package com.fsbarata.fp.concepts

interface Monad<C, out A>: Applicative<C, A> {
	override val scope: Scope<C>

	fun <B> bind(f: (A) -> Context<C, B>): Monad<C, B>

	override fun <B> map(f: (A) -> B): Monad<C, B> = ap(scope.just(f))

	override fun <B> ap(ff: Functor<C, (A) -> B>): Monad<C, B> =
		bind { a -> ff.map { it(a) } }

	interface Scope<C>: Applicative.Scope<C> {
		override fun <A> just(a: A): Monad<C, A>
	}
}

fun <C> liftM(): LiftM<C> = object : LiftM<C> {}

interface LiftM<C> {
	operator fun <A, B> invoke(f: (A) -> B): (Monad<C, A>) -> Monad<C, B> = { it.map(f) }
}
