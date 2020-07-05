package com.fsbarata.fp.concepts

interface Applicative<C, A> : Functor<C, A> {
	fun <B> just(b: B): Applicative<C, B>

	fun <B> ap(ff: Functor<C, (A) -> B>): Applicative<C, B>

	override fun <B> map(f: (A) -> B): Applicative<C, B> =
			ap(just(f))
}
