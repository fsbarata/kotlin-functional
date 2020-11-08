package com.github.fsbarata.functional.control

interface Functor<F, out A>: Context<F, A> {
	fun <B> map(f: (A) -> B): Functor<F, B>
}
