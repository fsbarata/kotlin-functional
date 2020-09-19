package com.fsbarata.fp.concepts

interface Monad<C, out A> : Applicative<C, A> {
	fun <B> bind(f: (A) -> Functor<C, B>): Monad<C, B>

	override fun <B> just(b: B): Monad<C, B>

	override fun <B> map(f: (A) -> B): Monad<C, B> = ap(just(f))

	override fun <B> ap(ff: Functor<C, (A) -> B>): Monad<C, B> =
			bind { a -> ff.map { it(a) } }
}

