package com.github.fsbarata.functional.control

interface Functor<C, out A>: Context<C, A> {
	fun <B> map(f: (A) -> B): Functor<C, B>
}

fun <A, B, C> lift(f: (A) -> B): (Functor<C, A>) -> Functor<C, B> = { it.map(f) }