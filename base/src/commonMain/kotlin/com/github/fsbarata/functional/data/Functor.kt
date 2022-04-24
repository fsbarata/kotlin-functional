package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.Context

interface Functor<F, out A>: Invariant<F, A> {
	val scope: Scope<F>

	fun <B> map(f: (A) -> B): Functor<F, B>

	override fun <B> invmap(f: (A) -> B, g: (B) -> @UnsafeVariance A): Functor<F, B> = map(f)

	interface Scope<F> {
		fun <A, B> map(ca: Context<F, A>, f: (A) -> B): Context<F, B> =
			(ca as Functor<F, A>).map(f)
	}
}
