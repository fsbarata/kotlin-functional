package com.github.fsbarata.functional.control

interface Functor<F, out A>: Context<F, A> {
	fun <B> map(f: (A) -> B): Functor<F, B>
}

fun <A, B, F> lift(f: (A) -> B): (Functor<F, A>) -> Functor<F, B> = { it.map(f) }
