package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context

interface Alternative<F, out A>: Applicative<F, A> {
	override val scope: Scope<F>

	override fun <B, R> lift2(fb: Applicative<F, B>, f: (A, B) -> R) =
		super.lift2(fb, f) as Alternative<F, R>

	fun associateWith(other: Context<F, @UnsafeVariance A>): Alternative<F, A>

	interface Scope<F>: Applicative.Scope<F> {
		fun <A> empty(): Alternative<F, A>
	}
}

fun <C, A> associate(alt1: Alternative<C, A>, alt2: Context<C, A>) =
	alt1.associateWith(alt2)
