package com.github.fsbarata.functional.data

interface Functor<F, out A>: Invariant<F, A> {
	fun <B> map(f: (A) -> B): Functor<F, B>

	override fun <B> invmap(f: (A) -> B, g: (B) -> @UnsafeVariance A) = map(f)
}
