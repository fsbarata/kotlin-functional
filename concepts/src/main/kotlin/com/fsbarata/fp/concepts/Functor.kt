package com.fsbarata.fp.concepts

interface Functor<C, A>: Context<C, A> {
	fun <B> map(f: (A) -> B): Functor<C, B>
}
