package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.Context

interface Invariant<F, out A>: Context<F, A> {
	fun <B> invmap(f: (A) -> B, g: (B) -> @UnsafeVariance A): Invariant<F, B>

	interface Scope<F> {
		fun <A, B> invmap(ca: Context<F, A>, f: (A) -> B, g: (B) -> A): Context<F, B> =
			(ca as Invariant<F, A>).invmap(f, g)
	}
}
