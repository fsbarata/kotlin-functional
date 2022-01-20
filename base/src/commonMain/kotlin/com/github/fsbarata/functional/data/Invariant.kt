package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.Context

interface Invariant<F, out A>: Context<F, A> {
	fun <B> invmap(f: (A) -> B, g: (B) -> @UnsafeVariance A): Invariant<F, B>
}
