package com.fsbarata.fp.concepts

interface Monad<C, A> : Applicative<C, A> {
	fun <B> flatMap(f: (A) -> Functor<C, B>): Monad<C, B>

	override fun <B> just(b: B): Monad<C, B>

	override fun <B> map(f: (A) -> B): Monad<C, B> = ap(just(f))

	override fun <B> ap(ff: Functor<C, (A) -> B>): Monad<C, B> =
			flatMap { a -> ff.map { c -> c(a) } as Monad<C, B> }
}

