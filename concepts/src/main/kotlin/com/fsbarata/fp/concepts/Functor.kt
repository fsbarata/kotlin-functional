package com.fsbarata.fp.concepts

interface Functor<C, A>: Context<C, A> {
	fun <B> map(f: (A) -> B): Functor<C, B>
}

fun <A, B, C> lift(f: (A) -> B): (Functor<C, A>) -> Functor<C, B> = { it.map(f) }
